package com.example.savitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    FirebaseUser user;
    Button nextBtn;
    Spinner condSpiner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Firebase Info
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        //Rest of the elements
        nextBtn = findViewById(R.id.nextBtn);
        condSpiner = findViewById(R.id.spinner);
        //Set On Click listeners
        nextBtn.setOnClickListener(this);
        fStore = FirebaseFirestore.getInstance();



        fStore.collection("authorizedUsers").whereEqualTo("email", user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<String> subjects = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                subjects.add(document.get("condominium").toString());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, subjects);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            condSpiner.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        /*DocumentReference docRef = fStore.collection("admins").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.get("condominiums"));
                        List<String> subjects = (ArrayList<String>)document.get("condominiums");
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, subjects);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        condSpiner.setAdapter(adapter);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });*/

    }

    @Override
    public void onClick(View v){
        int viewId = v.getId();
        if(viewId == R.id.nextBtn){
            String selected = condSpiner.getSelectedItem().toString();
            Log.d(TAG, "Selected option " + selected);

            fStore.collection("authorizedUsers").whereEqualTo("email", user.getEmail()).whereEqualTo("condominium", selected).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<String> subjects = new ArrayList<>();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    boolean isGuard = (boolean)document.get("guard");
                                    String addressForSelectedCond = document.get("address").toString();
                                    Intent i;
                                    if(isGuard){
                                        i = new Intent(MainActivity.this, GuardProfileActivity.class);
                                    }else{
                                        i = new Intent(MainActivity.this, ProfileActivity.class);
                                    }
                                    i.putExtra("condominium", selected);
                                    i.putExtra("isGuard", isGuard);
                                    i.putExtra("addressForSelectedCond", addressForSelectedCond);
                                    startActivity(i);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }



}