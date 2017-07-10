package com.alamicompany.collect;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import java.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.bumptech.glide.Glide;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wdullaer.materialdatetimepicker.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, Toolbar.OnMenuItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {


    private Boolean dataLoaded;
    private MultiAutoCompleteTextView hashtag;
    private Toolbar mToolbar;
    private ImageView profileImage;
    private TextView slogan;
    private TextView textViewUserEmail;
    public static String userId;
    private String projectId;
    private DatabaseReference databaseUsers;
    private FirebaseDatabase mFirebaseInstance;
    private Intent intentPost;
    private TextView searchText;
    private ProgressBar progressMain;
    private ProgressDialog dialog;
    private FirebaseStorage storage;
    private DatabaseReference databasePosts;
    ArrayList<String> postsNames;
    User currentUser;
    String currentUserName;
    private String currentUserSlogan;
    String currentUserPhoto;
    Bundle bundle;
    FirebaseUser user;
    private LoadAsyncData loadData;
    private FirebaseAuth firebaseAuth;
    private Uri mCropImageUri;
    String postname;
    Calendar calendar ;
    DatePickerDialog datePickerDialog ;
    int Year, Month, Day ;
    static final  int REQUEST_IMAGE_CAPTURE = 1;
    static final  int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    static final int REQUEST_INVITE = 1;
    //viewpager
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static final Integer[] XMEN= {R.drawable.collect_logo,R.drawable.profile1,R.drawable.search_background,R.drawable.collect_logo,R.drawable.profile};
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();
    private ArrayList<Post> postsPhotos = new ArrayList<Post>();
    //bottom Navigation
    private BottomNavigationView bottomNavigationView;
    private LruCache<String, Bitmap> mMemoryCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // bottomNavigationView.setOnNavigationItemSelectedListener(this);
        dataLoaded = false;
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databasePosts = FirebaseDatabase.getInstance().getReference("posts").child(userId);
        postsNames = new ArrayList<String>();
        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        byte[] data = new byte[10000];

        searchText = (TextView) findViewById(R.id.searchText);
        progressMain = (ProgressBar) findViewById(R.id.progressMain);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        slogan = (TextView)findViewById(R.id.userSlogan);
        profileImage.setOnClickListener(this);
        searchText.setOnClickListener(this);
        intentPost = new Intent(this,PostActivity.class);
        bundle = new Bundle();
        hashtag = (MultiAutoCompleteTextView)findViewById(R.id.edit_text_hashtag);
        hashtag.setThreshold(1);
        hashtag.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        //startAsynncTask();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar);
        toolbar.setOnMenuItemClickListener(this);
        //take new post

        //AH BottomNavigation
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        //memory cache
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

    }
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!dataLoaded){
            startAsynncTask();
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);


    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void startAsynncTask(){

        loadData = new LoadAsyncData(MainActivity.this);
        loadData.execute();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
        //noinspection SimplifiableIfStatement

            case R.id.action_logout:
                logoutUser();
                break;
            case R.id.action_tellfriend:
                inviteFriends();
                break;




        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            //noinspection SimplifiableIfStatement


            case R.id.action_logout:
                logoutUser();
                break;
            case R.id.action_tellfriend:
                inviteFriends();
                break;
            case R.id.action_Help:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.namePost:
                bundle.putString("search","name");
                intentPost.putExtras(bundle);
                startActivity(intentPost);
                break;

            case R.id.searchText:
                /*if (!valideTag(hashtag.getText().toString())){

                   break;
                }*/
                hashtag.setError(null);
                bundle.putString("search","tags");
                bundle.putString("hashtag",hashtag.getText().toString());
                intentPost.putExtras(bundle);
                startActivity(intentPost);
                break;
            case  R.id.profileImage:
                onSelectImageClick(v);
                break;


        }
        }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.bottombaritem_today:
                bundle.putString("search","today");
                intentPost.putExtras(bundle);
                startActivity(intentPost);
                return true;
            case R.id.bottombaritem_calendar:
                showCalendar();
                return true;
            case R.id.bottombaritem_camera:
                Intent intentProjectCamera = new Intent(MainActivity.this,ProjectActivity.class);
                Bundle projectBundleCamera = new Bundle();
                projectBundleCamera.putString("action", "camera");
                intentProjectCamera.putExtras(projectBundleCamera);
                startActivity(intentProjectCamera);
                return true;
            case R.id.bottombaritem_project:
                Intent intentProject = new Intent(this,ProjectActivity.class);
                String userN = user.getUid();
                Bundle projectBundle = new Bundle();
                projectBundle.putString("action", "project");
                intentProject.putExtras(projectBundle);
                startActivity(intentProject);
                return true;
            case R.id.bottombaritem_favorit:
                bundle.putString("search","favorit");
                intentPost.putExtras(bundle);
                startActivity(intentPost);
                return true;
        }
        return false;
    }



    public void logoutUser(){
        firebaseAuth.signOut();
        //closing activity
        finish();
        //starting login activity
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void getUserInfo(){

        databaseUsers.orderByChild("id").equalTo(user.getUid()).limitToFirst(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User currUser = dataSnapshot.getValue(User.class);
                currentUserName = currUser.getName();
                currentUserPhoto = currUser.getPhoto();
                currentUserSlogan = currUser.getSlogan();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    public void displayData(){
        textViewUserEmail.setText(currentUserName);
        slogan.setText(currentUserSlogan);
        /*Picasso.with(MainActivity.this)
                .load(currentUserPhoto) // Your image source.
                .transform(new RoundedTransformation())
                .fit()  // Fix centerCrop issue: http://stackoverflow.com/a/20824141/1936697
                .centerCrop()
                .into(profileImage);*/

       Glide.with(this)
                .load(currentUserPhoto)
                .bitmapTransform(new CropCircleTransformation(this))
               .placeholder(R.drawable.profile)
               .error(R.drawable.profile)
               .into(profileImage);


        /*.placeholder(R.drawable.placeholder)
                .error(R.drawable.imagenotfound)
                .override(200, 200);
        .centerCrop();*/

        ArrayAdapter<String> adp=new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,postsNames);


        hashtag.setAdapter(adp);


    }

    public void createUserInfo (){
        String userName;
        user.getProviderData();

        userId = user.getUid();


        databaseUsers.child(userId).setValue(user);
        //addUserChangeListener();
    }

    public void showProgressBar(){
        /*progressMain.setVisibility(View.VISIBLE);
        progressMain.setIndeterminate(true);*/
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Loading data...");
        dialog.show();

        dialog.setCancelable(false);
        dialog.show();
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(dialog, "progress", 10000, 0);
    }
    public void hideProgressBar(){
        //progressMain.setVisibility(View.GONE);
        dialog.hide();
        dataLoaded = true;
    }

//Profile Image picture set

    public void onSelectImageClick(View view) {
        Intent profile = new Intent(this,ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name",currentUserName);
        bundle.putString("slogan",currentUserSlogan);
        bundle.putString("photo",currentUserPhoto);
        profile.putExtras(bundle);
        startActivity(profile);
        //CropImage.startPickImageActivity(this);
    }
    // get all users photo to show it in the view pager

    public void getPostPhotos(){
        postsPhotos.clear();
        createPost("1","Wallpaper 1","1","https://firebasestorage.googleapis.com/v0/b/collect-43683.appspot.com/o/Collect%2F1498467203158.png?alt=media&token=7b4b8fd2-3678-4390-903a-2fb80fe82559","#wallpaper");
        createPost("2","Wallpaper 2","1","https://firebasestorage.googleapis.com/v0/b/collect-43683.appspot.com/o/Collect%2F1498473836777.png?alt=media&token=1e1b7b3e-059b-4599-bc43-3fee2d3516c8","#wallpaper");
        createPost("3","Wallpaper 3","1","https://firebasestorage.googleapis.com/v0/b/collect-43683.appspot.com/o/Collect%2F1498473330958.png?alt=media&token=dae1de35-8547-4739-930f-10dc7489d5e1","#wallpaper");
        createPost("4","Wallpaper 4","1","https://firebasestorage.googleapis.com/v0/b/collect-43683.appspot.com/o/Collect%2Fprofile%2F1498479927810.png?alt=media&token=411df48a-66e3-4ef7-a589-40e3ef6d22aa","#wallpaper");
        databasePosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    postsPhotos.add(post);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void createPost(String id,String postName ,String idProj, String photo, String description) {


        String date = getDateTime();
        Post post = new Post(id, postName, photo, date, false, idProj, description);
        databasePosts.child(id).setValue(post);

        //destroyFragment(addPostFrag);

    }
    public void getPostNames() {
        //posts.clear();
        databasePosts.orderByChild("name").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post searchedPost = dataSnapshot.getValue(Post.class);
                postname = searchedPost.getName();
                postsNames.add(postname);
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
    public void getPostTags(){
        postsNames.clear();
        databasePosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Post post = postSnapshot.getValue(Post.class);
                    addTagsToSearchList(post.getDescription());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void addTagsToSearchList(String description){
        String [] tags = description.split("#");
        for (int i = 0; i < tags.length; i++ ){
            postsNames.add(tags[i]);
        }
    }
    public String [] getSelectedTags(String tags){
        String newTags = tags.trim();
        String [] tagsList = newTags.split(",");
        return tagsList;

    }
    public boolean valideTag(String tag){

        if (tag.isEmpty()){
            hashtag.setError("no Tag entred");
            return false;
        }
        for (String item: postsNames){
            if (item.equals(tag))
                return true;

        }
        hashtag.setError("this tag is never used");
        return false;

    }


    public void showCalendar(){
        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);



        datePickerDialog = DatePickerDialog.newInstance(MainActivity.this, Year, Month, Day);

        datePickerDialog.setThemeDark(true);

        datePickerDialog.showYearPickerFirst(false);

        datePickerDialog.setAccentColor(Color.parseColor("#6e6e6e"));

        datePickerDialog.setTitle("Select Date From DatePickerDialog");

        datePickerDialog.show(getFragmentManager(), "DatePickerDialog");



    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date;
        int month = monthOfYear+1;
        if (dayOfMonth < 10){
            if (monthOfYear<10){
                date = "0"+dayOfMonth + "/0" + month + "/" + year +" ";
            }else
            date = "0"+dayOfMonth + "/" + month + "/" + year + " ";
        }else
            if (dayOfMonth>=10 && monthOfYear<10){
                date = dayOfMonth + "/0" + month + "/" + year + " ";
            }
            else
        date = dayOfMonth + "/" + month + "/" + year + " ";


        Toast.makeText(MainActivity.this, date, Toast.LENGTH_LONG).show();
        bundle.putString("search","calendar");
        bundle.putString("date",date);
        intentPost.putExtras(bundle);
        startActivity(intentPost);
    }

    private String getDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyy ");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    private void inviteFriends() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
               // .setCustomImage(Uri.parse("https://firebasestorage.googleapis.com/v0/b/collect-43683.appspot.com/o/Collect%2Fprofile%2Fprofile.jpg?alt=media&token=28124e9e-778e-4d4c-a7ce-192d43900bc2" ))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Invites", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d("onActivityResult", "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        }
    }

    public void init() {
        /*for(int i=0;i<XMEN.length;i++)
            XMENArray.add(XMEN[i]);*/

        mPager = (ViewPager) findViewById(R.id.pager);
        //mPager.setAdapter(new ViewPagerAdapter(MainActivity.this,XMENArray));
        mPager.setAdapter(new ViewPagerAdapter(MainActivity.this,postsPhotos));
        //CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        //indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == postsPhotos.size()) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);

            }
        };

        mPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {

              /* final float normalizedposition = Math.abs(Math.abs(position) - 1);
                page.setAlpha(normalizedposition);*/

                final float normalizedposition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedposition / 2 + 0.5f);
                page.setScaleY(normalizedposition / 2 + 0.5f);

                //page.setRotationY(position * -30);


            }
        });
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    public void showMainDisplay(String photo){
        FragmentManager fm = getFragmentManager();
        MainDisplayPhotoDialog dialog = new MainDisplayPhotoDialog();
        Bundle args = new Bundle();
        args.putString("photo",photo);
        dialog.setArguments(args);
        dialog.show(fm,"display");
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Toast.makeText(this,message,Toast.LENGTH_LONG).show();


    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
