package com.example.savitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class VisitorsList extends AppCompatActivity {
    public static final String TAG = "VisitorsList";
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    String userId, cond, addressForSelectedCond;
    boolean isGuard;
    ExampleAdapter exAdapter;
    private VisitorAdapter.OnItemClickListener listener;
    private List<Visitor> visitorList=new ArrayList<>();
    TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitors_list);
        //Intent information
        cond = getIntent().getStringExtra("condominium");
        addressForSelectedCond = getIntent().getStringExtra("addressForSelectedCond");
        isGuard = getIntent().getBooleanExtra("isGuard", false);
        Log.d(TAG, "Get extra condominium " + cond);
        Log.d(TAG, "Get extra addressForSelectedCond " + addressForSelectedCond);
        Log.d(TAG, "Get extra isGuard "+ isGuard);
        //Firebase Information
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        //Rest of items
        TextView titleTextView = findViewById(R.id.textView2);
        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        FloatingActionButton addVisitor = findViewById(R.id.add_visitor);
        if(isGuard){
            addVisitor.setVisibility(View.INVISIBLE);
            titleTextView.setText(R.string.visitorsList);
            fillExampleListGuard();
        }else{
            fillExampleList();
        }
        addVisitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CreateVisitor.class);
                intent.putExtra("condominium", cond);
                intent.putExtra("addressForSelectedCond",addressForSelectedCond);
                startActivity(intent);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.search_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.search_questions);
        SearchView searchView=(SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                exAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId== android.R.id.home) {
            finishActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillExampleListGuard() {
        Log.d(TAG, "FillExampleList Guard started");
        db.collection("visitor")
               .whereEqualTo("condominium" , cond)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Visitor visitor = document.toObject(Visitor.class);
                                visitor.setId(document.getId());
                                visitorList.add(visitor);
                            }
                            RecyclerView recyclerView = findViewById(R.id.recycler_view);
                            recyclerView.setHasFixedSize(true);
                            exAdapter = new ExampleAdapter(visitorList, true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(VisitorsList.this));
                            recyclerView.setAdapter(exAdapter);
                            exAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    Visitor visitor = visitorList.get(position);
                                    String id = visitor.getId();
                                    Log.d(TAG, "Clicked on visitor " + visitor.toString());
                                }
                            });

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    private void fillExampleList() {
        Log.d(TAG, "fillExampleList started");
        db.collection("visitor")
                .whereEqualTo("hostEmail", user.getEmail()).whereEqualTo("condominium" , cond)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Visitor visitor = document.toObject(Visitor.class);
                                visitor.setId(document.getId());
                                visitorList.add(visitor);
                            }
                            RecyclerView recyclerView = findViewById(R.id.recycler_view);
                            recyclerView.setHasFixedSize(true);
                            exAdapter = new ExampleAdapter(visitorList, isGuard);
                            recyclerView.setLayoutManager(new LinearLayoutManager(VisitorsList.this));
                            recyclerView.setAdapter(exAdapter);
                            exAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    Visitor visitor = visitorList.get(position);
                                    String id = visitor.getId();
                                    Log.d(TAG, "Clicked on visitor " + visitor.toString());
                                    Intent intent = new Intent(getBaseContext(), EditVisitor.class);
                                    intent.putExtra("EXTRA_VISITOR", visitor);
                                    intent.putExtra("SNAPSHOT_ID", id);
                                    startActivity(intent);
                                }
                            });

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    /*private void setUpRecyclerView(){
        Log.d(TAG, "setUpRecyclerView for user " + user.getEmail() );
        Query query = db.collection("visitor").whereEqualTo("hostEmail", user.getEmail());
        FirestoreRecyclerOptions<Visitor> options  = new FirestoreRecyclerOptions.Builder<Visitor>()
                .setQuery(query, Visitor.class)
                .build();
        adapter = new VisitorAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new VisitorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Visitor visitor = documentSnapshot.toObject(Visitor.class);
                String id = documentSnapshot.getId();
                Log.d(TAG, "Clicked item document id " + id );
                Intent intent = new Intent(getBaseContext(), EditVisitor.class);
                intent.putExtra("EXTRA_VISITOR", visitor);
                intent.putExtra("SNAPSHOT_ID", id);
                startActivity(intent);
            }
        });
    }*/



    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onBackPressed(){
        finishActivity();
    }

    private void finishActivity(){
        finish();
        Intent i;
        if(isGuard){
            i = new Intent(VisitorsList.this, GuardProfileActivity.class);
        }else{
            i = new Intent(VisitorsList.this, ProfileActivity.class);
        }
        i.putExtra("condominium", cond);
        i.putExtra("addressForSelectedCond",addressForSelectedCond);
        i.putExtra("isGuard", isGuard);
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

}