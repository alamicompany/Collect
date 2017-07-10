package com.alamicompany.collect;

import android.*;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by AC04 on 27.03.17.
 */

public class Add_post_Fragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{


    private ImageView imagePost;
    private ProgressBar progressUpload;
    private Button uploadPost;
    private EditText namePost;
    private EditText description;
    private EditText note;
    private String action;
    private Bitmap imageBitmap;
    private FirebaseStorage storage;

    byte[] data;
    Activity activity;
    //private ArrayList<String> projects;
    private Spinner spinProject;
    static final  int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       // takePicture();
        View addPostFragment = inflater.inflate(R.layout.add_post_fragment, container, false);

        //projects = ((PostActivity) activity).getProjectsNames();
        storage = FirebaseStorage.getInstance();
        imagePost = (ImageView) addPostFragment.findViewById(R.id.imagePost);
        description = (EditText)addPostFragment.findViewById(R.id.descriptionPost);
         note = (EditText) addPostFragment.findViewById(R.id.post_note);
        Bundle extras = getArguments();
        action = extras.getString("action");
        switch (action){
            case "Camera":
                imageBitmap = (Bitmap) extras.get("data");
                break;
            case "SelectFile":
                String url = extras.get("uri").toString();
                Uri uri = Uri.parse(url);
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(),uri );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

        imagePost.setImageBitmap(imageBitmap);
        namePost = (EditText) addPostFragment.findViewById(R.id.namePost);
        uploadPost = (Button) addPostFragment.findViewById(R.id.uploadPost);
        progressUpload = (ProgressBar) addPostFragment.findViewById(R.id.progressUpload);
        uploadPost.setOnClickListener(this);
        uploadPost.setEnabled(true);
        return addPostFragment;
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        this.activity = context;
    }

    @Override
    public void onPause() {
        super.onPause();
        namePost.setText("");
        description.setText("");
        note.setText("");
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()) {
             case R.id.uploadPost:
                 data = compress();
                 upload(data);
                 break;
        }
        
    }





    public byte[] compress (){
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imagePost.getDrawable());
        Bitmap bitmap = bitmapDrawable .getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.PNG, 100 ,baos);
        bitmap.compress(Bitmap.CompressFormat.JPEG,40,baos);
        byte [] data = baos.toByteArray();

        return data;

    }
    public void upload(byte [] data){
        final Long storageID = System.currentTimeMillis();
        String path = "Collect/" + storageID+ ".png";
        StorageReference collectRef = storage.getReference(path);
        StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("text", "hashtagimage").build();
        progressUpload.setVisibility(View.VISIBLE);
        uploadPost.setEnabled(false);
        UploadTask uploadTask = collectRef.putBytes(data,metadata);
        uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressUpload.setVisibility(View.INVISIBLE);
                //uploadPost.setEnabled(true);

                String photo = taskSnapshot.getDownloadUrl().toString();
                String name  = namePost.getText().toString();
                String tags =  description.getText().toString();
                String postDecription = note.getText().toString();
                if(validePost(name, tags,postDecription)){
                try{
                    ((OnNewPostAdded) activity).onNewPostAdded(name,photo, tags,postDecription,storageID);
                    uploadPost.setEnabled(true);

                }catch (ClassCastException cce){

                }}
                   // uploadPost.setEnabled(false);

            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public boolean validePost(String name, String tags, String postDescription){
        boolean valid = true;
            if (name.isEmpty()|| name.length() < 3) {
                namePost.setError("at least 3 characters");
                valid= false;
            }else
                namePost.setError(null);

        if (tags.isEmpty()|| !tags.contains("#")) {
            description.setError("at least add a # ");
            valid = false;
        }else
            description.setError(null);
             return valid;
        }


    public interface OnNewPostAdded{
        public void onNewPostAdded(String name, String Photo, String description,String note,Long storageId);
    }

}
