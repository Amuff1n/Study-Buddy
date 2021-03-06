package com.studybuddy.studybuddy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateGroup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    private Spinner mClass;
    private Spinner mLocation;
    private EditText mDescription;
    private EditText mTime;
    private Button mCreateGroup;
    private ArrayList<String> classList;

    private static final String TAG = "GoogleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        mClass = findViewById(R.id.group_class_field);
        mLocation = findViewById(R.id.group_location_field);
        mDescription = findViewById(R.id.group_desc_field);
        mCreateGroup = findViewById(R.id.create_group_button);
//        mTime = findViewById(R.id.scheduled_time);

        //Have to make an adapter to fill in drop down menu items with string array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.locations_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Drop down list for classes
        if (mUser != null) {
            getClasses();
        }

        mLocation.setAdapter(adapter);
        mLocation.setOnItemSelectedListener(this);

        mCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String groupClass = mClass.getSelectedItem().toString();
                String groupLocation = mLocation.getSelectedItem().toString();
                String groupDesc = mDescription.getText().toString();
                String scheduleTime = "00:00";
                boolean geoChecker = false;
                if(groupLocation.equals("Custom Location"))
                    geoChecker = true;
                createGroup(groupClass, groupLocation, groupDesc,scheduleTime,geoChecker);

                //Send back code to Home before finishing
                //Used for automatic refresh
                Intent intent = new Intent();
                intent.putExtra("completed", true); //isn't used by Home right now
                setResult(Activity.RESULT_OK, intent);
                finish();


            }
        });
    }

    //Methods for getting selected drop down list item
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    public boolean createGroup(String groupClass, String groupLocation, String groupDesc, String scheduleTime, Boolean geoChecker) {
        if(TextUtils.isEmpty(scheduleTime)){
            scheduleTime = "00:00";
        }
        if(TextUtils.isEmpty(groupClass)){
            Toast.makeText(getApplicationContext(), "Enter a class", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(TextUtils.isEmpty(groupLocation)){
            Toast.makeText(getApplicationContext(), "Enter a location", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(TextUtils.isEmpty(groupDesc)){
            Toast.makeText(getApplicationContext(), "Add a description", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            Map<String, Object> groupMap = new HashMap<>();
            groupMap.put("class", groupClass);
            groupMap.put("description", groupDesc);
            groupMap.put("creationTime", FieldValue.serverTimestamp());
            //groupMap.put("ScheduledTime", scheduleTime);
            groupMap.put("user", mAuth.getUid());
            groupMap.put("index", 1); //number of users

            if(geoChecker) {
                ActivityCompat.requestPermissions(CreateGroup.this,new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,},
                        123);
                GPStracker gps = new GPStracker(getApplicationContext());
                Location myLoc = gps.getLocation();
                if(myLoc != null){
                    double latitude = myLoc.getLatitude();
                    double longitute = myLoc.getLongitude();
                    Log.d("Coordinates",String.valueOf(latitude + " , " +longitute));
                    GeoPoint coordinateData = new GeoPoint(latitude,longitute);
                    groupMap.put("latlng",coordinateData);
                }
            }
            groupMap.put("location", groupLocation);

            mFirestore.collection("study_groups");

            groupMap.put("maxUserIndex", 1); //max index of user key

            Task<DocumentReference> documentReferenceTask = mFirestore.collection("study_groups")

                    .add(groupMap)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });

            Toast.makeText(getApplicationContext(), "Study group created!", Toast.LENGTH_SHORT).show();

            return true;
        }
    }
    //pulls object of all data in document then finds all classes and puts them into an array to
    //use for the drop down menu
    public void getClasses(){
        DocumentReference classRef = mFirestore.collection("users").document(mUser.getUid());
        classRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                classList = new ArrayList<String>();
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()){
                        Map<String, Object> classes = document.getData();
                        for(Map.Entry<String,Object>mapEntry : classes.entrySet()){
                            if(mapEntry.getKey().toString().length() >= 5 && mapEntry.getKey().toString().substring(0,5).equals("class")){
                                classList.add(mapEntry.getValue().toString());
                            }
                        }
                    }
                }
                if(classList.size() == 0){
                    Toast.makeText(getApplicationContext(), "You have not added any classes yet", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    return;
                }
                String[] classArray = new String[classList.size()];
                classArray = classList.toArray(classArray);
                ArrayAdapter<String> classAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, classArray);
                mClass.setAdapter(classAdapter);
            }
        });
    }
}
