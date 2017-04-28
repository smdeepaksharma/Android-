package com.sdsu.deepak.homework2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Deepak on 2/9/2017.
 * CountryActivity implements CountrySelectionListener and StateSelectionListener
 * This activity hosts the country and state list fragments
 */
public class CountryActivity extends AppCompatActivity implements CountryList.CountrySelectionListener, StateListFragment.StateSelectionListener{
    // String constants
    private static final String COUNTRY_LIST_FILE_NAME = "countries";
    public static final String COUNTRY_LIST_KEY = "CountryList";
    private static final String COUNTRY_INTENT_RETURN_KEY= "Country";
    private static final String STATE_INTENT_RETURN_KEY ="State";
    private static final String COUNTRY_ERROR_MESSAGE ="Please select country!";
    private static final String STATE_ERROR_MESSAGE ="Please select state!";
    private String countrySelected;
    private String stateSelected;
    // Button viwes
    Button mButtonCountry;
    Button mCancelButton;
    // List for storing country and state names
    ArrayList<String> countryNamesList;
    ArrayList<String> stateNamesList;
    FragmentTransaction fragmentTransaction;
    Toast toast;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);
        mButtonCountry =(Button)findViewById(R.id.done_country);
        mCancelButton =(Button) findViewById(R.id.cancel_country);
        countryNamesList = new ArrayList<String>();
        stateNamesList = new ArrayList<>();
        String fileInput;
        try {
            // Reading country names from assets
            InputStream countriesFile = getAssets().open(COUNTRY_LIST_FILE_NAME);
            BufferedReader in = new BufferedReader( new InputStreamReader(countriesFile));
            while((fileInput=in.readLine())!=null) {
                countryNamesList.add(fileInput);
                Log.i("Country Activity :",fileInput);
            }

            // Creating a bundle of country names
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(COUNTRY_LIST_KEY,countryNamesList);

            // Passing country names list to CountryList Fragment
            CountryList countryList = new CountryList();
            countryList.setArguments(bundle);
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.country_list_container, countryList);
            fragmentTransaction.commit();
        } catch (IOException e) {
            Log.e("rew", "read Error", e);
        }
        // Listener to capture DONE button clicks on country layout
        mButtonCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("rew", "Back");
                if(countrySelected!=null && stateSelected!=null){
                Intent toPassBack = getIntent();
                toPassBack.putExtra(COUNTRY_INTENT_RETURN_KEY, countrySelected);
                toPassBack.putExtra(STATE_INTENT_RETURN_KEY,stateSelected);
                setResult(RESULT_OK, toPassBack);
                finish();}
                // Displaying relevant error messages when user doesn't select a country or state
                else {
                    if(countrySelected==null){
                        toast = Toast.makeText(getApplicationContext(), COUNTRY_ERROR_MESSAGE, Toast.LENGTH_SHORT);
                    } else {
                        toast = Toast.makeText(getApplicationContext(), STATE_ERROR_MESSAGE, Toast.LENGTH_SHORT);
                    }
                    toast.show();
                }
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent passBack = getIntent();
                setResult(RESULT_CANCELED,passBack);
                finish();
            }
        });
    }

    /**
     * This function is invoked by country fragment once the user selects the country name
     * @param countryName
     */
    @Override
    public void onCountrySelection(String countryName) {
        this.countrySelected = countryName;
    }

    /**
     * this function is invoked when the user selects the state name
     * @param stateName
     */
    @Override
    public void onStateSelection(String stateName) {
        this.stateSelected = stateName;
    }
}
