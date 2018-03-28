package com.studybuddy.studybuddy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddClasses extends AppCompatActivity {

    private FirebaseUser mUser;
    private FirebaseFirestore mFireStore;
    private Button done;
    private ImageButton add;
    private EditText classes;
    private TextView cancelAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classes);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFireStore = FirebaseFirestore.getInstance();
        done = findViewById(R.id.classesAdded);
        add = findViewById(R.id.addClass);
        classes = findViewById(R.id.userClasses);
        cancelAdd = findViewById(R.id.CancelAdd);
        cancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "No classes added", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), Home.class));
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Home.class));
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(classes == null){
                    Toast.makeText(getApplicationContext(),"Enter a class", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    String classString = classes.getText().toString();
                    Map<String, Object> classMap = new HashMap<>();
                    classMap.put("class" + classString, classString);
                    mFireStore.collection("users").document(mUser.getUid()).update(classMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            classes.setText(null);
                            classes.setHint("Enter your class code (IUF1000)");
                            Toast.makeText(getApplicationContext(), "Add the rest of your classes or click done", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onBackPressed(){}
}
