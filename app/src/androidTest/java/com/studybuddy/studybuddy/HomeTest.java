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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Kolby on 4/3/2018.
 */
public class HomeTest extends ActivityInstrumentationTestCase2<Home> {
    private Home mHome;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    private MenuItem mItem;


    public HomeTest() {
        super(Home.class);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mHome = getActivity();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword("kolby.rottero@gmail.com", "thisisanothertest");
        Thread.sleep(2000);
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();

        mItem = new MenuItem() {
            @Override
            public int getItemId() {
                return R.id.nav_logout;
            }

            @Override
            public int getGroupId() {
                return 0;
            }

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public MenuItem setTitle(CharSequence title) {
                return null;
            }

            @Override
            public MenuItem setTitle(int title) {
                return null;
            }

            @Override
            public CharSequence getTitle() {
                return null;
            }

            @Override
            public MenuItem setTitleCondensed(CharSequence title) {
                return null;
            }

            @Override
            public CharSequence getTitleCondensed() {
                return null;
            }

            @Override
            public MenuItem setIcon(Drawable icon) {
                return null;
            }

            @Override
            public MenuItem setIcon(int iconRes) {
                return null;
            }

            @Override
            public Drawable getIcon() {
                return null;
            }

            @Override
            public MenuItem setIntent(Intent intent) {
                return null;
            }

            @Override
            public Intent getIntent() {
                return null;
            }

            @Override
            public MenuItem setShortcut(char numericChar, char alphaChar) {
                return null;
            }

            @Override
            public MenuItem setNumericShortcut(char numericChar) {
                return null;
            }

            @Override
            public char getNumericShortcut() {
                return 0;
            }

            @Override
            public MenuItem setAlphabeticShortcut(char alphaChar) {
                return null;
            }

            @Override
            public char getAlphabeticShortcut() {
                return 0;
            }

            @Override
            public MenuItem setCheckable(boolean checkable) {
                return null;
            }

            @Override
            public boolean isCheckable() {
                return false;
            }

            @Override
            public MenuItem setChecked(boolean checked) {
                return null;
            }

            @Override
            public boolean isChecked() {
                return false;
            }

            @Override
            public MenuItem setVisible(boolean visible) {
                return null;
            }

            @Override
            public boolean isVisible() {
                return false;
            }

            @Override
            public MenuItem setEnabled(boolean enabled) {
                return null;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public boolean hasSubMenu() {
                return false;
            }

            @Override
            public SubMenu getSubMenu() {
                return null;
            }

            @Override
            public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
                return null;
            }

            @Override
            public ContextMenu.ContextMenuInfo getMenuInfo() {
                return null;
            }

            @Override
            public void setShowAsAction(int actionEnum) {

            }

            @Override
            public MenuItem setShowAsActionFlags(int actionEnum) {
                return null;
            }

            @Override
            public MenuItem setActionView(View view) {
                return null;
            }

            @Override
            public MenuItem setActionView(int resId) {
                return null;
            }

            @Override
            public View getActionView() {
                return null;
            }

            @Override
            public MenuItem setActionProvider(ActionProvider actionProvider) {
                return null;
            }

            @Override
            public ActionProvider getActionProvider() {
                return null;
            }

            @Override
            public boolean expandActionView() {
                return false;
            }

            @Override
            public boolean collapseActionView() {
                return false;
            }

            @Override
            public boolean isActionViewExpanded() {
                return false;
            }

            @Override
            public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
                return null;
            }
        };

        assertNotNull(mAuth);
        assertNotNull(mUser);
        assertNotNull(mFirestore);
    }

    @Test
    public void testSignOut() throws Exception {
        mHome.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHome.onNavigationItemSelected(mItem);
            }
        });

        Thread.sleep(2000);
        mAuth = FirebaseAuth.getInstance();
        assertNull(mAuth.getCurrentUser());
    }

    @Test
    public void testGroupExistence() throws Exception {
        //assumes there is always at least one study group in existence
        CollectionReference collectionReference = mFirestore.collection("study_groups");
        Query query = collectionReference.orderBy("creationTime").limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                assertTrue(task.isSuccessful());
            }
        });
    }

    @Test
    public void testGroupInfoSingle() throws Exception {
        CollectionReference collectionReference = mFirestore.collection("study_groups");
        Query query = collectionReference.orderBy("creationTime").limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d("Instrumented Test", document.getId() + " => " + document.getData());
                        assertNotNull(document.get("class"));
                        assertNotNull(document.get("location"));
                        assertNotNull(document.get("user"));

                    }
                }
            }
        });
    }

    @Test
    public void testGroupInfoMultiple() throws Exception {
        //assumes there are at least 3 groups in existence
        CollectionReference collectionReference = mFirestore.collection("study_groups");
        Query query = collectionReference.orderBy("creationTime").limit(3);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d("Instrumented Test", document.getId() + " => " + document.getData());
                        assertNotNull(document.get("class"));
                        assertNotNull(document.get("location"));
                        assertNotNull(document.get("user"));

                    }
                }
            }
        });
    }
}