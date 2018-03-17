package com.studybuddy.studybuddy;

import android.content.Intent;
import android.support.annotation.NonNull;
<<<<<<< HEAD
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

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
=======
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
>>>>>>> Users
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

<<<<<<< HEAD
public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout DL;
    private ActionBarDrawerToggle AB_toggle;

=======
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {
    private static final String TAG = "GoogleActivity";
    private TextView mEmailTextView;
    private TextView mUidTextView;
    private TextView mFirstNameView;
    private TextView mLastNameView;
    private TextView mMajorView;
    private TextView mYearView;
    private GoogleSignInClient mGoogleSignInClient;
    private Button signOut;
    private Button save;
>>>>>>> Users
    private FirebaseAuth mAuth;
    private String userProfileId;
    private DocumentReference userProfile;

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
<<<<<<< HEAD
=======
        signOut = findViewById(R.id.testSignOut);
        save = findViewById(R.id.save);
        //delete later
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfo();
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Configure sign-in to request the user's ID, email address, etc.
        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
        builder.requestIdToken(getString(R.string.default_web_client_id));
        builder.requestEmail();
        GoogleSignInOptions gso = builder.build();

        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mEmailTextView = findViewById(R.id.email2);
        mUidTextView = findViewById(R.id.uid2);
        mFirstNameView = findViewById(R.id.firstName);
        mLastNameView = findViewById(R.id.lastName);
        mMajorView = findViewById(R.id.major);
        mYearView = findViewById(R.id.year);

        mEmailTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
        mUidTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

        // Gets document where user uid == document's uid
        // Sets Text based on document data
        db.collection("users").whereEqualTo("uid", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Need to get document ID so we can make a reference to it in userProfile
                                userProfileId = task.getResult().getDocuments().get(0).getId();
                                userProfile = db.collection("users").document(userProfileId);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                mFirstNameView.setHint(getString(R.string.first_name, document.get("firstName").toString()));
                                mLastNameView.setHint(getString(R.string.last_name, document.get("lastName").toString()));
                                mMajorView.setHint(getString(R.string.major, document.get("major").toString()));
                                mYearView.setHint(getString(R.string.year, document.get("year").toString()));
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    // Updates current document info with inputted info
    private void saveInfo() {
        Map<String, Object> user = new HashMap<>();
        // Make sure user doesn't try to save empty data
        //TODO this doesn't throw an error, might need to manually check if null, current try catch useless
        try {
            user.put("firstName", mFirstNameView.getText().toString());
            user.put("lastName", mLastNameView.getText().toString());
            user.put("major", mMajorView.getText().toString());
            user.put("year", mYearView.getText().toString());
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "updateProfileWithEmptyFields", e);
            Toast.makeText(Home.this, "Please fill out all fields.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        userProfile.set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(Home.this, "Profile updated successfully!",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(Home.this, "Failed to update profile!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

>>>>>>> Users
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

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return true;
        }

        else if (id == R.id.nav_settings) {
           //Open settings activity
            return true;
        }
        // TODO figure out why this doesn't work. It doesn't throw errors, just doesn't pull up page
        else if (id == R.id.nav_account) {
            //Open account activity
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            finish();
            return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}