package com.sdsu.deepak.mytabapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class UserLocation extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button addLocation , cancelButton;
    LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.user_map);
        addLocation = (Button) findViewById(R.id.location);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        userLocation = new LatLng(32.0,-113.4);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                userLocation = latLng;
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,6);
                mMap.moveCamera(cameraUpdate);
                mMap.addMarker(markerOptions);
            }
        });

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendLocation = getIntent();
                sendLocation.putExtra("Latitude",userLocation.latitude);
                sendLocation.putExtra("Longitude",userLocation.longitude);
                setResult(RESULT_OK,sendLocation);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendCancel = getIntent();
                setResult(RESULT_CANCELED,sendCancel);
                finish();
            }
        });
    }

}
