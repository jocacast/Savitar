package com.example.savitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfirmEmail extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "ConfirmEmail";
    TextView mTitle, mSubtitle;
    Button sendCode, login;
    FirebaseAuth fAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);

        String title = getIntent().getStringExtra("tittleMessage");
        String emailAddress = getIntent().getStringExtra("emailAddress");
        String message = "Please click on the link sent to " + emailAddress + " and then Login";

        mTitle = findViewById(R.id.textView_title);
        mSubtitle = findViewById(R.id.textView2);
        sendCode = findViewById(R.id.sendCodeBtn);
        login = findViewById(R.id.loginBtn);
        sendCode.setOnClickListener(this);
        login.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();

        mTitle.setText(title);
        mSubtitle.setText(message);
        user = fAuth.getCurrentUser();
    }

    @Override
    public void onClick(View v){
        int viewId = v.getId();
        if(viewId == R.id.sendCodeBtn){
            sendEmail();
        }else if(viewId == R.id.loginBtn){
            loginIntent();
        }
    }

    public void sendEmail(){
        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ConfirmEmail.this, "Verification Email has been sent.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("tag", "onFailure: Email not sent " + e.getMessage());
            }
        });
    }
    public void loginIntent(){
        Intent i = new Intent(ConfirmEmail.this, Login.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG , "onBackPressed Called");
        startActivity(new Intent(getApplicationContext(),Login.class));
    }

}