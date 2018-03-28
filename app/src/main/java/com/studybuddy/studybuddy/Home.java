package com.studybuddy.studybuddy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout DL;
    private ActionBarDrawerToggle AB_toggle;

    private FirebaseAuth mAuth;

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

        mAuth = FirebaseAuth.getInstance();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateGroup.class));
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (AB_toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
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

        else if (id == R.id.nav_location) {
            //Open location activity
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}