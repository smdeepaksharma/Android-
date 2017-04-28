package com.sdsu.deepak.assignment1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class  MainActivity extends AppCompatActivity {

    protected int clickCount = 0;
    protected int backgroundCount = 0;
    TextView mBackgroundCount;
    TextView mButtonCount;
    private static final String TAG = "MainActivity";
    private static final String CLICK_COUNT = "Click_Count";
    private static final String BACKGROUND_COUNT = "Background_Count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonCount = (TextView) findViewById(R.id.button_count);
        mBackgroundCount = (TextView) findViewById(R.id.background_count);
        Button button = (Button) findViewById(R.id.button);
        // OnClickListener to identify click event of the button
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clickCount += 1;
                mButtonCount.setText(String.format("%d",clickCount));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        if (isChangingConfigurations()) {
            Log.i(TAG, "Changing configuration");
        } else {
            ++backgroundCount;
        }
    }
    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }
    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        mBackgroundCount.setText(String.format("%d",backgroundCount));
    }
    // This method stores values of buttonCount and backgroundCount before activity is destroyed
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSavedInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt(CLICK_COUNT,clickCount);
        outState.putInt(BACKGROUND_COUNT,backgroundCount);
    }
    // Restoring values of buttonCount and backgroundCount
    @Override
    protected void onRestoreInstanceState(Bundle savedState)
    {
        Log.i(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedState);
        clickCount = savedState.getInt(CLICK_COUNT);
        backgroundCount = savedState.getInt(BACKGROUND_COUNT);
        mBackgroundCount.setText(String.format("%d",backgroundCount));
        mButtonCount.setText(String.format("%d",clickCount));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart");
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"onDestroy");
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
            Log.i(TAG,"App is Visible");
        else
            Log.i(TAG,"App is not visible");
    }
}