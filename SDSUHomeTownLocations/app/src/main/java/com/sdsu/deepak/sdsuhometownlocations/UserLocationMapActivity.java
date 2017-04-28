package com.sdsu.deepak.sdsuhometownlocations;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.WindowDecorActionBar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class UserLocationMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<User> userListForMap;
    private LatLng defaultLatLng;
    ProgressDialog progressDialog;
    private ArrayList<User> usersWithoutLatLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location_map);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.user_map);
        mapFragment.getMapAsync(this);
        usersWithoutLatLng = new ArrayList<>();
        defaultLatLng = new LatLng(0.0,0.0);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(0.0,0.0);
        Intent userOnMap = getIntent();
        String condition = userOnMap.getStringExtra("Condition");
        // AsyncTask is not used for one user
            if(condition.equals("Single User")){
                User currentUser = userOnMap.getParcelableExtra("User Data");
                MarkerOptions markerOptions = new MarkerOptions();
                if(currentUser.getLatitude()==0 && currentUser.getLongitude()==0){
                    Log.i("rew","Inside default true");
                    Geocoder locator = new Geocoder(getApplicationContext());
                    String location = currentUser.getState()+", "+currentUser.getCountry();
                    try {
                        List<Address> possibleAddresses = locator.getFromLocationName(location,1);
                        for (Address approxLocation: possibleAddresses) {
                            if (approxLocation.hasLatitude())
                                Log.i("rew", "Lat " + approxLocation.getLatitude());
                            if (approxLocation.hasLongitude())
                                Log.i("rew", "Long " + approxLocation.getLongitude());
                            latLng = new LatLng(approxLocation.getLatitude(),approxLocation.getLongitude());
                            markerOptions.position(latLng);
                            }
                        } catch (Exception error) {
                        Log.i("rew","Error");
                        }
                    } else {
                        latLng = new LatLng(currentUser.getLatitude(), currentUser.getLongitude());
                    markerOptions.position(latLng);
                    }
                    markerOptions.title(currentUser.getNickname());
                    mMap.clear();
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,5);
                    mMap.moveCamera(cameraUpdate);
                    mMap.addMarker(markerOptions);
                    }
            else if(condition.equals("All Users")){
                ArrayList<User> currentUsersList = userOnMap.getParcelableArrayListExtra("User Data");
                Log.i("User",currentUsersList.toString());
                for(User u : currentUsersList){
                    MarkerOptions markerOptions = new MarkerOptions();
                    try {
                        LatLng latLngg = new LatLng(u.getLatitude(),u.getLongitude());
                        if(u.getLatitude()==0 && u.getLongitude()==0){
                            usersWithoutLatLng.add(u);
                        }
                        else {
                            markerOptions.position(latLngg);
                            markerOptions.title(u.getNickname());
                            mMap.addMarker(markerOptions);
                        }
                    }catch(NullPointerException exp){
                        Log.i("UserLocationMapActivity",exp.toString());
                    }
                }
        }
        if(!usersWithoutLatLng.isEmpty())
        {
            GeocodeLocator geocodeLocator = new GeocodeLocator();
            geocodeLocator.execute(usersWithoutLatLng);
        }
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
            LatLng position = new LatLng(locatedUser.getLatitude(),locatedUser.getLongitude());
            if(locatedUser.getLongitude()==0.0 || locatedUser.getLatitude()==0.0){
                position = new LatLng(32.1,-114.24);
            }
            markerOptions.position(position);
            markerOptions.title(locatedUser.getNickname());
            mMap.addMarker(markerOptions);
        }
    }
}
