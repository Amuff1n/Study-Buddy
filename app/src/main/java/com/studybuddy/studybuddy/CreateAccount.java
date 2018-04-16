package com.studybuddy.studybuddy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "GoogleActivity";
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPassword;
    private Button mCreateAccountBtn;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        mAuth = FirebaseAuth.getInstance();
        mEmailField = findViewById(R.id.email_);
        mPasswordField = findViewById(R.id.password_);
        mConfirmPassword = findViewById(R.id.confirm_password);
        mCreateAccountBtn = findViewById(R.id.SignUp);
        mFirestore = FirebaseFirestore.getInstance();
        mCreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmailField.getText().toString().trim();
                String password = mPasswordField.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();
                //checks to make sure email field is filled in
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
                }
                //checks for valid email
                else if(!email.matches(emailPattern)){
                    Toast.makeText(getApplicationContext(), "Enter a valid email", Toast.LENGTH_SHORT).show();
                }
                //checks to make sure password field is filled in
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                }
                //checks to make sure password confirmed is filled in
                else if(TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(getApplicationContext(), "Confirm password", Toast.LENGTH_SHORT).show();
                }
                //checks to make sure passwords match
                else if(!password.equals(confirmPassword)){
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                //creates account using email and password
                else{
                    mAuth.createUserWithEmailAndPassword(mEmailField.getText().toString(), mPasswordField.getText().toString())
                            .addOnCompleteListener(CreateAccount.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // account successfully created
                                        Log.d(TAG, "createUserWithEmail:success");
                                        Toast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                                        Map<String, Object> emailMap = new HashMap<>();
                                        emailMap.put("email", email);
                                        //adds email to database
                                        mFirestore.collection("users").document(mAuth.getUid()).set(emailMap);
                                        //starts activity to enter account information
                                        startActivity(new Intent(getApplicationContext(), SetUpAccount.class));
                                    } else {
                                        // account already created with given email
                                        mAuth.fetchProvidersForEmail(mEmailField.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<ProviderQueryResult> task){
                                                if(!task.getResult().getProviders().isEmpty()){
                                                    Toast.makeText(getApplicationContext(),"Email already registered",Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                else{
                                                    //account ot created
                                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });
    }
}
