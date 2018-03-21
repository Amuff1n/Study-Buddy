package com.studybuddy.studybuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classes);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFireStore = FirebaseFirestore.getInstance();
        done = (Button) findViewById(R.id.classesAdded);
        add = (ImageButton) findViewById(R.id.addClass);
        classes = (EditText) findViewById(R.id.userClasses);
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
                    classMap.put(classString, true);
                    //MapPost classMapPost = new MapPost("classes", classMap);
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

    public class MapPost {
        String classes;
        Map<String,Boolean> list;

        public MapPost(String classes, Map<String,Boolean> list) {
            this.classes = classes;
            this.list = list;
        }
    }
}
