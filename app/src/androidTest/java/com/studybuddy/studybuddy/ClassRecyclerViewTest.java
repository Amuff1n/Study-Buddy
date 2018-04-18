package com.studybuddy.studybuddy;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.studybuddy.studybuddy.RecyclerView.ClassAdapter;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kyle on 4/13/18.
 */

public class ClassRecyclerViewTest extends ActivityInstrumentationTestCase2<ClassRecyclerView>{

    private ClassRecyclerView recyclerView;
    private EditText addClassText;
    private ImageButton addClassButton;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private ImageButton deleteButton;

    public ClassRecyclerViewTest(){
        super(ClassRecyclerView.class);
    }

    @Before
    @Override
    public void setUp() throws Exception{
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        recyclerView = getActivity();
        addClassText = recyclerView.findViewById(R.id.add_class_text);
        addClassButton = recyclerView.findViewById(R.id.add_class_button);
        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        deleteButton = recyclerView.findViewById(R.id.delete_class);
    }

    @Test
    public void testAdd() throws Exception{
        recyclerView.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addClassText.setText("test");
                addClassButton.performClick();
            }
        });
        Thread.sleep(2000);
        final DocumentReference document = mFirestore.collection("users").document(mUser.getUid());
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    String classTest = documentSnapshot.get("classtest").toString();
                    assertTrue(classTest.equals("test"));
                }
            }
        });
    }
    @Test
    public void testDelete() throws Exception{
        recyclerView.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deleteButton.performClick();
            }
        });
        Thread.sleep(2000);
        final DocumentReference document = mFirestore.collection("users").document(mUser.getUid());
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    assertNull(documentSnapshot.get("classtest"));
                }
            }
        });
    }

}
