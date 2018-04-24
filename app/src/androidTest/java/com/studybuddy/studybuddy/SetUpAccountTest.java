package com.studybuddy.studybuddy;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kyle on 4/12/18.
 */

public class SetUpAccountTest extends ActivityInstrumentationTestCase2<SetUpAccount> {
    private SetUpAccount setUpAccount;
    private FirebaseUser mUser;
    private TextView cancel;
    private Button confirm;
    private EditText firstName;
    private EditText lastName;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;


    public SetUpAccountTest(){
        super(SetUpAccount.class);
    }

    @Before
    @Override
    public void setUp() throws Exception{
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        setUpAccount = getActivity();
        cancel = setUpAccount.findViewById(R.id.Cancel);
        confirm = setUpAccount.findViewById(R.id.ConfirmAccount);
        firstName = setUpAccount.findViewById(R.id.FirstName);
        lastName = setUpAccount.findViewById(R.id.LastName);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        assertNotNull(cancel);
        assertNotNull(confirm);
        assertNotNull(firstName);
        assertNotNull(lastName);
    }

    @Test
    public void testCreate() throws Exception{
        setUpAccount.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                firstName.setText("firstTest");
                lastName.setText("lastTest");
                confirm.performClick();
            }
        });
        Thread.sleep(2000);
        final DocumentReference document = mFirestore.collection("users").document(mUser.getUid());
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    String firstNameTest = documentSnapshot.get("firstName").toString();
                    String lastNameTest = documentSnapshot.get("lastName").toString();
                    assertTrue(firstNameTest.equals("firstTest"));
                    assertTrue(lastNameTest.equals("lastTest"));
                    Map<String, Object> update = new HashMap<>();
                    update.put("firstName", "test");
                    update.put("lastName", "test");
                    document.update(update);
                }
            }
        });
    }

}

