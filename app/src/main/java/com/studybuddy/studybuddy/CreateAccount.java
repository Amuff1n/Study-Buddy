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

public class CreateAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "GoogleActivity";
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPassword;
    private Button mCreateAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mAuth = FirebaseAuth.getInstance();
        mEmailField = findViewById(R.id.email_);
        mPasswordField = findViewById(R.id.password_);
        mConfirmPassword = findViewById(R.id.confirm_password);
        mCreateAccountBtn = findViewById(R.id.SignUp);
        mCreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailField.getText().toString().trim();
                String password = mPasswordField.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();
                //checks to make sure email and password are valid
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(getApplicationContext(), "Confirm password", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(confirmPassword)){
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.createUserWithEmailAndPassword(mEmailField.getText().toString(), mPasswordField.getText().toString())
                            .addOnCompleteListener(CreateAccount.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // account successfully created
                                        Log.d(TAG, "createUserWithEmail:success");
                                        Toast.makeText(getApplicationContext(), "Authentication succeeded!",
                                                Toast.LENGTH_SHORT).show();
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
