package com.sdsu.deepak.sdsuhometownlocations;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class UserInformationFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String COUNTRY_LIST_DATA = "Country List";
    private static final String YEAR_LIST_DATA = "Year List";
    private static final int LOCATION_REQUEST = 777;
    String selectedCountryName;
    String selectedStateName = "Select State";
    String yearSelected;
    User currentUser;
    private ArrayList<String> countryList;
    private ArrayList<String> yearList;
    private ArrayList<String> stateList;
    Spinner stateSpinner;
    Spinner countryListSpinner;
    Spinner yearListSpinner;
    Button addButton;
    EditText nickName, password, city;
    TextView countryLabel;
    TextView stateLabel;
    TextView yearLabel, longitude_value,latitude_value, coordinates;
    Button addLocation, resetButton;
    private OnFragmentInteractionListener mListener;

    public UserInformationFragment() {
        // Required empty public constructor
    }

    public static UserInformationFragment newInstance(ArrayList<String> countryNames, ArrayList<String> yearList) {
        UserInformationFragment fragment = new UserInformationFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(COUNTRY_LIST_DATA, countryNames);
        args.putStringArrayList(YEAR_LIST_DATA, yearList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            countryList = getArguments().getStringArrayList(COUNTRY_LIST_DATA);
            yearList = getArguments().getStringArrayList(YEAR_LIST_DATA);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_information, container, false);
        // Populating the country list spinner
        countryListSpinner = (Spinner) v.findViewById(R.id.country_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, countryList);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        countryListSpinner.setAdapter(adapter);
        countryListSpinner.setOnItemSelectedListener(this);

        // Population the year list spinner
        yearListSpinner = (Spinner) v.findViewById(R.id.year_spinner);
        final ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        yearListSpinner.setAdapter(yearAdapter);
        yearListSpinner.setOnItemSelectedListener(this);

        // setting up state spinner
        stateSpinner = (Spinner) v.findViewById(R.id.state_spinner);
        stateSpinner.setOnItemSelectedListener(this);

        // Obtaining reference to EditText views
        nickName = (EditText) v.findViewById(R.id.nick_name);
        password = (EditText) v.findViewById(R.id.password);
        city = (EditText) v.findViewById(R.id.city);

        countryLabel = (TextView) v.findViewById(R.id.country_label);
        stateLabel = (TextView) v.findViewById(R.id.state_label);
        yearLabel = (TextView) v.findViewById(R.id.year_label);
        addButton = (Button) v.findViewById(R.id.submit_button);
        addLocation = (Button) v.findViewById(R.id.add_location);
        resetButton = (Button) v.findViewById(R.id.reset_button);
        longitude_value = (TextView) v.findViewById(R.id.longitude_val);
        latitude_value = (TextView) v.findViewById(R.id.latitude_val);
        coordinates = (TextView) v.findViewById(R.id.coordinates);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentUser = new User();
        stateList = new ArrayList<>();
        stateList.add("Select State");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()){
                    User user = new User();
                    user.setNickname(nickName.getText().toString());
                    user.setPassword(password.getText().toString());
                    user.setCountry(selectedCountryName);
                    user.setState(selectedStateName);
                    user.setCity(city.getText().toString()) ;
                    user.setYear(Integer.parseInt(yearSelected));
                    if(latitude_value.getText().toString().equals("") ||longitude_value.getText().toString().equals("") )
                    {
                        Geocoder locator = new Geocoder(getContext());
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
                           Toast.makeText(getContext(),"Invalid Address!",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        user.setLatitude(Double.parseDouble(latitude_value.getText().toString()));
                        user.setLongitude(Double.parseDouble(longitude_value.getText().toString()));
                        postUserDataToServer(user);
                    }
                }
                else{
                    Toast.makeText(getContext(),"Enter the required fields",Toast.LENGTH_LONG).show();
                }
            }
        });
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent map = new Intent(getActivity(),MapsActivity.class);
                startActivityForResult(map,LOCATION_REQUEST);
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countryListSpinner.setSelection(0);
                stateSpinner.setSelection(0);
                yearListSpinner.setSelection(0);
                nickName.setText("");
                password.setText("");
                city.setText("");
                latitude_value.setText("");
                longitude_value.setText("");
                coordinates.setText("");
            }
        });
    }

    private void postUserDataToServer(User user){
        JSONObject jsonObject = new JSONObject();
        try {
            String userInfo = new Gson().toJson(user,User.class);
            jsonObject = new JSONObject(userInfo);
        } catch (JSONException error) {
            Log.e("rew", "JSON eorror", error);
        }
        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response!=null){
                    Toast.makeText(getContext(),"Added Successfully!",Toast.LENGTH_SHORT).show();
                }
                //Process response here
                Log.i("rew", response.toString());
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("rew", "post fail " + new String(error.networkResponse.data));
                int status = error.networkResponse.statusCode;
                if(status==404){
                    Toast.makeText(getContext(),"Server Not Found. Try Again",Toast.LENGTH_LONG).show();
                    Log.i("Server Response",error.networkResponse.data.toString());
                } else if(status==400){
                    Toast.makeText(getContext(),"User Already Exists",Toast.LENGTH_LONG).show();
                } else if(status==200){
                    Toast.makeText(getContext(),"Added successfully!",Toast.LENGTH_LONG).show();
                } else{
                    Log.d("rew", error.toString());
                }
            }
        };
        String url = "http://bismarck.sdsu.edu/hometown/adduser";
        JsonObjectRequest postRequest = new JsonObjectRequest(url, jsonObject, success, failure);
        VolleyQueue.instance(this.getActivity()).add(postRequest);

    }

    /**
     * This method is used to validate the data entered by the user
     * @return TRUE if all the entered fields are valid, FALSE otherwise
     */
    private boolean validateForm(){
        boolean valid = true;
        if(nickName.getText().toString().trim().equalsIgnoreCase("") || nickName.getText()==null){
            nickName.setError("Please enter nick name!");
            valid = false;
        }
        if(password.getText().toString().equalsIgnoreCase("") || password.getText().toString().length() <3){
            password.setError("Invalid Password");
            valid = false;
        }
        if(city.getText().toString().equalsIgnoreCase("")){
            city.setError("Please enter city!");
            valid = false;
        }
        if(city.getText().toString().equalsIgnoreCase("All") || city.getText().toString().equalsIgnoreCase("Select Country")){
            countryLabel.setError("Please select country!");
            valid = false;
        }
        if(selectedCountryName.equalsIgnoreCase("Select Country")){
            countryLabel.setError("Please select country!");
            valid = false;
        }
        if(selectedStateName.equalsIgnoreCase("Select state")){
            stateLabel.setError("Select State");
            valid = false;
        }
        if(yearSelected.equalsIgnoreCase("Select Year")){
            yearLabel.setError("Please select year");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LOCATION_REQUEST){
            if(resultCode==RESULT_OK){
                double lat = data.getDoubleExtra("Latitude",0.00);
                double lng = data.getDoubleExtra("Longitude",0.00);
                latitude_value.setText(String.valueOf(lat));
                longitude_value.setText(String.valueOf(lng));
                coordinates.setText("Co-ordinates");
            }
            if(resultCode==RESULT_CANCELED){
                Toast.makeText(getContext(),"Canceled",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            switch (adapterView.getId()) {
                case R.id.country_spinner:
                    selectedCountryName = adapterView.getItemAtPosition(position).toString();
                    getStateListFromServer(selectedCountryName);
                    countryLabel.setError(null);
                    break;
                case R.id.state_spinner:
                    selectedStateName = adapterView.getItemAtPosition(position).toString();
                    stateLabel.setError(null);
                    break;
                case R.id.year_spinner:
                    yearSelected = adapterView.getItemAtPosition(position).toString();
                    yearLabel.setError(null);
                    break;
            }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        countryLabel.setError(" ");
    }

    private void statePopulate()
    {
        ArrayAdapter adapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, stateList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);
    }

    /**
     * this method is invoked when user selects a Country from the country list
     * @param selectedCountry
     */
    private void getStateListFromServer(String selectedCountry){
        final ArrayList<String> stateNamesList = new ArrayList<>();
        stateNamesList.add("Select State");
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                try{
                    for(int i=0;i<response.length();i++){
                        stateNamesList.add(response.getString(i));
                    }
                    stateList = stateNamesList;
                    statePopulate();
                    Log.i("MainActivity",response.toString());
                }
                catch(JSONException e){
                    Log.i("MainActivity","EmptyList");
                }
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                int status = error.networkResponse.statusCode;
                if(status==404){
                    Log.i("User Info","Server Not Found. Try Again");
                }
                else if(status==400){
                    Toast.makeText(getContext(),"User Already Exists",Toast.LENGTH_LONG).show();
                }
                else if(status==200){
                        Toast.makeText(getContext(),"Added successfully!",Toast.LENGTH_LONG).show();
                }
                else{
                Log.d("rew", error.toString());}
            }
        };
        JsonArrayRequest request = new JsonArrayRequest( Url.STATE_NAMES+selectedCountry, success, failure);
        VolleyQueue.instance(this.getActivity()).add(request);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
