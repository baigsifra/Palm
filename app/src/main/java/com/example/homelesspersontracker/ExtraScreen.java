package com.example.homelesspersontracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Map;

public class ExtraScreen extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private TextView authenticationReminder;

    public static ArrayList<Request> myRequests;

    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_screen);
        mAuth = MainActivity.firebaseHelper.getmAuth();
        authenticationReminder = findViewById(R.id.authenticationReminder);

        if (!(mAuth.getCurrentUser().isEmailVerified())) {
            authenticationReminder.setText("Your account is not yet verified. Please check your email for the verification link");
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();

        MainActivity.firebaseHelper.getRequests(new FirebaseHelper.FirestoreCallback() {
            @Override
            public void onCallback(String userType) {

            }

            @Override
            public void onCallback(Map<String, LatLng> locationsMap) {

            }

            @Override
            public void onCallback(ArrayList<Request> requestAL) {
                myRequests = FirebaseHelper.requestArrayList;
            }

            @Override
            public void onCallback(Request r) {

            }
        });
    }

    public void signOut(View v) {
        mAuth.getInstance().signOut();

        Intent welcomeIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(welcomeIntent);

    }

    public void firstScreen(View v) {
        Intent intent;
        if(MainActivity.userType.equals("individual")) {
            intent = new Intent(getApplicationContext(), RequestStatus.class);
        } else if(MainActivity.userType.equals("employee")) {
            intent = new Intent(getApplicationContext(), ViewPersonalRequests.class);
        } else {
            intent = new Intent(getApplicationContext(), ViewRequests.class);
        }

        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            requestNewLocationData();
                            currentLocation = location;

                            MainActivity.firebaseHelper.getUserType(new FirebaseHelper.FirestoreCallback() {
                                @Override
                                public void onCallback(String userType) {
                                    MainActivity.userType = userType;
                                    Log.d("LFRA", "User Type: " + userType);
                                    if(userType.equals("employee")) {
                                        MainActivity.firebaseHelper.updateEmployeeLocation(currentLocation);
                                    }
                                }

                                @Override
                                public void onCallback(Map<String, LatLng> locationsMap) { }

                                @Override
                                public void onCallback(ArrayList<Request> requestAL) { }

                                @Override
                                public void onCallback(Request r) { }
                            });
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.d("LFRA", "Lat: " + mLastLocation.getLatitude() + "\nLong: " + mLastLocation.getLongitude());
            currentLocation = mLastLocation;
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
}