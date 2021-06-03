package com.example.savitar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterEntryActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "RegisterEntryActivity";
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    Visitor visitor;
    RegisterEntry registerEntry;
    String userId, cond, addressForSelectedCond;
    TextView visitorName, entryDate, hostOffice;
    Button registerBtn;
    boolean isGuard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_entry);
        //Intent information
        cond = getIntent().getStringExtra("condominium");
        addressForSelectedCond = getIntent().getStringExtra("addressForSelectedCond");
        isGuard = getIntent().getBooleanExtra("isGuard", false);
        visitor = (Visitor)getIntent().getSerializableExtra("visitor");
        Log.d(TAG, "Visitor info " + visitor.toString());

        visitorName = findViewById(R.id.visitorName);
        entryDate = findViewById(R.id.entryDate);
        hostOffice = findViewById(R.id.hostOffice);
        registerBtn = findViewById(R.id.registerEntryBtn);

        visitorName.setText(visitor.getName());
        entryDate.setText(visitor.getLicensePlates());
        hostOffice.setText(visitor.getHostAddress());

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);
        registerBtn.setOnClickListener(this);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId== android.R.id.home) {
            finishActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        int viewId = v.getId();
        if(viewId == R.id.registerEntryBtn){
            registerEntry();
        }
    }

    private void finishActivity(){
        finish();
        Intent i;
        i = new Intent(RegisterEntryActivity.this, VisitorsList.class);
        i.putExtra("condominium", cond);
        i.putExtra("addressForSelectedCond",addressForSelectedCond);
        i.putExtra("isGuard", isGuard);
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

    private void registerEntry(){
        registerBtn.setEnabled(false);
        //public RegisterEntry(String visitorName, String entryDate, String hostName, String hostOffice)
        registerEntry = new RegisterEntry(visitor.getName(), visitor.getHostName(), visitor.getHostAddress());
        db.collection("entries")
                .add(registerEntry)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(RegisterEntryActivity.this, "Entry successfully registered", Toast.LENGTH_SHORT).show();
                        registerBtn.setEnabled(true);
                        finishActivity();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(RegisterEntryActivity.this, "Error while saving entry" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    registerBtn.setEnabled(true);
                });
    }

    @Override
    public void onBackPressed(){
        finishActivity();
    }
}