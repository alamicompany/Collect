package com.alamicompany.collect;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    EditText userName;
    ImageButton profileImage;
    private Uri mCropImageUri;
    private FirebaseStorage storage;
    private DatabaseReference databaseUsers;
    private FirebaseAuth firebaseAuth;
    private EditText userProfileSlogan;
    FirebaseUser user;
    User currentUser;
    private String name;
    private String photo;
    String photoUpdated;
    private String slogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Firebase
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userProfileSlogan = (EditText) findViewById(R.id.user_profile_slogan);
        Bundle bundle = getIntent().getExtras();
         name = bundle.getString("name");
        photo = bundle.getString("photo");
        slogan = bundle.getString("slogan");
        userName = (EditText) findViewById(R.id.user_profile_name);
        profileImage = (ImageButton)findViewById(R.id.profileImageButton);
        profileImage.setOnClickListener(this);
        Uri myPhoto = Uri.parse(photo);
        Glide.with(this)
                .load(myPhoto)
                .bitmapTransform(new CropCircleTransformation(this))
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .into(profileImage);
        userName.setText(name);
        userProfileSlogan.setText(slogan);
        FloatingActionButton updateProfileFloat = (FloatingActionButton) findViewById(R.id.profile_update_float);
        updateProfileFloat.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profileImageButton:
                CropImage.startPickImageActivity(this);
                break;
            case R.id.profile_update_float:
                updateUser(userName.getText().toString(),photo,userProfileSlogan.getText().toString());
                Intent main = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(main);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                // profileImage.setImageURI(result.getUri());
               /* Picasso.with(ProfileActivity.this)
                        .load(result.getUri()) // Your image source.
                        .transform(new RoundedTransformation())
                        .fit()  // Fix centerCrop issue: http://stackoverflow.com/a/20824141/1936697
                        .centerCrop()
                        .into(profileImage);*/
                Glide.with(this)
                        .load(result.getUri())
                        .bitmapTransform(new CropCircleTransformation(this))
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .into(profileImage);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    uploadProfileImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity

            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }
    public void uploadProfileImage(Bitmap bitmap){
        String path = "Collect/profile/" + System.currentTimeMillis()+ ".png";
        StorageReference collectRef = storage.getReference(path);
        StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("text", "hashtagimage").build();
        byte [] byteArray = bitmapToByte(bitmap);
        UploadTask uploadTask = collectRef.putBytes(byteArray);
        uploadTask.addOnSuccessListener(ProfileActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // change userPhoto link;
                //updateUser(taskSnapshot.getDownloadUrl().toString());
                photo = taskSnapshot.getDownloadUrl().toString();

            }
        });

    }
    public byte [] bitmapToByte(Bitmap bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);

        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public void updateUser(String name,String photo, String slogan){
        User updatesUser = new User(user.getUid(),name,photo,slogan);
        databaseUsers.child(user.getUid()).setValue(updatesUser);
    }


}
