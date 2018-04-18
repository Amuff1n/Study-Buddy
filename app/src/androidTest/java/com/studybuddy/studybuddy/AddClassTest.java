package com.studybuddy.studybuddy;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kyle on 4/13/18.
 */

public class AddClassTest extends ActivityInstrumentationTestCase2<AddClasses> {

    private AddClasses addClasses;
    private FirebaseUser mUser;
    private FirebaseFirestore mFireStore;
    private Button done;
    private ImageButton add;
    private EditText classes;
    private TextView cancelAdd;

    public AddClassTest(){
        super(AddClasses.class);
    }

    @Before
    @Override
    public void setUp() throws Exception{
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        addClasses = getActivity();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFireStore = FirebaseFirestore.getInstance();
        done = addClasses.findViewById(R.id.classesAdded);
        add = addClasses.findViewById(R.id.addClass);
        classes = addClasses.findViewById(R.id.userClasses);
        cancelAdd = addClasses.findViewById(R.id.CancelAdd);

    }

    @Test
    public void testAddClasses() throws Exception{
        addClasses.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                classes.setText("testClass1");
                add.performClick();
                classes.setText("testClass2");
                add.performClick();
                done.performClick();
            }
        });
        Thread.sleep(2000);
        final DocumentReference document = mFireStore.collection("users").document(mUser.getUid());
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                String test1 = documentSnapshot.get("classtestClass1").toString();
                String test2 = documentSnapshot.get("classtestClass2").toString();
                assertTrue(test1.equals("testClass1"));
                assertTrue(test2.equals("testClass2"));
                Map<String, Object> delete = new HashMap<>();
                delete.put("classtestClass1", FieldValue.delete());
                delete.put("classtestClass2", FieldValue.delete());
                document.update(delete);
            }
        });
    }
}
