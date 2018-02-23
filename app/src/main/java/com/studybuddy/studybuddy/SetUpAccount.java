package com.studybuddy.studybuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SetUpAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_account);
    }
    //override default back button
    //todo add cancel button
    @Override
    public void onBackPressed(){}
}
