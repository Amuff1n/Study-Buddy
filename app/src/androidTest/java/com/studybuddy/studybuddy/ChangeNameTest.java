package com.studybuddy.studybuddy;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Daniel on 4/10/2018.
 */
public class ChangeNameTest extends ActivityInstrumentationTestCase2<ProfileActivity> {
    private ProfileActivity mProfileActivity;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    private MenuItem mItem;


    public ChangeNameTest() {
        super(ProfileActivity.class);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mProfileActivity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword("kolby.rottero@gmail.com", "thisisanothertest");
        Thread.sleep(2000);
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Test
    public void testSchoolChange() throws Exception {
        // Standard case of a name change
        mProfileActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProfileActivity.changeSchool("UNC");
            }
        });
        Thread.sleep(3000);
        assertEquals("UNC",mProfileActivity.getSchool(mUser));

        mProfileActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProfileActivity.changeSchool("UF");
            }
        });
        Thread.sleep(4000);
        assertEquals("UF",mProfileActivity.getSchool(mUser));//*/
    }

    @Test
    public void testMajorChange() throws Exception {
        // Standard case of a name change
        mProfileActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProfileActivity.changeMajor("Ee");
            }
        });
        Thread.sleep(4000);
        assertEquals("Ee",mProfileActivity.getMajor(mUser));
        Thread.sleep(2000);

        mProfileActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProfileActivity.changeMajor("Cs");
            }
        });
        Thread.sleep(4000);
        assertEquals("Cs",mProfileActivity.getMajor(mUser));
    } 
    
    @Test
    public void testYearChange() throws Exception {
        // Standard case of a name change
        mProfileActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProfileActivity.changeYear("1");
            }
        });
        Thread.sleep(4000);
        assertEquals("1",mProfileActivity.getYear(mUser));
        Thread.sleep(2000);

        mProfileActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProfileActivity.changeYear("3");
            }
        });
        Thread.sleep(4000);
        assertEquals("3",mProfileActivity.getYear(mUser));
    }//*/
}