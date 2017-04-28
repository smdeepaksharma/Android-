package com.sdsu.deepak.homework2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * PersonActivity is the main activity class. Intents are sent from PersonActivity to DateActivity
 * and CountryActivity when required
 * @author Deepak
 */
public class PersonActivity extends AppCompatActivity  {

    String firstName, familyName, email, birthdate, country, state, phone, age;
    // Button Views
    Button mBirthdayButton;
    Button mCountryButton;
    Button mConfirmButton;
    //Text Views
    TextView mCountry;
    TextView mBirthdate;
    TextView mFirstName;
    TextView mFamilyName;
    TextView mEmail;
    TextView mPhone;
    TextView mAge;
    TextView mState;
    private static String TAG = "Person Activity";
    private static final int COUNTRY_INTENT_REQUEST = 123;
    private static final int DATE_INTENT_REQUEST = 555;
    private static final String PERSON_DATA="Person Activity Data";
    //String constants to save app data
    private static final String FIRST_NAME = "First Name";
    private static final String FAMILY_NAME = "Family Name";
    private static final String EMAIL = "Email";
    private static final String PHONE = "Phone";
    private static final String AGE = "Age";
    private static final String BIRTH_DATE = "Birth Date";
    private static final String COUNTRY = "Country ";
    private static final String STATE = "State";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Inside onCreate()");
        setContentView(R.layout.activity_person);

        // Buttons
        mBirthdayButton = (Button) findViewById(R.id.birthday_button);
        mCountryButton = (Button) findViewById(R.id.country_select_button);
        mConfirmButton =(Button) findViewById(R.id.confirm);

        // Text Views
        mCountry = (TextView) findViewById(R.id.country);
        mBirthdate = (TextView) findViewById(R.id.birthday);
        mFirstName = (TextView) findViewById(R.id.first_name);
        mFamilyName = (TextView) findViewById(R.id.family_name);
        mAge = (TextView) findViewById(R.id.age);
        mPhone = (TextView) findViewById(R.id.phone);
        mEmail = (TextView) findViewById(R.id.email);

        // SharedPreferences to store data (using getPreferences since there is only one SharedPreference used)
        SharedPreferences preferences = getPreferences(0);
        if(preferences!=null) {
        // Retrieving values stores n sharedPreference
        firstName = preferences.getString(FIRST_NAME,null);
        familyName = preferences.getString(FAMILY_NAME,null);
        age = preferences.getString(AGE,null);
        email = preferences.getString(EMAIL,null);
        phone = preferences.getString(PHONE,null);
        country = preferences.getString(COUNTRY,null);
        state = preferences.getString(STATE,null);
        birthdate = preferences.getString(BIRTH_DATE,null);
            mFirstName.setText(firstName);
            mFamilyName.setText(familyName);
            mAge.setText(age);
            mEmail.setText(email);
            mPhone.setText(phone);
            mCountry.setText(country);
            mBirthdate.setText(birthdate);
        }
        // Sending intent to DateActivity
        mBirthdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent datePicker = new Intent(view.getContext(),DateActivity.class);
                startActivityForResult(datePicker,DATE_INTENT_REQUEST);
            }
        });
        // Sending intent to CountryActivity
        mCountryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent countryPicker = new Intent(view.getContext(),CountryActivity.class);
                startActivityForResult(countryPicker, COUNTRY_INTENT_REQUEST);
            }
        });
        /* This function is invoked when the user clicks the DONE button
        after entering the personal details */
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reading values entered by the user
                firstName = mFirstName.getText().toString();
                familyName = mFamilyName.getText().toString();
                age = mAge.getText().toString();
                email = mEmail.getText().toString();
                phone = mPhone.getText().toString();
                country = mCountry.getText().toString();
                birthdate = mBirthdate.getText().toString();
                // Storing the values in Shared Preferences
                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(FIRST_NAME,firstName);
                editor.putString(FAMILY_NAME,familyName);
                editor.putString(AGE,age);
                editor.putString(EMAIL,email);
                editor.putString(BIRTH_DATE,birthdate);
                editor.putString(PHONE,phone);
                editor.putString(COUNTRY,country);
                editor.commit();
                Toast success = Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT);
                success.show();
            }
        });
    }
    /*
    This method is invoked when the intent is returned
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COUNTRY_INTENT_REQUEST) {
            switch (resultCode) {
                case RESULT_OK:
                    country = data.getStringExtra("Country");
                    state = data.getStringExtra("State");
                    mCountry.setText(country+", "+state);
                    break;
                case RESULT_CANCELED:
                    break;
            }
        }
        else if(requestCode == DATE_INTENT_REQUEST) {
            switch (resultCode) {
                case RESULT_OK:
                    mBirthdate.setText(data.getStringExtra("birthdate"));
                    break;
                case RESULT_CANCELED:
                    break;
            }
        }
    }
}
