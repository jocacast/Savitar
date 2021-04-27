package com.example.savitar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class GuardProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "GuardProfile";
    TextView fullName,email,phone, address;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId, cond, addressForSelectedCond;
    FirebaseUser user;
    Button showVisitorsListBtn, editProfile, changePassword;
    Host host;
    boolean isGuard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_profile);
        //Intent information
        cond = getIntent().getStringExtra("condominium");
        addressForSelectedCond = getIntent().getStringExtra("addressForSelectedCond");
        isGuard = getIntent().getBooleanExtra("isGuard", false);

        //Firebase information
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        //Rest of items
        phone = findViewById(R.id.guardPhone);
        fullName = findViewById(R.id.guardName);
        email    = findViewById(R.id.guardEmailAddress);
        address = findViewById(R.id.guardAddress);
        showVisitorsListBtn = findViewById(R.id.showVisitorListBtn);
        editProfile = findViewById(R.id.editProfile);
        changePassword = findViewById(R.id.changePass);



        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        showVisitorsListBtn.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        changePassword.setOnClickListener(this);

        Log.d(TAG, "Condominium " + cond);
        Log.d(TAG, "addressForSelectedCond " + addressForSelectedCond);

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    String dsPhone = documentSnapshot.getString("phone");
                    String dsFName = documentSnapshot.getString("fName");
                    String dsEmail = documentSnapshot.getString("email");

                    phone.setText(dsPhone);
                    fullName.setText(dsFName);
                    email.setText(dsEmail);
                    address.setText(cond);

                    host = new Host(dsFName,addressForSelectedCond,dsEmail,dsPhone);

                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.logout){
            logout();
        }else if (itemId == R.id.visitors_list){
            showVisitors();
        }else if (itemId== android.R.id.home) {
            finishActivity();
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onClick(View v){
        int viewId = v.getId();
        if(viewId == R.id.createText){
            startActivity(new Intent(getApplicationContext(),Register.class));
        }else if (viewId== R.id.showVisitorListBtn){
            Intent intent = new Intent(getBaseContext(), VisitorsList.class);
            intent.putExtra("condominium", cond);
            intent.putExtra("addressForSelectedCond", addressForSelectedCond);
            intent.putExtra("isGuard", isGuard);
            startActivity(intent);
        }else if(viewId == R.id.editProfile){
            Log.d(TAG, "Show Edit Profile View");
        }else if (viewId == R.id.changePass){
            changePass();
        }
    }

    private void showVisitors() {
        Intent i = new Intent(GuardProfileActivity.this, VisitorsList.class);
        i.putExtra("condominium", cond);
        i.putExtra("addressForSelectedCond", addressForSelectedCond);
        i.putExtra("isGuard", true );
        startActivity(i);
    }

    public void editProfile(){
        Log.d(TAG, "Show edit profile activity");
    }

    public void changePass(){
        final EditText resetPassword = new EditText(GuardProfileActivity.this);

        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(GuardProfileActivity.this);
        passwordResetDialog.setTitle("Reset Password?");
        passwordResetDialog.setMessage("Enter New Password > 6 Characters long.");
        passwordResetDialog.setView(resetPassword);

        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // extract the email and send reset link
                String newPassword = resetPassword.getText().toString();
                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(GuardProfileActivity.this, "Password Reset Successfully.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GuardProfileActivity.this, "Password Reset Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close
            }
        });

        passwordResetDialog.create().show();
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    @Override
    public void onBackPressed(){
        finish();
        finishActivity();
    }

    private void finishActivity(){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("condominium", cond);
        startActivity(intent);
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }
}