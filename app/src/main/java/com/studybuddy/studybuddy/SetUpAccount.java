package com.studybuddy.studybuddy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SetUpAccount extends AppCompatActivity {

    private FirebaseUser mUser;
    private TextView cancel;
    private Button confirm;
    private EditText firstName;
    private EditText lastName;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_account);
        cancel = findViewById(R.id.Cancel);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        confirm = findViewById(R.id.ConfirmAccount);
        firstName = findViewById(R.id.FirstName);
        lastName = findViewById(R.id.LastName);
        mFirestore = FirebaseFirestore.getInstance();
        mId = FirebaseAuth.getInstance();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        //adds first, last, and uid to database
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {create();}
        });
    }
    //override default back button
    @Override
    public void onBackPressed(){}

    //delete account if user clicks cancel
    private void delete(){
        if(mUser != null){
            mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }
            });
        }
    }
    //adds first name last name and uid to firebase
    private void create(){
        String FirstName = firstName.getText().toString();
        String LastName = lastName.getText().toString();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", FirstName);
        userMap.put("lastName", LastName);
        userMap.put("uid", mId.getUid());
        mFirestore.collection("users").document(mUser.getUid()).update(userMap).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Enter all info", Toast.LENGTH_SHORT).show();
            }
        });
        startActivity(new Intent(getApplicationContext(), AddClasses.class));
    }
}
