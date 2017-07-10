package com.alamicompany.collect;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class PostActivity extends AppCompatActivity implements Add_post_Fragment.OnNewPostAdded , UpdatePostFragment.OnPostNameUpdated, DialogTakeAction.OnPostActionTake, DisplayPostDialog.displayPostAction, DeleteDialog.OnDeletePostConfirmation{


    private GridLayoutManager lLayout;
    private PostRecyclerViewAdapter rcAdapter;
    private DatabaseReference databasePosts;
    private ProgressBar progressLoad;
    private TextView textTest;
    private String userId;
    private String projectId;
    private String searchType;
    private String hashtag;
    private Bundle bundle;
    private ArrayList<Post> posts;
    private ArrayList<Post> postsByProject;
    private ArrayList<String> projects;
    private PostFragmentLoad postFragmentLoadTask;
    public Load_Post_Fragment load;
    public Add_post_Fragment addPostFrag;
    public Intent intentProject;
    public Post postUpdated;
    private FirebaseStorage storage;

    static final  int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    static final int REQUEST_IMAGE_CAPTURE = 0;
    static final int SELECT_FILE = 1;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post);
        progressLoad = (ProgressBar) findViewById(R.id.progressPost);
        textTest = (TextView) findViewById(R.id.textTest);
        posts = new ArrayList<Post>();
        System.out.println("on create again called");
        System.out.println(projects);
        userId= MainActivity.userId;
        databasePosts = FirebaseDatabase.getInstance().getReference("posts").child(userId);
        Intent intent = getIntent();
        bundle = intent.getExtras();
        searchType = bundle.getString("search");
       // projects = ProjectActivity.getActivityinstance().getProjectsNames();
        intentProject = new Intent(this, ProjectActivity.class);
        load = new Load_Post_Fragment();
        addPostFrag = new Add_post_Fragment();
        startAsynncTask();
        storage = FirebaseStorage.getInstance();
        FloatingActionButton fabAddPhoto = (FloatingActionButton) findViewById(R.id.fabAddPost);
        fabAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGalery();
            }
        });





    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void startAsynncTask(){

        postFragmentLoadTask = new PostFragmentLoad(this);
        postFragmentLoadTask.execute();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent main = new Intent(PostActivity.this, MainActivity.class);
        startActivity(main);
    }

    public ArrayList<String> getProjectsNames(){
        return this.projects;
    }
    public ArrayList<Post>  getPosts (){

        return this.posts;

    }

    public void displayFragment(){

        if(searchType.equals("camera")){
            takePicture();

            //loadFragment(new Add_post_Fragment());

        }else
        loadFragment(load);

    }

    public void loadFragment(Fragment fragment) {

        // create a FragmentManager
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragTransaction.replace(R.id.frameLayout, fragment);
        fragTransaction.commit();// save the changes
    }
    public void destroyFragment(Fragment fragment){


    }




    @Override
    public void onNewPostAdded(String name, String photo, String description,String note, Long storageId) {

        createPost(name,photo,description,note,storageId);
    }

    private String getDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyy ");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public void createPost(String postName , String photo, String description,String note, Long storageId) {

        String date = getDateTime();
        String postID = databasePosts.push().getKey();
        Post post = new Post(postID, postName, photo, date, false, projectId, description,note,storageId);
        databasePosts.child(postID).setValue(post);
        refreshPostList();
        loadFragment(load);
        //destroyFragment(addPostFrag);

    }
    public Boolean updatePostName(Post post,String name, String description,String note){

        Post updatedPost = new Post();
        updatedPost.setId(post.getId());
        updatedPost.setName(name);
        updatedPost.setFavorit(post.getFavorit());
        updatedPost.setDate(post.getDate());
        updatedPost.setPhoto(post.getPhoto());
        updatedPost.setProject_id(post.getProject_id());
        updatedPost.setDescription(description);
        updatedPost.setNote(note);
        databasePosts.child(post.getId()).setValue(updatedPost);
        refreshPostList();
        return true;
    }
    public void addFavoritPost(Post post,boolean favorit){

        Post favoritPost = new Post();
        favoritPost.setId(post.getId());
        favoritPost.setName(post.getName());
        favoritPost.setFavorit(favorit);
        favoritPost.setDate(post.getDate());
        favoritPost.setPhoto(post.getPhoto());
        favoritPost.setProject_id(post.getProject_id());
        favoritPost.setDescription(post.getDescription());
        databasePosts.child(post.getId()).setValue(favoritPost);
        refreshPostList();


    }

    public void deletePost(Post post){
        databasePosts.child(post.getId()).removeValue();
        refreshPostList();

        String path = "Collect/" + post.getStorageId()+ ".png";
        StorageReference collectRef = storage.getReference(path);
        collectRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            Toast.makeText(PostActivity.this,"Post Deleted",Toast.LENGTH_LONG).show();
                        }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

      public void loadPosts(){

        switch (this.searchType){

            case "project":
                projectId = bundle.getString("project_id");
                searchByProject(projectId);
                break;
            case "camera":
                projectId = bundle.getString("project_id");
                searchByProject(projectId);
            case "today":
                searchByDate(getDateTime());
                break;
            case "calendar":
                String date = bundle.getString("date");
                searchByDate(date);
                break;
            case "name":
                hashtag = bundle.getString("hashtag");
                searchPostByName(hashtag);
                break;
            case "tags":
                hashtag = bundle.getString("hashtag");
                searchByTags(hashtag);
                break;

            case "favorit":
                searchByFavorits(true);
                break;
            default: break;
        }
    }


    public void searchPostByName(String name) {
        //posts.clear();
        //databasePosts.orderByChild("name").equalTo(name).addChildEventListener(new ChildEventListener() {
        databasePosts.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post searchedPost = dataSnapshot.getValue(Post.class);
                posts.add(searchedPost);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }
        });
    }

    public void searchByTags(final String tags){


        databasePosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Post post = postSnapshot.getValue(Post.class);
                    if (checkTagList(post.getDescription(),tags)){
                        posts.add(post);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean checkTags(String description, String tag){

        String [] tags = description.split("#");

        for (int i = 0; i < tags.length;i++){

            if (tags[i].equals(tag) ) {
                return true;
            }


        }
        return false;


    }
    boolean checkTagList(String postDescription, String tag){

        String newTag = tag.replaceAll("\\s", "");
        String newDescription = postDescription.replaceAll("\\s", "");

        String [] tags = newTag.split(",");
        String [] descriptions = newDescription.split("#");
        int somme = 0;
        for (int i=0; i<= tags.length-1;i++) {
            for (int j = 0; j<= descriptions.length-1;j++) {
                if (tags[i].equals(descriptions[j])){
                    somme++;
                    break;
                }

            }

        }
        if (somme == tags.length)
            return true;
        return false;


    }


        public void searchByDate(String date){
            //posts.clear();
            databasePosts.orderByChild("date").equalTo(date).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Post searchedPost = dataSnapshot.getValue(Post.class);
                    posts.add(searchedPost);
                    //System.out.println("the post id is" + searchedPost.getId());
                    //Log.i("search post by date", searchedPost.getId());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

            });
        }

        public void searchByFavorits(Boolean favorit){

            databasePosts.orderByChild("favorit").equalTo(favorit).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Post searchedPost = dataSnapshot.getValue(Post.class);
                    posts.add(searchedPost);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

            });
        }

        public void searchByProject(String project_id){
            posts.clear();
            databasePosts.orderByChild("project_id").equalTo(project_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    Post searchedPost = dataSnapshot.getValue(Post.class);
                    posts.add(searchedPost);
                    //System.out.println("the post id is" + posts);
                    //Log.i("search post by project", searchedPost.getId());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Post searchedPost = dataSnapshot.getValue(Post.class);
                    posts.remove(searchedPost);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                }

            });
        }



        //Progress Bar
    public void showProgressBar(){
        progressLoad.setVisibility(View.VISIBLE);
        progressLoad.setIndeterminate(true);
    }

    public void showDialogListAction(Post post){
        FragmentManager fm = getFragmentManager();
        DialogTakeAction dialogAction = new DialogTakeAction();
        Bundle args = new Bundle();
        args.putString("target", "post");
        args.putParcelable("post",post);
        dialogAction.setArguments(args);
        dialogAction.show(fm,"Take Action");
    }


    public void showUpdatePost(Post post){
        FragmentManager fm = getFragmentManager();
       UpdatePostFragment dialogPost = new UpdatePostFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("post",post);
        dialogPost.setArguments(bundle);
        dialogPost.show(fm, "Alert Dialog fragment");
    }
    public void showDialogDeletePost(Post post){
        FragmentManager fm = getFragmentManager();
        DeleteDialog deletePostDialog = new DeleteDialog();
        Bundle args = new Bundle();
        args.putString("action","post");
        args.putParcelable("post",post);
        deletePostDialog.setArguments(args);
        deletePostDialog.show(fm,"Delete post");
    }

    public void showPostDialog(Post post){
        FragmentManager fm = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putParcelable("post",post);
        DisplayPostDialog displayPost = new DisplayPostDialog();
        displayPost.setArguments(bundle);
        displayPost.show(fm, "Alert Dialog fragment");
    }

    public void hideProgressBar(){
        progressLoad.setVisibility(View.GONE);
    }

    protected boolean isTaskRunning(PostFragmentLoad task) {

        if(task==null ) {

            return false;

        } else if(task.getStatus() == PostFragmentLoad.Status.FINISHED){

            return false;

        } else {

            return true;

        }
    }
    public void onResult(){
        textTest.setText("Yes Worked fine");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onPostNameUpdated(Post post, String name,String description, String note) {

        updatePostName(post, name, description, note);
    }

    public void refreshPostList(){
        databasePosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()){
                    Post searchedPost = postsnapshot.getValue(Post.class);
                    posts.add(searchedPost);
                }
                load.refreshPosts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
    });

    }

    public void takePicture() {

        /*if (ActivityCompat.checkSelfPermission(PostActivity.this,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(PostActivity.this,
                    android.Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(PostActivity.this,
                        new String[]{android.Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }

        }else
        camera();*/
        boolean result=Utility.checkCameraPermission(PostActivity.this);
        if (result)
            camera();


    }
    public void camera() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentCamera.resolveActivity(PostActivity.this.getPackageManager()) != null) {
            startActivityForResult(intentCamera, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void selectImageFromGalery(){
        boolean result=Utility.checkPermission(PostActivity.this);
        if (result)
            galleryIntent();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }else
                if (requestCode == REQUEST_IMAGE_CAPTURE){


            Bundle extras = data.getExtras();
                extras.putString("action","Camera");
            addPostFrag.setArguments(extras);
            loadFragment(addPostFrag);
            }
            //imagePost.setImageBitmap(imageBitmap);

        }
    }
    private void onSelectFromGalleryResult(Intent data) {

        Bundle extras = new Bundle();
        //Bitmap imageBitmap = (Bitmap) extras.get("data");
        extras.putString("action","SelectFile");
        extras.putString("uri",data.getData().toString());
        addPostFrag.setArguments(extras);
        loadFragment(addPostFrag);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    camera();

                }
                break;


            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntent();
                    break;


                    // other 'case' lines to check for other
                    // permissions this app might request
                }
        }
    }

    @Override
    public void onPostActionDelete(Post post) {
        //deletePost(idPost);
        showDialogDeletePost(post);
    }

    @Override
    public void onPostActionUpdate(Post post) {
    showUpdatePost(post);
    }

    @Override
    public void addPostToFavorit(Post post, boolean favorit) {
        addFavoritPost(post,favorit);
    }

    @Override
    public void onDeletePostConfirm(Post post) {
        deletePost(post);
    }
}


