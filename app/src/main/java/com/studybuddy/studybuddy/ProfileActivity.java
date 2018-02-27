package com.studybuddy.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView mEmailTextView;
    private TextView mUidTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
        }

        //TODO currently these textviews show incorrect demo information
        mEmailTextView = findViewById(R.id.profile_name);
        mUidTextView = findViewById(R.id.profile_classes);
    }  // void onCreate()

    private void updateUI(FirebaseUser user) {
        if(user != null) {
            mEmailTextView.setText(getString(R.string.firebase_status_fmt, user.getEmail()));
            mUidTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
        }
        else {
            mEmailTextView.setText("Nobody's logged in!");
        }
    }
}  // class ProfileActivity
