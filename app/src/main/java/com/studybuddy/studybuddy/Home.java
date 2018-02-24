package com.studybuddy.studybuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {
    private TextView mEmailTextView;
    private TextView mUidTextView;
    private GoogleSignInClient mGoogleSignInClient;
    private Button signOut;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        signOut = findViewById(R.id.testSignOut);
        //delete later
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();

        // Configure sign-in to request the user's ID, email address, etc.
        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
        builder.requestIdToken(getString(R.string.default_web_client_id));
        builder.requestEmail();
        GoogleSignInOptions gso = builder.build();

        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mEmailTextView = findViewById(R.id.email2);
        mUidTextView = findViewById(R.id.uid2);

        mEmailTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
        mUidTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
