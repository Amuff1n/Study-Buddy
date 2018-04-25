package com.studybuddy.studybuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.studybuddy.studybuddy.RecyclerView.ClassAdapter;
import com.studybuddy.studybuddy.RecyclerView.ClassPOJO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassRecyclerView extends AppCompatActivity {

    private EditText addClassText;
    private ImageButton addClassButton;
    private RecyclerView classRecyclerView;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private ClassAdapter classAdapter;
    private List<String> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_recycler_view);
        addClassText = (EditText)findViewById(R.id.add_class_text);
        addClassButton = (ImageButton)findViewById(R.id.add_class_button);
        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        checkUserLogged();

        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String classString = addClassText.getText().toString();
                if(classString.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter a class", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Map<String, Object> classMap = new HashMap<>();
                    classMap.put("class" + classString, classString);
                    mFirestore.collection("users").document(mUser.getUid()).update(classMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            addClassText.setText(null);
                            addClassText.setHint("Enter your class code (IUF1000)");
                        }
                    });
                }
            }
        });
        populateList();
    }

    public void checkUserLogged(){
        if(mUser == null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

    }

    private void populateList(){
        mFirestore.collection("users").document(mUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                classList = new ArrayList<String>();
                if(e != null){
                    Log.d("Error: ",e.getMessage());
                }
                Map<String, Object> classes = documentSnapshot.getData();
                for(Map.Entry<String,Object>mapEntry : classes.entrySet()){
                    if(mapEntry.getKey().length() >= 5 && mapEntry.getKey().substring(0,5).equals("class")){
                        Log.d("class", mapEntry.getValue().toString());
                        classList.add(mapEntry.getValue().toString());
                    }
                }
                classAdapter = new ClassAdapter(classList);
                classRecyclerView = (RecyclerView) findViewById(R.id.class_list);
                classRecyclerView.setHasFixedSize(true);
                classRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                classRecyclerView.setAdapter(classAdapter);
                classAdapter.notifyDataSetChanged();
            }
        });
    }
}
