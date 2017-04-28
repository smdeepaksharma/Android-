package com.sdsu.deepak.mytabapplication;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText nicknameEditTextView ;
    EditText emailEditTextView ;
    EditText passwordEditTextView;
    Spinner countryListSpinnerView;
    Spinner stateListSpinnerView;
    Spinner yearListSpinnerView;
    EditText cityEditTextView;
    Button addLocation;
    Button resetButton;
    TextView latitude_value;
    TextView longitude_value;
    TextView coordinates;

    ArrayAdapter<String> countryListAdapter;
    ArrayAdapter<String> stateListAdapter;
    ArrayAdapter<String> yearListAdapter;

    ArrayList<String> countryList = new ArrayList<>();
    ArrayList<String> stateList = new ArrayList<>();;
    ArrayList<String> yearList = new ArrayList<>();;

    private ProgressBar progressBar;
    private FirebaseAuth auth;
    int check = 0;
    String selectedCountryName, selectedStateName, yearSelected;
    String email, password, nickName;

    private static String TAG = "SignUp";
    private static int LOCATION_CODE = 111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        getCountryDataFromServer();

        if(yearList.isEmpty()){
            yearList.add("Select");
            for(int i = 2017; i >= 1790; i--){
                yearList.add(String.valueOf(i));
            }
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        nicknameEditTextView = (EditText) findViewById(R.id.nick_name);
        emailEditTextView = (EditText) findViewById(R.id.email);
        passwordEditTextView = (EditText) findViewById(R.id.password);
        countryListSpinnerView = (Spinner) findViewById(R.id.country);
        stateListSpinnerView = (Spinner) findViewById(R.id.state);
        yearListSpinnerView = (Spinner) findViewById(R.id.year);
        cityEditTextView = (EditText) findViewById(R.id.city);
        latitude_value = (TextView) findViewById(R.id.latitude_val);
        longitude_value = (TextView) findViewById(R.id.longitude_val);
        coordinates = (TextView) findViewById(R.id.coordinates);

        countryListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countryList);
        countryListAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        countryListSpinnerView.setAdapter(countryListAdapter);
        countryListSpinnerView.setOnItemSelectedListener(this);

        stateListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,stateList );
        stateListAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        stateListSpinnerView.setAdapter(stateListAdapter);
        stateListSpinnerView.setOnItemSelectedListener(this);

        yearListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearList );
        yearListAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        yearListSpinnerView.setAdapter(yearListAdapter);
        yearListSpinnerView.setOnItemSelectedListener(this);

        resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countryListSpinnerView.setSelection(0);
                stateListSpinnerView.setSelection(0);
                yearListSpinnerView.setSelection(0);
                nicknameEditTextView.setText("");
                passwordEditTextView.setText("");
                cityEditTextView.setText("");
                latitude_value.setText("");
                longitude_value.setText("");
                coordinates.setText("");
            }
        });
        addLocation = (Button) findViewById(R.id.add_location);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : add map fragment
                Intent mapIntent = new Intent(SignUpActivity.this, UserLocation.class);
                startActivityForResult(mapIntent,LOCATION_CODE);
            }
        });

        Button registerUser = (Button) findViewById(R.id.submit_button);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditTextView.getText().toString().trim();
                String password = passwordEditTextView.getText().toString().trim();
                final String nickName = nicknameEditTextView.getText().toString().trim();

                if (isInputValid()) {
                   // progressBar.setVisibility(View.VISIBLE);
                    //create user
                    SaveUser user = new SaveUser();
                    user.setNickname(nicknameEditTextView.getText().toString());
                    user.setPassword(passwordEditTextView.getText().toString());
                    user.setCountry(selectedCountryName);
                    user.setState(selectedStateName);
                    user.setCity(cityEditTextView.getText().toString()) ;
                    user.setYear(Integer.parseInt(yearSelected));

                    if(latitude_value.getText().toString().equals("") ||longitude_value.getText().toString().equals("") )
                    {
                        Geocoder locator = new Geocoder(getApplicationContext());
                        String location = selectedStateName+", "+selectedCountryName;
                        try {
                            List<Address> possibleAddresses = locator.getFromLocationName(location,1);
                            for (Address approxLocation: possibleAddresses) {
                                if (approxLocation.hasLatitude())
                                    Log.i("rew", "Lat " + approxLocation.getLatitude());
                                if (approxLocation.hasLongitude())
                                    Log.i("rew", "Long " + approxLocation.getLongitude());
                                LatLng latLng = new LatLng(approxLocation.getLatitude(),approxLocation.getLongitude());
                                user.setLatitude(latLng.latitude);
                                user.setLongitude(latLng.longitude);
                            }
                            postUserDataToServer(user);
                        } catch (Exception error) {
                            Toast.makeText(getApplicationContext(),"Invalid Address!",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        user.setLatitude(Double.parseDouble(latitude_value.getText().toString()));
                        user.setLongitude(Double.parseDouble(longitude_value.getText().toString()));
                        postUserDataToServer(user);
                    }

                } else {
                    Toast.makeText(getApplicationContext(),"Enter required fields",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LOCATION_CODE){
            if(resultCode==RESULT_OK){
                double lat = data.getDoubleExtra("Latitude",0.00);
                double lng = data.getDoubleExtra("Longitude",0.00);
                latitude_value.setText(String.valueOf(lat));
                longitude_value.setText(String.valueOf(lng));
                coordinates.setText("Co-ordinates");
            }
            if(resultCode==RESULT_CANCELED){
                Toast.makeText(this,"Canceled",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void addUserToFirebase(){
        email = emailEditTextView.getText().toString();
        password = passwordEditTextView.getText().toString();
        nickName = nicknameEditTextView.getText().toString();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        //progressBar.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                            emailEditTextView.setError("Invalid email id");
                            //TODO : store user details in Bismark
                        } else {
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nickName)
                                    .build();
                            try {
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("SignUpActivity", "User profile updated.");
                                                    addUserToDatabase(SignUpActivity.this,user);
                                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                                else {
                                                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } catch (NullPointerException e) {
                                Log.d("", "");
                            }
                        }
                    }
                });


        }

        private void postUserDataToServer(SaveUser user){
            JSONObject jsonObject = new JSONObject();
            try {
                String userInfo = new Gson().toJson(user,SaveUser.class);
                jsonObject = new JSONObject(userInfo);
            } catch (JSONException error) {
                Log.e("rew", "JSON eorror", error);
            }

            Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        addUserToFirebase();
                    }
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
                public void onErrorResponse(VolleyError error) {
                        int status = error.networkResponse.statusCode;
                    if(status==404){
                        Toast.makeText(getApplicationContext(),"Server Not Found. Try Again",Toast.LENGTH_LONG).show();
                        Log.i("Server Response",error.networkResponse.data.toString());
                    } else if(status==400){
                        Toast.makeText(getApplicationContext(),"User Already Exists",Toast.LENGTH_LONG).show();
                    } else if(status==200){
                        Toast.makeText(getApplicationContext(),"Added successfully!",Toast.LENGTH_LONG).show();
                    } else{
                        Log.d("rew", error.toString());
                    }
                }
            };
                String url = "http://bismarck.sdsu.edu/hometown/adduser";
                JsonObjectRequest postRequest = new JsonObjectRequest(url, jsonObject, success, failure);
                VolleyQueue.instance(this).add(postRequest);
        }


    private void getCountryDataFromServer(){
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                try{
                    if(response.length() > 0){
                        countryList.add("Select");
                        for(int i = 0 ; i < response.length();i++) {
                            countryList.add(new Gson().fromJson(response.get(i).toString(), String.class));
                        }
                        countryListAdapter.notifyDataSetChanged();
                    } else { countryList.clear();
                    }
                }catch(JSONException e){
                    Log.i("rww",e.toString());
                }
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                int status = error.networkResponse.statusCode;
                if(status == 404 || status == -1){
                    Toast errorMessage = Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_SHORT);
                    errorMessage.show();
                }
            }
        };
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, Url.COUNTRY_NAMES, null, success,failure);
        VolleyQueue.instance(getApplicationContext()).add(jsObjRequest);
    }
    private void statePopulate()
    {
        Log.i(TAG,"Inside statePopulate()");
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stateList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateListSpinnerView.setAdapter(adapter);
    }
    private void getStateListFromServer(String selectedCountry) {
        if (!selectedCountry.equalsIgnoreCase("Select")) {
            final ArrayList<String> stateNamesList = new ArrayList<>();
            stateNamesList.add("All");
            Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
                public void onResponse(JSONArray response) {
                    try {
                        stateList.add("All");
                        for (int i = 0; i < response.length(); i++) {
                            stateList.add(response.getString(i));
                        }
                        statePopulate();
                    } catch (JSONException e) {
                        Log.i(TAG, "EmptyList");
                    }
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Log.d("rew", error.toString());
                }
            };
            JsonArrayRequest request = new JsonArrayRequest(Url.STATE_NAMES + selectedCountry, success, failure);
            VolleyQueue.instance(getApplicationContext()).add(request);
        }
    }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
            switch (parent.getId()) {
                case R.id.country:
                    selectedCountryName = parent.getItemAtPosition(position).toString();
                    Log.i(TAG,"Selected Country:"+selectedCountryName);
                    getStateListFromServer(selectedCountryName);
                    break;
                case R.id.state:
                    selectedStateName = parent.getItemAtPosition(position).toString();
                    break;
                case R.id.year:
                    yearSelected = parent.getItemAtPosition(position).toString();
                    break;
            }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private boolean isInputValid(){
        boolean valid = true;
        if (TextUtils.isEmpty(emailEditTextView.getText().toString())) {
            emailEditTextView.setError("Enter email address!");
            valid = false;
        }

        if (TextUtils.isEmpty(passwordEditTextView.getText().toString())) {
            passwordEditTextView.setError("Enter password!");
            valid = false;
        }

        if (passwordEditTextView.getText().toString().length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if(TextUtils.isEmpty(nicknameEditTextView.getText().toString())){
            nicknameEditTextView.setError("Enter Nick Name");
            valid = false;
        }
        return valid;
    }

    public void addUserToDatabase(final Context context, FirebaseUser firebaseUser) {
        Log.i(TAG,"Add users to database");

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        FireBaseUser user = new FireBaseUser(firebaseUser.getUid(),
                firebaseUser.getDisplayName());

        Log.i(TAG,user.displayName);
        database.child(Constants.ARG_USERS)
                .child(firebaseUser.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG,"Added");
                        } else {
                            Log.i(TAG,"Not Added");
                        }
                    }
                });
    }
}