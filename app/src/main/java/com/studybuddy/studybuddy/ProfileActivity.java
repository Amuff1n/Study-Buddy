package com.studybuddy.studybuddy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "GoogleActivity";

    private FirebaseFirestore db;
    private String userProfileId;

    private DocumentReference userProfile;
    private TextView mNameTextView;
    private TextView mSchoolTextView;
    private TextView mMajorTextView;
    private TextView mYearTextView;
    private TextView mClassesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Configure sign-in to request the user's ID, email address, etc.
        GoogleSignInOptions.Builder builder =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
        builder.requestIdToken(getString(R.string.default_web_client_id));
        builder.requestEmail();
        //GoogleSignInOptions gso = builder.build();

        // Build a GoogleSignInClient with the options specified by gso
        //GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button saveButton = findViewById(R.id.profile_save_button);
        mNameTextView = findViewById(R.id.profile_name);
        mSchoolTextView = findViewById(R.id.profile_school);
        mMajorTextView = findViewById(R.id.profile_major);
        mYearTextView = findViewById(R.id.profile_year);
        mClassesTextView = findViewById(R.id.profile_classes);

        fetchProfile(mAuth.getCurrentUser());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        //startActivity(new Intent(getApplicationContext(), Home.class));
        //finish()
    }  // void onCreate()

    private void fetchProfile(FirebaseUser user) {
        if (user != null) {
            CollectionReference collectionReference = db.collection("users");
            String uid = "ftE8vUlZdgUktuxQyj6XeS94RDJ3";
            Query query = collectionReference.whereEqualTo("uid", uid);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            userProfileId = task.getResult().getDocuments().get(0).getId();
                            userProfile = db.collection("users").document(userProfileId);
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            String firstName = document.get("firstName").toString();
                            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
                            String lastName = document.get("lastName").toString();
                            String name = firstName + " " + lastName.substring(0, 1).toUpperCase() + ".";
                            mNameTextView.setText(name);

                            String school = document.get("school").toString();
                            mSchoolTextView.setText(school);

                            String major = document.get("major").toString();
                            major = major.substring(0, 1).toUpperCase() + major.substring(1).toLowerCase();
                            mMajorTextView.setText(major);

                            String year = document.get("year").toString();
                            mYearTextView.setText(year);

                            mClassesTextView.setText(document.get("classes").toString());
                        }
                    } else {
                        mNameTextView.setText(R.string.profile_error_databaseread);
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
        } else {
            mNameTextView.setText(R.string.profile_error_notlogged);
        }
    }  // void fetchProfile()

    private void saveProfile() {
        Map<String, Object> user = new HashMap<>();

        try {
            user.put("major", mMajorTextView.getText().toString());
            user.put("year", mYearTextView.getText().toString());
            user.put("school", mSchoolTextView.getText().toString());
            user.put("classes", mClassesTextView.getText().toString());

        } catch (IllegalArgumentException iae) {
            Log.w(TAG, "updateProfileWithEmptyFields", iae);
            Toast.makeText(ProfileActivity.this, "Please fill out all fields.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        userProfile.set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully!",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(ProfileActivity.this, "Failed to update Profile!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    } // void saveInfo()
}  // class ProfileActivity
