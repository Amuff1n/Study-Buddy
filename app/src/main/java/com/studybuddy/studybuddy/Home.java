package com.studybuddy.studybuddy;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {
    private DrawerLayout DL;
    private ActionBarDrawerToggle AB_toggle;
    Context hackContext;
    private static final String TAG = "GoogleActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RecyclerView recyclerView;
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

        DL = findViewById(R.id.drawerLayout);
        AB_toggle = new ActionBarDrawerToggle(this, DL, R.string.open, R.string.close);
        DL.addDrawerListener(AB_toggle);
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

        populateRecyclerview();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateGroup.class));
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
                        GroupListItem groupListItem = new GroupListItem(
                                document.get("class").toString(),
                                document.get("description").toString() + "\n" + document.get("location").toString() + "\n" + document.get("creationTime").toString(),
                                document.get("class").toString(),
                                document.get("location").toString()
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
}