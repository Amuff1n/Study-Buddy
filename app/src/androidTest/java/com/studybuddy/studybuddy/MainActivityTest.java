package com.studybuddy.studybuddy;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Kolby on 3/22/2018.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity mMainActivity;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignInEmail;
    private SignInButton mSignInGoogle;
    private FirebaseAuth mAuth;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mMainActivity = getActivity();
        mEmailField = mMainActivity.findViewById(R.id.email_field);
        mPasswordField = mMainActivity.findViewById(R.id.password_field);
        mSignInEmail = mMainActivity.findViewById(R.id.email_sign_in_button);
        mSignInGoogle = mMainActivity.findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        assertNotNull(mEmailField);
        assertNotNull(mPasswordField);
        assertNotNull(mSignInEmail);
        assertNotNull(mSignInGoogle);
    }

    @Test
    public void testOnClickSignInEmail() throws Exception {

        mEmailField.setText("kolby.rottero@gmail.com");
        mPasswordField.setText("thisisanothertest");
        mSignInEmail.performClick();

        assertNotNull(mAuth.getCurrentUser());

    }

    @Test
    public void testOnClickSignInGoogle() throws Exception {
        mSignInGoogle.performClick();
        assertNotNull(mAuth.getCurrentUser());
    }

}