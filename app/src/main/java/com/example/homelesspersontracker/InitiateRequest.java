package com.example.homelesspersontracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class InitiateRequest extends AppCompatActivity implements OnMapReadyCallback {
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    public static Location currentLocation;
    public static Map<String, LatLng> employeeLocations;

    private MapView mView;
    private Button helpRequestCVBtn, donationRequestCVBtn, submitHelpRequestBtn, submitDonationRequestBtn, uploadPhotoButton;
    private TextView IRQ1TV, IHRQ2TV;
    private TableLayout Q3HRTL, Q3DRTL;
    private RadioGroup Q2DRRG;

    private ImageView uploadedPhoto;
    // Define the pic id
    private static final int pic_id = 123;
    // Define variables to store the photo
    private StorageReference mStorageRef;
    private Uri url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate_request);

        helpRequestCVBtn = findViewById(R.id.initiateHelpRequestCVBtn);
        donationRequestCVBtn = findViewById(R.id.initiateDonationRequestCVBtn);
        submitHelpRequestBtn = findViewById(R.id.submitHelpRequestBtn);
        submitDonationRequestBtn = findViewById(R.id.submitDonationRequestBtn);

        IRQ1TV = findViewById(R.id.IRQ1TV);

        IHRQ2TV = findViewById(R.id.IHRQ2TV);
        uploadPhotoButton = findViewById(R.id.upload_photo);
        uploadedPhoto = findViewById(R.id.uploaded_photo);
        Q3HRTL = findViewById(R.id.Q3HRTL);
        Q3DRTL = findViewById(R.id.Q3DRTL);

        Q2DRRG = findViewById(R.id.Q2DRRG);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        createUI();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();

        mView = findViewById(R.id.geo_location);

        mView.onCreate(mapViewBundle);

        mView.getMapAsync(this);

        MainActivity.firebaseHelper.getEmployeeLocations(new FirebaseHelper.FirestoreCallback() {
            @Override
            public void onCallback(String userType) {

            }

            @Override
            public void onCallback(Map<String, LatLng> locationsMap) {
                employeeLocations = locationsMap;
                Log.d("LFRA", "Inside Callback");
            }

            @Override
            public void onCallback(ArrayList<Request> requestAL) { }

            @Override
            public void onCallback(Request r) { }
        });
    } // End of onCreate().

    private void createUI() {
        helpRequestCVBtn.setTextColor(getResources().getColor(R.color.black));
        SpannableString btnText = new SpannableString("help request");
        btnText.setSpan(new UnderlineSpan(), 0, btnText.length(), 0);
        helpRequestCVBtn.setText(btnText);

        uploadPhotoButton = (Button) findViewById(R.id.upload_photo);
        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    //startActivity(intent);
                    startActivityForResult(intent, pic_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        findViewById(R.id.requestStatusMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        findViewById(R.id.initiateRequestMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if(MainActivity.userType.equals("individual")) {
                    intent = new Intent(getApplicationContext(), InitiateRequest.class);
                } else if(MainActivity.userType.equals("employee")) {
                    intent = new Intent(getApplicationContext(), RequestStatus.class);
                } else {
                    intent = new Intent(getApplicationContext(), ExtraScreen.class);
                }

                startActivity(intent);
            }
        });

        findViewById(R.id.leaderboardMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(MainActivity.userType.equals("individual")) {
                    intent = new Intent(getApplicationContext(), Individual_Leaderboard.class);
                } else if(MainActivity.userType.equals("employee")) {
                    intent = new Intent(getApplicationContext(), Individual_Leaderboard.class);
                } else {
                    intent = new Intent(getApplicationContext(), AgencyLeaderBoard.class);
                }

                startActivity(intent);
            }
        });

        findViewById(R.id.profileMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(MainActivity.userType.equals("individual")) {
                    intent = new Intent(getApplicationContext(), IndividualProfile.class);
                } else if(MainActivity.userType.equals("employee")) {
                    intent = new Intent(getApplicationContext(), EmployeeProfile.class);
                } else {
                    intent = new Intent(getApplicationContext(), AgencyProfile.class);
                }

                startActivity(intent);
            }
        });
    }


    // This method will help to retrieve the image
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Match the request 'pic id with requestCode
        if (requestCode == pic_id) {

            // BitMap is data structure of image file
            // which store the image in memory
            Bitmap photo = (Bitmap) data.getExtras()
                    .get("data");

            Uri u = data.getData();

            // Set the image in imageview for display
            uploadedPhoto.setImageBitmap(photo);
            IHRQ2TV.setVisibility(View.INVISIBLE);
            uploadPhotoButton.setVisibility(View.INVISIBLE);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            byte bb[] = bytes.toByteArray();

            MainActivity.firebaseHelper.uploadToFirebase(u);
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void changeViewInitiateRequest(View v) {
        switch (v.getId()) {
            case R.id.initiateHelpRequestCVBtn:
                // highlight the button
                helpRequestCVBtn.setTextColor(getResources().getColor(R.color.black));
                SpannableString btnText = new SpannableString("help request");
                btnText.setSpan(new UnderlineSpan(), 0, btnText.length(), 0);
                helpRequestCVBtn.setText(btnText);

                // reset other button
                donationRequestCVBtn.setTextColor(getResources().getColor(R.color.grey));
                donationRequestCVBtn.setText("donation request");

                // change the layout
                IRQ1TV.setText("1. Location of homeless person in need of help*");
                IHRQ2TV.setVisibility(View.VISIBLE);
                uploadPhotoButton.setVisibility(View.VISIBLE);
                uploadedPhoto.setVisibility(View.VISIBLE);
                Q3HRTL.setVisibility(View.VISIBLE);
                submitHelpRequestBtn.setVisibility(View.VISIBLE);

                Q2DRRG.setVisibility(View.INVISIBLE);
                Q3DRTL.setVisibility(View.INVISIBLE);
                submitDonationRequestBtn.setVisibility(View.INVISIBLE);
                break;
            case R.id.initiateDonationRequestCVBtn:
                // highlight the button
                donationRequestCVBtn.setTextColor(getResources().getColor(R.color.black));
                SpannableString btnText2 = new SpannableString("donation request");
                btnText2.setSpan(new UnderlineSpan(), 0, btnText2.length(), 0);
                donationRequestCVBtn.setText(btnText2);

                // reset the other button
                helpRequestCVBtn.setTextColor(getResources().getColor(R.color.grey));
                helpRequestCVBtn.setText("help request");

                // change the layout
                IRQ1TV.setText("1. Location for donation pickup");
                IHRQ2TV.setVisibility(View.INVISIBLE);
                uploadPhotoButton.setVisibility(View.INVISIBLE);
                uploadedPhoto.setVisibility(View.INVISIBLE);
                Q3HRTL.setVisibility(View.INVISIBLE);
                submitHelpRequestBtn.setVisibility(View.INVISIBLE);

                Q2DRRG.setVisibility(View.VISIBLE);
                Q3DRTL.setVisibility(View.VISIBLE);
                submitDonationRequestBtn.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
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
        if (checkPermissions()) {
            getLastLocation();
        }
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
                            Log.d("LFRA", "Inside getLastLocation");
                            Log.d("LFRA", "Lat: " + currentLocation.getLatitude() + "\nLong: " + currentLocation.getLongitude());
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

    public void initiateRequest(View v) {

        RadioGroup rg = (RadioGroup) findViewById(R.id.Q1RG);
        int selectedId = rg.getCheckedRadioButtonId();

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();

        switch (v.getId()) {
            case R.id.submitHelpRequestBtn:
                // grab the photo

                // grab any other comments
                EditText nameET = findViewById(R.id.homelessNameET);
                EditText otherET = findViewById(R.id.otherCommentsET);
                String name = nameET.getText().toString();
                String other = otherET.getText().toString();

                if(selectedId == R.id.Q1RB1) {
                    MainActivity.firebaseHelper.initiateRequest(true, new HelpRequest
                            ("Awaiting", formatter.format(date), currentLocation.getLatitude(),
                                    currentLocation.getLongitude(), name, other));
                } else {
                    EditText locationET = findViewById(R.id.IRQ1OtherET);
                    MainActivity.firebaseHelper.initiateRequest(true, new HelpRequest
                            ("Awaiting", formatter.format(date),
                                    locationET.getText().toString(), name, other));
                }
                break;
            case R.id.submitDonationRequestBtn:
                // grab preferred method of contact
                RadioGroup rgq2 = (RadioGroup) findViewById(R.id.Q2DRRG);
                int selectedId2 = rgq2.getCheckedRadioButtonId();
                RadioButton selectedMOC = (RadioButton) findViewById(selectedId2);
                // grab specific agency

                if(selectedId == R.id.Q1RB1) {
                    MainActivity.firebaseHelper.initiateRequest(false, new DonationRequest
                            ("Awaiting", formatter.format(date), currentLocation.getLatitude(),
                                    currentLocation.getLongitude(), selectedMOC.getText().toString()));
                } else {
                    EditText locationET = findViewById(R.id.IRQ1OtherET);
                    MainActivity.firebaseHelper.initiateRequest(false, new DonationRequest
                            ("Awaiting", formatter.format(date),
                                    locationET.getText().toString(), selectedMOC.getText().toString()));
                }
                break;
        }

        goToNearestEmployee();
    }

    public void goToNearestEmployee() {
        Intent intent = new Intent(getApplicationContext(), NearestEmployee.class);
        startActivity(intent);
    }

}