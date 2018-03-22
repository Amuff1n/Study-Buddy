package com.studybuddy.studybuddy;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.test.ActivityInstrumentationTestCase;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Kolby on 3/22/2018.
 */
public class SetUpAccountTest extends ActivityInstrumentationTestCase2<SetUpAccount> {
    private SetUpAccount mSetUpActivity;
    private Button confirm;
    private EditText firstName;
    private EditText lastName;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;

    public SetUpAccountTest() {
        super(SetUpAccount.class);
    }

    @Before
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mSetUpActivity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword("jaece.yeray@carins.io", "thisisonlyatest");
        confirm = mSetUpActivity.findViewById(R.id.ConfirmAccount);
        firstName = mSetUpActivity.findViewById(R.id.FirstName);
        lastName = mSetUpActivity.findViewById(R.id.LastName);
        mFirestore = FirebaseFirestore.getInstance();

        assertNotNull(confirm);
        assertNotNull(firstName);
        assertNotNull(lastName);
        assertNotNull(mAuth);
    }

    //TODO these tests aren't working correctly, not creating or deleting database doc
    @Test
    public void testOnCreate() throws Exception {
        mSetUpActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                firstName.setText("UNIT");
                lastName.setText("TEST");
                confirm.performClick();
            }
        });

        assertNotNull(mFirestore.collection("users").document(mAuth.getUid()));
    }

    @Test
    public void testDelete() throws Exception {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mSetUpActivity.delete();
        assertNull(mFirestore.collection("users").document(mAuth.getUid()));
    }

}