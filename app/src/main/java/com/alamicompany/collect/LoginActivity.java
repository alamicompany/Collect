package com.alamicompany.collect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private EditText signinEmail;
    private EditText signinPass;
    private Button signinButton;
    private TextView signinText;
    private ProgressDialog progressDialog;
    private TextView forgetPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        signinEmail = (EditText) findViewById(R.id.signinEmail);
        signinPass = (EditText) findViewById(R.id.signinPassword);
        signinButton = (Button) findViewById(R.id.signinButton);
        signinText = (TextView) findViewById(R.id.signinText);
        forgetPassword = (TextView) findViewById(R.id.forgetPasswordText);
        progressDialog = new ProgressDialog(this);
        forgetPassword.setOnClickListener(this);
        signinButton.setOnClickListener(this);
        signinText.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.signinButton:
                userLogin();
                break;
            case R.id.signinText:
                finish();
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.forgetPasswordText:
                Intent intent = new Intent(this, ResetPasswordActivity.class);
                startActivity(intent);
        }
    }

        private void userLogin(){

            String email = signinEmail.getText().toString().trim();
            String password  = signinPass.getText().toString().trim();
            if (!validate(email,password)) {
                onLoginFailed();
                return;
            }
            signinButton.setEnabled(false);
            progressDialog.setMessage("Registering Please Wait...");
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            //if the task is successfull
                            signinButton.setEnabled(true);
                            if(task.isSuccessful()){
                                //start the profile activity

                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        }
                    });
        }
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        signinButton.setEnabled(true);
    }





    public boolean validate(String email,String password) {
        boolean valid = true;



        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signinEmail.setError("enter a valid email address");
            valid = false;
        } else {
            signinEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            signinPass.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            signinPass.setError(null);
        }

        return valid;
    }




}
