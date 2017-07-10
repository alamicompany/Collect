package com.alamicompany.collect;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText resetEmail;
    private Button resetPassword,cancel;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        resetEmail = (EditText) findViewById(R.id.reset_email);
        resetPassword = (Button) findViewById(R.id.reset_password);
        cancel = (Button) findViewById(R.id.btn_cancel);
        progressBar = (ProgressBar)findViewById(R.id.reset_password_progress_bar);
        resetPassword.setOnClickListener(this);
        cancel.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.reset_password:
                resetPassword();
                break;
        }

    }

    public  void resetPassword (){

        email = resetEmail.getText().toString().trim();

        if (!validate(email)) {
            return;
        }
        resetPassword.setEnabled(false);

        progressBar.setVisibility(View.VISIBLE);

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                } else {
                    resetPassword.setEnabled(true);
                    Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.GONE);
            }
        });
    }
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Reset Password failed", Toast.LENGTH_LONG).show();

        resetPassword.setEnabled(true);
    }


    public boolean validate(String email) {
        boolean valid = true;


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            resetEmail.setError("enter a valid email address");
            valid = false;
        } else {
            resetEmail.setError(null);
        }



        return valid;
    }
}
