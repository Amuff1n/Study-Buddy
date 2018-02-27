package com.studybuddy.studybuddy;

import android.support.annotation.NonNull;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout DL;
    private ActionBarDrawerToggle AB_toggle;

    private Toolbar tool;

    private Button signOut;
    private FirebaseAuth mAuth;

    private void setNavigationViewListner() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_action);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //setNavigationViewListener();

        /*tool = findViewById(R.id.nav_action);
        setSupportActionBar(tool);*/

        DL = (DrawerLayout) findViewById(R.id.drawerLayout);
        AB_toggle = new ActionBarDrawerToggle(this, DL, R.string.open, R.string.close);
        DL.addDrawerListener(AB_toggle);
        AB_toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        signOut = (Button) findViewById(R.id.testSignOut);
        //delete later
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
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
            return true;
        }

        else if (id == R.id.nav_settings) {
           //Open settings activity
            return true;
        }

        else if (id == R.id.nav_account) {
            //Open account activity
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}