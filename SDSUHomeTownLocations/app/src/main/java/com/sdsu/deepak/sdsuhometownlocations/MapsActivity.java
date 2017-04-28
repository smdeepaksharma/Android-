package com.sdsu.deepak.sdsuhometownlocations;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    Button addLocation , cancelButton;
    LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
