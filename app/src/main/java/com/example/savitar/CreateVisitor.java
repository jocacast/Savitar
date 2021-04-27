package com.example.savitar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class CreateVisitor extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "CreateGuest";
    FirebaseAuth fAuth;
    String userId, visitorName, visitorLicencePlates, cond, addressForSelectedCond;
    FirebaseUser user;
    FirebaseFirestore db;
    Button saveBtn;
    EditText visitorNameEditText, visitorLicencePlatesEditText;
    TextView hostPhoneTextView, hostAddressTextView;
    SwitchCompat isAllowedSwitch;
    Host host;

    TextView phone, address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_visitor);
        cond = getIntent().getStringExtra("condominium");
        addressForSelectedCond = getIntent().getStringExtra("addressForSelectedCond");

        visitorNameEditText = findViewById(R.id.guardName);
        visitorLicencePlatesEditText = findViewById(R.id.guardEmailAddress);
        hostPhoneTextView = findViewById(R.id.guardPhone);
        hostAddressTextView = findViewById(R.id.guardAddress);
        saveBtn = findViewById(R.id.showVisitorListBtn);


        isAllowedSwitch = (SwitchCompat) findViewById(R.id.switch1);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        DocumentReference documentReference = db.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    String dsPhone = documentSnapshot.getString("phone");
                    String dsFName = documentSnapshot.getString("fName");
                    String dsEmail = documentSnapshot.getString("email");
                    host = new Host(dsFName,addressForSelectedCond,dsEmail,dsPhone);
                    Log.d(TAG, "Host info " + host.toString());
                    setHostStaticInfo();
                }else {
                    Log.d(TAG, "onEvent: Document do not exists");
                }
            }
        });
        saveBtn.setOnClickListener(this);
    }

    private void setHostStaticInfo() {
        hostPhoneTextView.setText(host.getPhone());
        hostAddressTextView.setText(host.getAddress());
    }


    @Override
    public void onClick(View v){
        int viewId = v.getId();
        if(viewId == R.id.showVisitorListBtn){
            saveVisitor();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finishActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveVisitor() {
        saveBtn.setEnabled(false);
        visitorName = visitorNameEditText.getText().toString();
        visitorLicencePlates = visitorLicencePlatesEditText.getText().toString();
        Boolean switchState = isAllowedSwitch.isChecked();
        Visitor visitor = new Visitor(visitorName, visitorLicencePlates, addressForSelectedCond, host.getfName(), host.getEmail(), host.getPhone(), cond, switchState);
        Log.d(TAG, "Visitor " + visitor.toString());
        db.collection("visitor")
                .add(visitor)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(CreateVisitor.this, "Visitor successfully created", Toast.LENGTH_SHORT).show();
                        saveBtn.setEnabled(true);
                        finishActivity();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(CreateVisitor.this, "Error while saving visitor" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    saveBtn.setEnabled(true);
                });
    }

    private void finishActivity(){
        finish();
        Intent i = new Intent(getApplicationContext(), VisitorsList.class);
        i.putExtra("condominium", cond);
        i.putExtra("addressForSelectedCond",addressForSelectedCond);
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

    @Override
    public void onBackPressed(){
        finishActivity();
    }

}