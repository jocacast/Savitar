package com.example.savitar;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.firestore.FirebaseFirestore;

public class EditVisitor extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "EditVisitor";
    FirebaseAuth fAuth;
    String userId, visitorName, visitorLicencePlates, documentId;
    FirebaseUser user;
    FirebaseFirestore db;
    Button saveBtn, deleteBtn;
    EditText visitorNameEditText, visitorLicencePlatesEditText;
    TextView hostPhoneTextView, hostAddressTextView;
    SwitchCompat isAllowedSwitch;
    Visitor visitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_visitor);

        visitor = (Visitor)getIntent().getSerializableExtra("EXTRA_VISITOR");
        documentId = getIntent().getStringExtra("SNAPSHOT_ID");

        Log.d(TAG, "Get extra condominium " + visitor.getCondominium());
        Log.d(TAG, "Get extra addressForSelectedCond " + visitor.getHostAddress());

        visitorNameEditText = findViewById(R.id.guardName);
        visitorLicencePlatesEditText = findViewById(R.id.guardEmailAddress);
        hostPhoneTextView = findViewById(R.id.guardPhone);
        hostAddressTextView = findViewById(R.id.guardAddress);
        saveBtn = findViewById(R.id.showVisitorListBtn);
        deleteBtn = findViewById(R.id.deleteGuest);
        isAllowedSwitch = findViewById(R.id.switch1);



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
        setVisitorInformation();
        saveBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }

    private void setVisitorInformation() {
        visitorNameEditText.setText(visitor.getName());
        visitorLicencePlatesEditText.setText(visitor.getLicensePlates());
        hostPhoneTextView.setText(visitor.getHostName());
        hostAddressTextView.setText(visitor.getHostAddress());
        isAllowedSwitch.setChecked(visitor.isAllowEntrance());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId== android.R.id.home) {
            finishEditActivity();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v){
        int viewId = v.getId();
        if(viewId == R.id.showVisitorListBtn){
            saveVisitorInfo();
        }else if(viewId == R.id.deleteGuest){
            deleteVisitor();
        }
    }

    private void deleteVisitor() {
        Log.d(TAG, "Delete Button selected");
        final TextView deleteVisitor = new TextView(EditVisitor.this);
        final AlertDialog.Builder deleteVisitorAlertDialog = new AlertDialog.Builder(EditVisitor.this);
        deleteVisitorAlertDialog.setTitle("Delete User " + visitor.getName()+"?");
        deleteVisitorAlertDialog.setView(deleteVisitor);
        deleteVisitorAlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Yes clicked");
                db.collection("visitor").document(documentId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                finishEditActivity();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditVisitor.this, "Delete Function Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        deleteVisitorAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "No clicked");
            }
        });
        deleteVisitorAlertDialog.create().show();
    }

    private void saveVisitorInfo() {
        saveBtn.setEnabled(false);
        visitorName = visitorNameEditText.getText().toString();
        visitorLicencePlates = visitorLicencePlatesEditText.getText().toString();
        Boolean switchState = isAllowedSwitch.isChecked();
        db.collection("visitor").document(documentId)
                .update("name",visitorName,"licensePlates",visitorLicencePlates, "allowEntrance", switchState)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditVisitor.this, "Visitor successfully updated", Toast.LENGTH_SHORT).show();
                saveBtn.setEnabled(true);
                finishEditActivity();
            }
        });
    }

    private void finishEditActivity(){
        finish();
        Intent i = new Intent(getApplicationContext(), VisitorsList.class);
        i.putExtra("condominium", visitor.getCondominium());
        i.putExtra("addressForSelectedCond",visitor.getHostAddress());
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

    @Override
    public void onBackPressed(){
        finishEditActivity();
    }

}