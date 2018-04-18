package com.studybuddy.studybuddy.ClassList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.studybuddy.studybuddy.Home;
import com.studybuddy.studybuddy.R;
import com.studybuddy.studybuddy.RecyclerView.ClassPOJO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*public class ClassRecyclerView extends AppCompatActivity {

    FirebaseFirestore mFirestore;
    FirebaseUser mUser;
    List<ClassPOJO> classList = new ArrayList<ClassPOJO>();
    ClassAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_recycler_view);
        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        getClasses();
        RecyclerView recycler = (RecyclerView)findViewById(R.id.classRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClassAdapter(recycler, this, classList);
        recycler.setAdapter(adapter);
        adapter.setClassLoader(new ClassLoader() {
            @Override
            public void onLoadMore() {
                if(classList.size() <= 20){
                    classList.add(null);
                    adapter.notifyItemChanged(classList.size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            classList.remove(classList.size() - 1);
                            adapter.notifyItemRemoved(classList.size());
                        }
                    },5000);
                    adapter.notifyItemChanged(classList.size());
                    adapter.setLoaded();
                }
                else{
                    Toast.makeText(getApplicationContext(), "No more classes", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getClasses(){
        ArrayList<ClassPOJO>l;
        DocumentReference classRef = mFirestore.collection("users").document(mUser.getUid());
        classRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                l = new ArrayList<ClassPOJO>();
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()){
                        Map<String, Object> classes = document.getData();
                        for(Map.Entry<String,Object>mapEntry : classes.entrySet()){
                            if(mapEntry.getKey().toString().length() >= 5 && mapEntry.getKey().toString().substring(0,5).equals("class")){
                                ClassPOJO c = new ClassPOJO(mapEntry.getValue().toString());
                                l.add(c);
                            }
                        }
                    }
                }
            }
        });
        classList = l;
        /*classList = new ArrayList<>();
        for(int i = 0; i < 10; i ++){
            ClassPOJO c = new ClassPOJO("hi" + i);
            classList.add(c);
        }
    }
}*/
