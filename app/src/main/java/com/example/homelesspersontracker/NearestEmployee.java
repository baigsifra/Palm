package com.example.homelesspersontracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class NearestEmployee extends AppCompatActivity implements OnMapReadyCallback {
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mView;
    private Map<String, LatLng> employeeLocations;
    private TextView nearestWorkerTV;
    private String nearestWorkerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_employee);

        nearestWorkerTV = findViewById(R.id.nearestEmployeeTV);

        employeeLocations = InitiateRequest.employeeLocations;
        Log.d("LFRA", employeeLocations.toString());

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        Log.d("LFRA", "Before Map Created");

        mView = findViewById(R.id.geo_location_nearest_employee);

        mView.onCreate(mapViewBundle);

        mView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        double distance = Integer.MAX_VALUE;
        LatLng currentCoords = new LatLng(InitiateRequest.currentLocation.getLatitude(), InitiateRequest.currentLocation.getLongitude());
        LatLng nearestWorker = currentCoords;
        nearestWorkerID = "";
        ArrayList<String> employeeLocationKeys = new ArrayList<>(employeeLocations.keySet());
        for(int i = 0; i < employeeLocationKeys.size(); i++) {
            LatLng location = employeeLocations.get(employeeLocationKeys.get(i));
            googleMap.addMarker(new MarkerOptions().position(location));
            if(SphericalUtil.computeDistanceBetween(location, currentCoords) < distance) {
                distance = SphericalUtil.computeDistanceBetween(location, currentCoords);
                //grab the nearest worker
                nearestWorker = location;
                nearestWorkerID = employeeLocationKeys.get(i);
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

        nearestWorkerTV.setText("Nearest Worker\nLatitude: " + nearestWorker.latitude + "\nLongitude: " + nearestWorker.longitude);
    }

    public void sendRequest(View v) {
        MainActivity.firebaseHelper.addRequestToWorker(nearestWorkerID);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if(mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mView.onLowMemory();
    }



}