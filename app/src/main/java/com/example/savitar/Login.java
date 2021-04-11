package com.example.savitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "Login";
    EditText mEmail,mPassword;
    Button mLoginBtn;
    TextView mCreateBtn,forgotTextLink;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.createText);
        forgotTextLink = findViewById(R.id.forgotPassword);

        forgotTextLink.setOnClickListener(this);
        mCreateBtn.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Login onStart");
        validateCurrentUser();
    }

    @Override
    public void onClick(View v){
        int viewId = v.getId();
        if(viewId == R.id.createText){
            startActivity(new Intent(getApplicationContext(),Register.class));
        }else if(viewId == R.id.loginBtn){
            login();
        }else if(viewId == R.id.forgotPassword){
            forgotPassword();
        }
    }

    private void login(){
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            mEmail.setError("Email is Required.");
            return;
        }

        if(TextUtils.isEmpty(password)){
            mPassword.setError("Password is Required.");
            return;
        }

        if(password.length() < 6){
            mPassword.setError("Password Must be >= 6 Characters");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        //Check on DB if customer exists. If not, don´t even try to sign in

        // Authenticate the user
        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                progressBar.setVisibility(View.GONE);
                if(fAuth.getCurrentUser().isEmailVerified()){
                    Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                }else{
                    Intent i = new Intent(Login.this, ConfirmEmail.class);
                    i.putExtra("tittleMessage", "Email Validation Pending");
                    i.putExtra("emailAddress" , fAuth.getCurrentUser().getEmail());
                    startActivity(i);
                }

            }else {
                Toast.makeText(Login.this, "Error!!! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG , task.getException().getMessage());
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    private void validateCurrentUser(){
        if(fAuth.getCurrentUser() != null){
            if(fAuth.getCurrentUser().isEmailVerified()){
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }else{
                Log.d(TAG, "User exists, email not verified, keep in Login Activity");
            }
        }else{
            Log.d(TAG, "User not logged in, keep in Login Activity");
        }
    }

    private void forgotPassword(){
        final EditText resetMail = new EditText(Login.this);
        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(Login.this);
        passwordResetDialog.setTitle("Reset Password ?");
        passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
        passwordResetDialog.setView(resetMail);

        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // extract the email and send reset link
                String mail = resetMail.getText().toString();
                fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Login.this, "Reset link sent to your email.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Error! Reset link was not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close the dialog
            }
        });

        passwordResetDialog.create().show();


    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}