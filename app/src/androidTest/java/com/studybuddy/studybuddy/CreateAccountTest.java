package com.studybuddy.studybuddy;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Kolby on 3/26/2018.
 */
public class CreateAccountTest extends ActivityInstrumentationTestCase2<CreateAccount> {
    private CreateAccount mCreateAccount;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPassword;
    private Button mCreateAccountBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public CreateAccountTest() {
        super(CreateAccount.class);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mCreateAccount = getActivity();
        mEmailField = mCreateAccount.findViewById(R.id.email_);
        mPasswordField = mCreateAccount.findViewById(R.id.password_);
        mConfirmPassword = mCreateAccount.findViewById(R.id.confirm_password);
        mCreateAccountBtn = mCreateAccount.findViewById(R.id.SignUp);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        assertNotNull(mEmailField);
        assertNotNull(mPasswordField);
        assertNotNull(mConfirmPassword);
        assertNotNull(mCreateAccountBtn);
    }

    @Test
    public void testOnCreate() throws Exception {
        mCreateAccount.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEmailField.setText("nubeyili@web2mailco.com");
                mPasswordField.setText("anothertest");
                mConfirmPassword.setText("anothertest");
                mCreateAccountBtn.performClick();
            }
        });
        Thread.sleep(2000);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assertNotNull(mUser);
        mUser.delete(); //delete profile so we can run test again
        mAuth.signOut();
    }

    @Test
    public void testExistingProfile() throws Exception {
        mCreateAccount.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEmailField.setText("kolby.rottero@gmail.com");
                mPasswordField.setText("thisisanothertest");
                mConfirmPassword.setText("thisisanothertest");
                mCreateAccountBtn.performClick();
            }
        });
        Thread.sleep(2000);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assertNull(mUser);
        mAuth.signOut();
    }

    @Test
    public void testEmptyFields() throws Exception {
        mCreateAccount.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCreateAccountBtn.performClick();
            }
        });
        Thread.sleep(2000);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assertNull(mUser);
        mAuth.signOut();
    }

    @Test
    public void testMismatchPasswords() throws Exception {
        mCreateAccount.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEmailField.setText("nubeyili@web2mailco.com");
                mPasswordField.setText("anothertest");
                mConfirmPassword.setText("justanothertest");;
                mCreateAccountBtn.performClick();
            }
        });

        Thread.sleep(2000);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assertNull(mUser);
        mAuth.signOut();
    }
}