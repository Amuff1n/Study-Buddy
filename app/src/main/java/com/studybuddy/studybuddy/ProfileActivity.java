package com.studybuddy.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView mNameTextView;
    private TextView mSchoolTextView;
    private TextView mYearTextView;
    private TextView mClassesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNameTextView = findViewById(R.id.profile_name);
        mSchoolTextView = findViewById(R.id.profile_school);
        mYearTextView = findViewById(R.id.profile_year);
        mClassesTextView = findViewById(R.id.profile_classes);

        mDatabase = 
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            fetchProfile(mAuth.getCurrentUser());
            //startActivity(new Intent(getApplicationContext(), Home.class));
            //finish();
        }

    }  // void onCreate()

    private void fetchProfile(FirebaseUser user) {
        if(user != null) {
            String uid = user.getUid();

        }
        else {
            mNameTextView.setText("Nobody's logged in!");
        }
    }
}  // class ProfileActivity
