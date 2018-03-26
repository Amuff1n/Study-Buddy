package com.studybuddy.studybuddy;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

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

        assertNotNull(mAuth.getCurrentUser());
    }

    @Test
    public void testEmptyFields() throws Exception {
        mCreateAccount.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCreateAccountBtn.performClick();
            }
        });

        assertNull(mAuth.getCurrentUser());
    }
}