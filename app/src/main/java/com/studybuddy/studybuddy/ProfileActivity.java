package com.studybuddy.studybuddy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "GoogleActivity";
    private TextView mEmailTextView;
    private TextView mUidTextView;
    private TextView mFirstNameView;
    private TextView mLastNameView;
    private TextView mMajorView;
    private TextView mYearView;
    private GoogleSignInClient mGoogleSignInClient;
    private Button save;
    private FirebaseAuth mAuth;
    private String userProfileId;
    private DocumentReference userProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        save = findViewById(R.id.save);

        mAuth = FirebaseAuth.getInstance();

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

        mEmailTextView = findViewById(R.id.email);
        mUidTextView = findViewById(R.id.uid);
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
                                mFirstNameView.setText(document.get("firstName").toString());
                                mLastNameView.setText(document.get("lastName").toString());
                                mMajorView.setText(document.get("major").toString());
                                mYearView.setText(document.get("year").toString());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
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
                        Toast.makeText(ProfileActivity.this, "Failed to update profile!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }


}
