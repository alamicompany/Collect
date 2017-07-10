package com.alamicompany.collect;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth firebaseAuth;
    private EditText signName;
    private EditText signEmail;
    private EditText signPass;
    private Button signButton;
    private TextView signupText;
    private  String email;
    private String password;
    private String userName;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseUsers;
    private Uri mCropImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        signName = (EditText) findViewById(R.id.signName);
        signEmail = (EditText) findViewById(R.id.signEmail);
        signPass = (EditText) findViewById(R.id.signPass);
        signButton = (Button) findViewById(R.id.signButton);
        signupText = (TextView) findViewById(R.id.signupText);
        progressDialog = new ProgressDialog(this);
        signButton.setOnClickListener(this);
        signupText.setOnClickListener(this);
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.signButton:
                registerUser();
                break;
            case R.id.signupText:
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                break;

        }
    }



    private void registerUser(){
         userName = signName.getText().toString().trim();
         email = signEmail.getText().toString().trim();
         password = signPass.getText().toString().trim();

        if (!validate(userName,email,password)) {
            onSignupFailed();
            return;
        }

        signButton.setEnabled(false);

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            //display some message here
                            signButton.setEnabled(false);
                            Toast.makeText(SignUpActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                            FirebaseUser user = task.getResult().getUser();
                            createUser(user.getUid());
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            }
                        else{
                            //display some message here
                            Toast.makeText(SignUpActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();

                    }
                });

    }

    public void createUser (String id){

        //getting a unique id using push().getKey() method
        //it will create a unique id and we will use it as the Primary Key for our user
       // userId = databaseUsers.push().getKey();
        String userName = signName.getText().toString();
        String userPhoto = "https://firebasestorage.googleapis.com/v0/b/collect-43683.appspot.com/o/Collect%2Fprofile%2Fprofile.jpg?alt=media&token=28124e9e-778e-4d4c-a7ce-192d43900bc2";
        String userSlogan = "This is my standard Slogan";
        User user = new User(id,userName,userPhoto,userSlogan);
        databaseUsers.child(id).setValue(user);
        //addUserChangeListener();
    }


    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();

        signButton.setEnabled(true);
    }


    public boolean validate(String userName, String email, String Password) {
        boolean valid = true;



        if (userName.isEmpty() || userName.length() < 3) {
            signName.setError("at least 3 characters");
            valid = false;
        } else {
            signName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signEmail.setError("enter a valid email address");

            valid = false;
        } else {
            signEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            signPass.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            signPass.setError(null);
        }
        return valid;
    }





}
