package com.studybuddy.studybuddy;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout DL;
    private ActionBarDrawerToggle AB_toggle;
    Context hackContext;

    private static final String TAG = "GoogleActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    RecyclerView recyclerView;
    private List<GroupListItem> list;

    private FloatingActionButton fab;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private void setNavigationViewListner() {
        NavigationView navigationView = findViewById(R.id.nav_action);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //setNavigationViewListener();

        DL = findViewById(R.id.drawerLayout);
        AB_toggle = new ActionBarDrawerToggle(this, DL, R.string.open, R.string.close);
        DL.addDrawerListener(AB_toggle);
        AB_toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hackContext = this;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerview);
        list = new ArrayList<>();

        populateRecyclerview();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateGroup.class));
            }
        });

        //Hide the fab when we scroll down, reveal when scroll up
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecyclerView();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return AB_toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            mAuth.signOut();
            Toast.makeText(Home.this, "Logging out",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return true;
        }

        else if (id == R.id.nav_settings) {
            //Open settings activity
            Toast.makeText(Home.this, "Nothing yet!",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        else if (id == R.id.nav_account) {
            //Open account activity
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            return true;
        }

        else if (id == R.id.nav_add_classe) {
            startActivity(new Intent(getApplicationContext(), AddClasses.class));
            return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //simple 'refresh' method that clears the grouplist and repopulates it
    public void refreshRecyclerView() {
        list.clear();
        populateRecyclerview();
        mSwipeRefreshLayout.setRefreshing(false); //stop refresh animation when done
    }

    private void populateRecyclerview() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Get groups collection
        CollectionReference collectionReference = db.collection("study_groups");
        Query query = collectionReference.orderBy("creationTime").limit(20);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

                        //Start the process of checking if user is in that group
                        //If uid matches 'user' or ''user' + index' they are in that group, so they can't join
                        boolean joining = true;
                        int userIndex = 0; //save user's index in group for easy removal later
                        //try catch block until deleting a group implemented
                        try {
                            if (document.get("user").equals(mAuth.getUid())) {
                                joining = false;
                            }

                            for (int i = 1; i < document.getDouble("index"); i++) {
                                if (document.get("user" + i).equals(mAuth.getUid())) {
                                    joining = false;
                                    userIndex = i;
                                }
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Failed to get any user, group probably shouldn't exist", e);
                        }


                        //For each group, get class, desc, location, timestamp, groupID, number of users in group (index)
                        //also get current user's index if they are in the group
                        //if not in group, joining true
                        GroupListItem groupListItem = new GroupListItem(
                                document.get("class").toString(),
                                document.get("description").toString() + "\n" + document.get("location").toString() + "\n" + document.get("creationTime").toString(),
                                document.getDouble("index"),
                                userIndex,
                                joining,
                                document.getId()
                        );
                        list.add(0, groupListItem);
                        //System.out.println(list.get(0).getHeader()); //should use log instead
                        RecyclerView.Adapter adapter = new GroupListAdapter(list, hackContext);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }
        });
    }
}