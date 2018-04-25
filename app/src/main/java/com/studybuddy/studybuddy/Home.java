package com.studybuddy.studybuddy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private static final String TAG = "GoogleActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle AB_toggle;
    private FloatingActionButton fab;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView recyclerView;
    Context hackContext;

    private GroupListAdapter adapter;
    private List<GroupListItem> list;
    private RecyclerView.LayoutManager layoutManager;

    private void setNavigationViewListner() {
        NavigationView navigationView = findViewById(R.id.nav_action);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //setNavigationViewListener();

        setClassVariables();
        drawerLayout.addDrawerListener(AB_toggle);
        AB_toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hackContext = this;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();

        fabListeners();
        populateRecyclerview();
        recyclerviewListeners();
        swipeRefreshListeners();
    }

    //TODO what is this for?
    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.nav_action);
        navigationView.setNavigationItemSelectedListener(this);
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
        } else if (id == R.id.nav_settings) {
            //Open settings activity
            Toast.makeText(Home.this, "Nothing yet!",
                    Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.nav_account) {
            //Open account activity
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            return true;
        } else if (id == R.id.nav_add_classe) {
            startActivity(new Intent(getApplicationContext(), ClassRecyclerView.class));
            return true;
        }
        else if(id == R.id.nav_chat) {
            startActivity(new Intent(getApplicationContext(), Chat.class ));
            return true;
        }
        else if(id == R.id.nav_chat) {
            startActivity(new Intent(getApplicationContext(), Chat.class ));
            return true;
        }
        else if (id == R.id.nav_find_classes) {
            //Open location activity
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //simple 'refresh' method that clears the grouplist and repopulates it
    public void refreshRecyclerView() {

        recyclerView.getAdapter().notifyDataSetChanged();
        list.clear();
        populateRecyclerview();
        mSwipeRefreshLayout.setRefreshing(false); //stop refresh animation when done
    }

    //Populate Recycler View with data from Firebase
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
                        boolean isInGroup = false;
                        int userIndex = 0; //save user's index in group for easy removal later
                        //try catch block until deleting a group implemented
                        try {
                            if (document.get("user") != null) {
                                if (document.get("user").equals(mAuth.getUid())) {
                                    isInGroup = true;
                                }
                            }
                            for (int i = 1; i < document.getDouble("maxUserIndex"); i++) {
                                if (document.get("user" + i) != null) {
                                    if (document.get("user" + i).equals(mAuth.getUid())) {
                                        isInGroup = true;
                                        userIndex = i;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Failed to retrieve users from Firestore.", e);
                        }

                        //For each card, get class, desc, location, timestamp, groupID, and index
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM d HH:mm");

                        //Cast firebase object to Date before trying to use format()
                        Date creationTime = Date.class.cast(document.get("creationTime"));

                        GroupListItem groupListItem = new GroupListItem(
                                document.get("class").toString() + '\n' +
                                         sdf.format(creationTime),
                                document.get("description").toString() +
                                        "\n" + document.get("location").toString(),
                                document.get("class").toString(),
                                document.get("location").toString(),
                                document.getDouble("index"),
                                userIndex,
                                document.getDouble("maxUserIndex"),
                                isInGroup,
                                document.getId()
                        );
                        list.add(0, groupListItem);
                        ArrayList<GroupListItem> mutable = new ArrayList<>(list);
                        adapter = new GroupListAdapter(mutable, hackContext);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }
        });
    }

    //Helper method to define all class data members
    private void setClassVariables() {
        drawerLayout = findViewById(R.id.drawerLayout);
        fab = findViewById(R.id.fab);
        hackContext = this;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        AB_toggle = new
                ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    //Helper method to add event listeners to recyclerview
    private void recyclerviewListeners() {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        final MenuItem item = menu.findItem(R.id.search_groups);
        final SearchView searchView = (SearchView) item.getActionView();
        /*int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        searchPlate.setBackgroundResource(R.color.colorPrimaryAlt2);*/
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        s = s.toLowerCase();
        List<GroupListItem> filteredModelList = new ArrayList<>();
        for (GroupListItem model : list) {
            String text = model.getText().toLowerCase();
            String header = model.getHeader().toLowerCase();
            if (text.contains(s) || header.contains(s)) {
                filteredModelList.add(model);
            }
        }

        adapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    //Helper method to add listeners to fab
    private void fabListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send code with intent which CreateGroup will send back before finishing
                Intent intent = new Intent(getApplicationContext(), CreateGroup.class);
                int requestCode = 1;
                startActivityForResult(intent, requestCode);
            }
        });
    }

    //Activity result of create groups so we can refresh
    //Makes it so CreateGroup.java doesn't depend on Home.java code
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                refreshRecyclerView();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //do nothing if we don't create a group?
            }
        }

    }

    //Helper method to add listeners to swipe refresh layout
    private void swipeRefreshListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecyclerView();
            }
        });
    }
}