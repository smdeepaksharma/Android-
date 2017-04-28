package com.sdsu.deepak.homework2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import java.lang.reflect.Field;
import java.text.DateFormatSymbols;

/**
 * Created by Deepak on 2/8/2017.
 */

public class DateActivity extends AppCompatActivity {

    private String birthdate;
    Button mDoneButton;
    Button mCancelButton;
    DatePicker datePicker;
    private static final String BIRTHDATE_RETURN_KEY  = "birthdate";

    @Override
    public void onCreate(Bundle savedState){
        super.onCreate(savedState);
        setContentView(R.layout.activity_date);
        mDoneButton = (Button) findViewById(R.id.done_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        // Making year picker visible
        try{
            Field year = datePicker.getClass().getDeclaredField("mYearPicker");
            year.setAccessible(true);
        }catch(NoSuchFieldException e){
            Log.d("NoSuchField","No Such Field");
        }
        /*
         * This method is invoked when user click DONE button after selecting the date.
         * The selected date is sent back to the Person Activity through the intent
         */
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Log.i("DatePicker","Inside Done Button Listener");
                int day = datePicker.getDayOfMonth();
                int year = datePicker.getYear();
                String month = getMonthName(datePicker.getMonth());
                birthdate = day+" "+month+" "+year;
                Intent toPassBack = getIntent();
                toPassBack.putExtra(BIRTHDATE_RETURN_KEY, birthdate);
                setResult(RESULT_OK, toPassBack);
                finish();
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toPassBack = getIntent();
                setResult(RESULT_CANCELED, toPassBack);
                finish();
            }
        });
    }
    /**
     * This method is used to get the month name of the selected date
     * @param num
     * @return
     */
    String getMonthName(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }
}

