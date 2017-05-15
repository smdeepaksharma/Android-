package com.sdsu.deepak.campusjobs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * This activity is used as a universal logout helper
 * When the user logs out of the application, an intent
 * is sent to this activity, clearing all the activities previously
 * added to the back stack
 */
public class CloseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
    }
}
