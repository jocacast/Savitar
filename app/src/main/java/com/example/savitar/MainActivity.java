package com.example.savitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId, cond;
    FirebaseUser user;
    Button nextBtn;
    Spinner condSpinner;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent info
        cond = getIntent().getStringExtra("condominium");
        //Firebase Info
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        //Rest of the elements
        nextBtn = findViewById(R.id.nextBtn);
        condSpinner = findViewById(R.id.spinner);
        progressBar = findViewById(R.id.progressBar);
        //Set On Click listeners
        nextBtn.setOnClickListener(this);
        fStore = FirebaseFirestore.getInstance();
        //Toolbar
        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);


        progressBar.setVisibility(View.VISIBLE);
        condSpinner.setVisibility(View.INVISIBLE);
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
                            condSpinner.setAdapter(adapter);
                            if(cond != null){
                                for(int i = 0; i<subjects.size(); i++){
                                    if(subjects.get(i).equals(cond)){
                                        condSpinner.setSelection(i);
                                    }
                                }
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        progressBar.setVisibility(View.GONE);
                        condSpinner.setVisibility(View.VISIBLE);
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
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        int viewId = v.getId();
        if(viewId == R.id.nextBtn){
            String selected = condSpinner.getSelectedItem().toString();
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

    private void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }




}