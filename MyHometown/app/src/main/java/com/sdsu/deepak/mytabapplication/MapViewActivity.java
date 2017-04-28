package com.sdsu.deepak.mytabapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback, FilterFragment.OnFilterChangeListener, ServerInteraction.ResponseHandler, AdapterView.OnItemSelectedListener{


    private static final String LOG_TAG = "Hometown | MapView";

    private ArrayList<User> mapUsers = new ArrayList<>();
    private ArrayList<String> countryList = new ArrayList<>();
    private ArrayList<String> yearList = new ArrayList<>();
    private ArrayList<String> stateList = new ArrayList<>();
    private ArrayList<FireBaseUser> userForChat = new ArrayList<>();

    private String currentUrl;
    private String currentQuery;
    private GoogleMap mMap;
    private ArrayList<User> userListForMap;
    private LatLng defaultLatLng;
    ProgressDialog progressDialog;
    private ArrayList<User> usersWithoutLatLng;
    ServerInteraction serverInteraction;
    int check = 0;
    Spinner countrySpinner;
    Spinner stateSpinner;
    Spinner yearSpinner;
    Button searchButton;
    int nextId, maxId;
    Button loadMoreButton;
    String selectedCountryName, selectedStateName, yearSelected;
    Boolean isDatabaseEmpty = true, updateAvailable = false;
    // Adapters
    ArrayAdapter<String> countryAdapter;
    ArrayAdapter<String> stateAdapter;
    ArrayAdapter<String> yearAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG,"onCreate()");

        serverInteraction = new ServerInteraction(this);
        serverInteraction.checkDataUpdates();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("updating user list");
        progressDialog.show();
        getAllUsersFromFirebase();
        setContentView(R.layout.activity_map_view);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.user_map);
        mapFragment.getMapAsync(this);


        // Initializations
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        check = 0;

        defaultLatLng = new LatLng(0.0,0.0);

        usersWithoutLatLng = new ArrayList<>();
        userListForMap = new ArrayList<>();
        serverInteraction = new ServerInteraction(this);
        selectedStateName = "All";
        selectedCountryName = "Select";
        yearSelected = "Select";
        // Assigning values sent in intent
        Intent mapIntent = getIntent();
        countryList = mapIntent.getStringArrayListExtra(Keys.COUNTRY_LIST);
        yearList = mapIntent.getStringArrayListExtra(Keys.YEAR_LIST);

        // instances of views
        countrySpinner = (Spinner) findViewById(R.id.country_list_spinner);
        stateSpinner = (Spinner) findViewById(R.id.state_list_spinner);
        yearSpinner = (Spinner) findViewById(R.id.year_spinner);
        searchButton = (Button) findViewById(R.id.search_on_map);
        loadMoreButton = (Button) findViewById(R.id.load_more_button);
        loadMoreButton.setEnabled(false);
        // adapter initializations
        countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countryList);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        countrySpinner.setAdapter(countryAdapter);
        countrySpinner.setOnItemSelectedListener(this);

        stateSpinner.setOnItemSelectedListener(this);

        yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setOnItemSelectedListener(this);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG,"Triggered search button");
                progressDialog.setTitle("Locating...");
                progressDialog.setMessage("Please wait! We're looking for your friends!");
                progressDialog.show();
                loadMoreButton.setEnabled(true);
                Bundle filterBundle = new Bundle();
                filterBundle.putString(Keys.COUNTRY,selectedCountryName);
                filterBundle.putString(Keys.STATE,selectedStateName);
                filterBundle.putString(Keys.YEAR,yearSelected);
                filterBundle.putInt(Keys.NEXT_ID,nextId);
                serverInteraction.getUserData(filterBundle);

            }
        });

        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoreData();
            }
        });
    }


    private void loadMoreData(){
        int nextId;
        progressDialog.setTitle("Locating >>>");
        progressDialog.setMessage("Please wait! We're looking for your friends!");
        progressDialog.show();
        if(userListForMap.size() > 1){
            nextId = userListForMap.get(userListForMap.size()-1).getId();
            Log.i(LOG_TAG,"next to fetch" + nextId);
            Bundle filterBundle = new Bundle();
            filterBundle.putString(Keys.COUNTRY,selectedCountryName);
            filterBundle.putString(Keys.STATE,selectedStateName);
            filterBundle.putString(Keys.YEAR,yearSelected);
            filterBundle.putInt(Keys.NEXT_ID,nextId);
            serverInteraction.getUserData(filterBundle);
        } else {
           Toast.makeText(this,"No more users",Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MapViewActivity.this,
                                        "You have been signed out.",
                                        Toast.LENGTH_LONG)
                                        .show();
                                // Close activity
                                finish();
                            }
                        });
                break;
            case R.id.list_view:
                Intent intent = new Intent(MapViewActivity.this, MainActivity.class);
                intent.putStringArrayListExtra(Keys.COUNTRY_LIST,countryList);
                intent.putStringArrayListExtra(Keys.YEAR_LIST,yearList);
                startActivity(intent);
                finish();
                break;
            case R.id.map_view:
                Toast.makeText(this,"Already in Map view",Toast.LENGTH_SHORT).show();
                break;
        }
        super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        Log.i(LOG_TAG,"Inside onItemSelected()");
        if(check++ > 0) {
            switch (parent.getId()) {
                case R.id.country_list_spinner:
                    selectedCountryName = parent.getItemAtPosition(position).toString();
                    Log.i(LOG_TAG, "Selected Country:" + selectedCountryName);
                    if (selectedCountryName.equalsIgnoreCase("Select")) {
                        stateList.clear();
                        stateList.add("All");
                    } else {
                        serverInteraction.getStateListFromServer(selectedCountryName);
                    }
                    break;
                case R.id.state_list_spinner:
                    selectedStateName = parent.getItemAtPosition(position).toString();
                    Log.i(LOG_TAG, "Selected state : " + selectedStateName);
                    break;
                case R.id.year_spinner:
                    yearSelected = parent.getItemAtPosition(position).toString();
                    Log.i(LOG_TAG, yearSelected);
                    break;
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(LOG_TAG,"onMapReady");
        mMap = googleMap;
        LatLng latLng = new LatLng(0.0, 0.0);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.getTitle();
                Intent chat = new Intent(MapViewActivity.this,ChatActivity.class);
                for(User mapUser : userListForMap)
                {
                    if(mapUser.getNickname().equalsIgnoreCase(marker.getTitle())){
                        chat.putExtra(Keys.USER_DATA,mapUser);
                        for(FireBaseUser fbuser : userForChat){
                            if(fbuser.displayName.equalsIgnoreCase(marker.getTitle())){
                                Log.i(LOG_TAG,"MapRecv:"+fbuser.uid);
                                chat.putExtra("userid",fbuser.uid);
                            }
                        }
                    } else {
                        Log.i(LOG_TAG,"No matches");
                    }
                }
                startActivity(chat);
            }
        });

    }

    @Override
    public void onFragmentInteraction(Bundle filterOptions) {

    }

    @Override
    public void showUsersOmMap(Bundle filterOptions) {

    }


    @Override
    public void userDataListener(ArrayList<User> users) {
        Log.i(LOG_TAG,"userDataListener");
        displayUsersMap(users);
    }

    @Override
    public void countryListDataListener(ArrayList<String> countryList) {
        Log.i(LOG_TAG,"countryListDataListener");
    }

    @Override
    public void stateListDataListener(ArrayList<String> stateList) {
        Log.i(LOG_TAG,"stateListDataListener");
        stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stateList);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        stateSpinner.setAdapter(stateAdapter);
        stateSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void sendUsersCount(int count) {

    }

    @Override
    public void sendNextIdAndMaxId(int nextId, int maxId) {
        this.nextId = nextId;
        this.maxId = maxId;
        if(maxId < nextId-1){
            String url = Url.BASE_URL + "&afterid="+maxId+"&beforeid="+nextId;
            serverInteraction.getUserDataFromServer(url);
        }
        else if(maxId == 0){
            String url = Url.BASE_URL +"&beforeid="+nextId;
            serverInteraction.getUserDataFromServer(url);
        }
        else {
            String query = QueryAndUrlHelper.prepareSQLQueryForMap(selectedCountryName,selectedStateName,yearSelected,maxId);
            displayUsersMap(new DataBaseHelper(getApplicationContext()).retrieveUserTable(query));
        }
    }

    private void displayUsersMap(ArrayList<User> users) {
        userListForMap.addAll(users);
        Log.i(LOG_TAG,"displayUsersMap");
        if(users.isEmpty()){
            Toast.makeText(this,"No users",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            loadMoreButton.setEnabled(false);
        } else{

        for (User u : users) {
            MarkerOptions markerOptions = new MarkerOptions();
            try {
                LatLng latLngg = new LatLng(u.getLatitude(), u.getLongitude());
                if (u.getLatitude() == 0 && u.getLongitude() == 0) {
                    usersWithoutLatLng.add(u);
                } else {
                    markerOptions.position(latLngg);
                    markerOptions.title(u.getNickname());
                    mMap.addMarker(markerOptions);
                }
            } catch (NullPointerException exp) {
                Log.i("UserLocationMapActivity", exp.toString());
            }
        }
        User cameraLocation = users.get(users.size()-1);
        LatLng focusCamera = new LatLng(cameraLocation.getLatitude(),cameraLocation.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(focusCamera,4);
        mMap.moveCamera(cameraUpdate);
        progressDialog.dismiss();
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    /**
     * GeocodeLocator is used to find latitude,longitude of users whose Lat Lng are unknown or (0.0,0.0)
     */
    private class GeocodeLocator extends AsyncTask<ArrayList<User>,User,ArrayList<User>> {

        LatLng latLng;
        MarkerOptions markerOptions = new MarkerOptions();
        ArrayList<User> allUsers;
        @Override
        protected ArrayList<User> doInBackground(ArrayList<User>... arrayLists) {
            allUsers = arrayLists[0];
            for (User user : allUsers) {
                Geocoder locator = new Geocoder(getApplicationContext());
                String location = user.getState() + ", " + user.getCountry();
                try {
                    List<Address> possibleAddresses =
                            locator.getFromLocationName(location, 1);
                    for (Address approxLocation : possibleAddresses) {
                        if (approxLocation.hasLatitude())
                            Log.i("rew", "Lat " + approxLocation.getLatitude());
                        if (approxLocation.hasLongitude())
                            Log.i("rew", "Long " + approxLocation.getLongitude());
                        latLng = new LatLng(approxLocation.getLatitude(), approxLocation.getLongitude());
                    }
                } catch (Exception error) {
                    Log.i("rew", "Address lookup Error", error);
                    latLng = new LatLng(0.0, 0.0);
                }
                user.setLatitude(latLng.latitude);
                user.setLongitude(latLng.longitude);
                publishProgress(user);
            }return allUsers;
        }
        // Camera animation is handled in this method
        @Override
        protected void onPostExecute(ArrayList<User> users) {
            super.onPostExecute(users);
            progressDialog.dismiss();
            User cameraLocation = users.get(0);
            LatLng focusCamera = new LatLng(cameraLocation.getLatitude(),cameraLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(focusCamera));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(focusCamera,3);
            mMap.moveCamera(cameraUpdate);
        }
        // Setting up Process Dialog
        @Override
        protected void onPreExecute() {
            progressDialog.setTitle("Locating...");
            progressDialog.setMessage("Please wait! We're looking for your friends!");
            progressDialog.show();
        }
        // As soon as user location is identified a Marker is added in onProgressUpdate()
        @Override
        protected void onProgressUpdate(User... values) {
            super.onProgressUpdate(values);
            User locatedUser = values[0];
            Log.i(LOG_TAG,"Loc :"+locatedUser.getLatitude()+ ":" + locatedUser.getLongitude());
            LatLng position = new LatLng(locatedUser.getLatitude(),locatedUser.getLongitude());
            if(locatedUser.getLongitude()==0.0 || locatedUser.getLatitude()==0.0){
                position = new LatLng(0,0);
            }
            markerOptions.position(position);
            markerOptions.title(locatedUser.getNickname());
            mMap.addMarker(markerOptions);
        }

    }

    public ArrayList<FireBaseUser> getAllUsersFromFirebase() {
        Log.i(LOG_TAG,"getAllUsersFromFirebase");
        final ArrayList<FireBaseUser> users = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    FireBaseUser user = dataSnapshotChild.getValue(FireBaseUser.class);
                    Log.i(LOG_TAG,"FB user"+user.displayName);
                    if (!TextUtils.equals(user.uid, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        userForChat.add(user);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return users;
    }
}

