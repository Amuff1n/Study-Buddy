package com.studybuddy.studybuddy;

import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    private FirebaseUser mUser;

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

    //Sleeps could probably be replaced with some sort of listener
    @Test
    public void testOnClickSignInEmail() throws Exception {
        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEmailField.setText("kolby.rottero@gmail.com");
                mPasswordField.setText("thisisanothertest");
                mSignInEmail.performClick();

            }
        });
        Thread.sleep(2000);
        mAuth = FirebaseAuth.getInstance();
        assertNotNull(mAuth.getCurrentUser());
        mAuth.signOut();
    }

    //Should we still have google sign-in?
    /*
    @Test
    public void testOnClickSignInGoogle() throws Exception {
        mSignInGoogle.performClick();

        Thread.sleep(2000);
        mAuth = FirebaseAuth.getInstance();
        assertNotNull(mAuth.getCurrentUser());
        mAuth.signOut();
    }
    */
}