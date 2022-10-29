package com.example.homelesspersontracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUp extends AppCompatActivity {
    private Button individualSUCV, agencySUCV, employeeSUCV, signUpBtn;
    private TableLayout services;
    private TextInputLayout nameLabelTV, usernameTV, locationTV, agencyTV, phoneNumTV, regionTV, employeeIdTV, addressTV, ageTV;
    private EditText nameET, emailET, passwordET, confirmPasswordET, usernameET, locationET, agencyET, phoneNumET, regionET, employeeIdET, addressET, ageET;
    private String userType = "individual";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        individualSUCV = findViewById(R.id.individualSignUpCVBtn);
        agencySUCV = findViewById(R.id.agencySignUpCVBtn);
        employeeSUCV = findViewById(R.id.employeeSignUpCVBtn);
        signUpBtn = findViewById(R.id.signUpBtn);

        individualSUCV.setTextColor(getResources().getColor(R.color.black));
        SpannableString btnText = new SpannableString("individual user");
        btnText.setSpan(new UnderlineSpan(), 0, btnText.length(), 0);
        individualSUCV.setText(btnText);

        services = findViewById(R.id.services);

        nameLabelTV = findViewById(R.id.nameLabelTV);
        usernameTV = findViewById(R.id.usernameTV);
        locationTV = findViewById(R.id.locationTV);
        agencyTV = findViewById(R.id.agencyTV);
        phoneNumTV = findViewById(R.id.phoneNumTV);
        regionTV = findViewById(R.id.regionTV);
        employeeIdTV = findViewById(R.id.employeeIdTV);
        addressTV = findViewById(R.id.addressTV);
        ageTV = findViewById(R.id.ageTV);

        nameET = findViewById(R.id.nameET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        confirmPasswordET = findViewById(R.id.confirmPasswordET);
        usernameET = findViewById(R.id.usernameET);
        locationET = findViewById(R.id.locationET);
        agencyET = findViewById(R.id.agencyET);
        phoneNumET = findViewById(R.id.phoneNumET);
        regionET = findViewById(R.id.regionET);
        employeeIdET = findViewById(R.id.employeeIdET);
        addressET = findViewById(R.id.addressET);
        ageET = findViewById(R.id.ageET);
    }

    public void signUp(View v) {
        // Get user data
        String username = "", location = "", agency = "", phoneNum = "", region = "", employeeId = "", address = "", services = "";
        int age = 0;
        String name = nameET.getText().toString();
        String email =  emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confPassword = confirmPasswordET.getText().toString();

        if(userType.equals("individual")) {
            username = usernameET.getText().toString();
            phoneNum = phoneNumET.getText().toString();
            address = addressET.getText().toString();
            age = Integer.parseInt(ageET.getText().toString());
            if (phoneNum.length() == 0) {
                Toast.makeText(getApplicationContext(), "Enter all required fields", Toast.LENGTH_SHORT).show();
            }
        } else if (userType.equals("agency")) {
            location = locationET.getText().toString();
            region = regionET.getText().toString();
            services = "";
            if (region.length() == 0 || services.length() == 0) {
                Toast.makeText(getApplicationContext(), "Enter all required fields", Toast.LENGTH_SHORT).show();
            }
        } else {
            agency = agencyET.getText().toString();
            employeeId = employeeIdET.getText().toString();
            if (agency.length() == 0) {
                Toast.makeText(getApplicationContext(), "Enter all required fields", Toast.LENGTH_SHORT).show();
            }
        }

        // verify all user data is entered
        if (name.length() == 0 || email.length() == 0 || password.length() == 0 || confPassword.length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter all required fields", Toast.LENGTH_SHORT).show();
        }
        // verify password is at least 6 char long (otherwise firebase will deny)
        else if (password.length() < 8) {
            Toast.makeText(getApplicationContext(), "Password must be at least 8 char long", Toast.LENGTH_SHORT).show();
        }
        // verify that password match
        else if (!(password.equals(confPassword))){
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
        else {
            // code to sign up user
            String fbusername = username, fblocation = location, fbagency = agency, fbphoneNum = phoneNum,
                    fbregion = region, fbemployeeId = employeeId, fbaddress = address, fbservices = services;
            int fbage = age;
            MainActivity.firebaseHelper.getmAuth().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // user account was created in firebase auth
                                Log.i(MainActivity.TAG, email + " account created");

                                FirebaseUser user = MainActivity.firebaseHelper.getmAuth().getCurrentUser();

                                // update the FirebaseHelper var uid to equal the uid of currently signed in user
                                MainActivity.firebaseHelper.updateUid(user.getUid());

                                if(userType.equals("individual")) {
                                    MainActivity.firebaseHelper.addUserToFirestore(name, userType, fbusername, fbphoneNum, fbaddress, fbage, user.getUid());
                                } else if (userType.equals("agency")) {
                                    MainActivity.firebaseHelper.addUserToFirestore(name, userType, fblocation, fbregion, fbservices, user.getUid());
                                } else {
                                    MainActivity.firebaseHelper.addUserToFirestore(name, userType, fbagency, fbemployeeId, user.getUid());
                                }

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(MainActivity.TAG, "User profile updated.");
                                                }
                                            }
                                        });

                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(MainActivity.TAG, "Email sent.");
                                                }
                                            }
                                        });

                                Intent intent = new Intent(getApplicationContext(), ExtraScreen.class);
                                startActivity(intent);

                            } else {
                                // user WASN'T created
                                Log.d(MainActivity.TAG, email + " sign up failed");
                            }
                        }
                    });
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void changeView(View v) {
        switch (v.getId()) {
            case R.id.individualSignUpCVBtn:
                // highlight the button
                individualSUCV.setTextColor(getResources().getColor(R.color.black));
                SpannableString btnText = new SpannableString("individual user");
                btnText.setSpan(new UnderlineSpan(), 0, btnText.length(), 0);
                individualSUCV.setText(btnText);

                // reset the styles of the other buttons
                agencySUCV.setTextColor(getResources().getColor(R.color.grey));
                agencySUCV.setText("agency user");
                employeeSUCV.setTextColor(getResources().getColor(R.color.grey));
                employeeSUCV.setText("employee");

                // change the input fields
                nameLabelTV.setHint("Full Name...");
                usernameTV.setVisibility(View.VISIBLE);
                locationTV.setVisibility(View.INVISIBLE);
                agencyTV.setVisibility(View.INVISIBLE);
                phoneNumTV.setVisibility(View.VISIBLE);
                regionTV.setVisibility(View.INVISIBLE);
                employeeIdTV.setVisibility(View.INVISIBLE);
                addressTV.setVisibility(View.VISIBLE);
                services.setVisibility(View.INVISIBLE);
                ageTV.setVisibility(View.VISIBLE);
                userType = "individual";
                break;
            case R.id.agencySignUpCVBtn:
                // highlight the button
                agencySUCV.setTextColor(getResources().getColor(R.color.black));
                SpannableString btnText2 = new SpannableString("agency user");
                btnText2.setSpan(new UnderlineSpan(), 0, btnText2.length(), 0);
                agencySUCV.setText(btnText2);

                // reset the styles of the other buttons
                individualSUCV.setTextColor(getResources().getColor(R.color.grey));
                individualSUCV.setText("individual user");
                employeeSUCV.setTextColor(getResources().getColor(R.color.grey));
                employeeSUCV.setText("employee");

                // change the input fields
                nameLabelTV.setHint("Agency Name...");
                usernameTV.setVisibility(View.INVISIBLE);
                locationTV.setVisibility(View.VISIBLE);
                agencyTV.setVisibility(View.INVISIBLE);
                phoneNumTV.setVisibility(View.INVISIBLE);
                regionTV.setVisibility(View.VISIBLE);
                employeeIdTV.setVisibility(View.INVISIBLE);
                addressTV.setVisibility(View.INVISIBLE);
                services.setVisibility(View.VISIBLE);
                ageTV.setVisibility(View.INVISIBLE);
                userType = "agency";
                break;
            case R.id.employeeSignUpCVBtn:
                // highlight the button
                employeeSUCV.setTextColor(getResources().getColor(R.color.black));
                SpannableString btnText3 = new SpannableString("employee");
                btnText3.setSpan(new UnderlineSpan(), 0, btnText3.length(), 0);
                employeeSUCV.setText(btnText3);

                // reset the styles of the other buttons
                agencySUCV.setTextColor(getResources().getColor(R.color.grey));
                agencySUCV.setText("agency user");
                individualSUCV.setTextColor(getResources().getColor(R.color.grey));
                individualSUCV.setText("individual user");

                // change the input fields
                nameLabelTV.setHint("Full Name...");
                usernameTV.setVisibility(View.INVISIBLE);
                locationTV.setVisibility(View.INVISIBLE);
                agencyTV.setVisibility(View.VISIBLE);
                phoneNumTV.setVisibility(View.INVISIBLE);
                regionTV.setVisibility(View.INVISIBLE);
                employeeIdTV.setVisibility(View.VISIBLE);
                addressTV.setVisibility(View.INVISIBLE);
                services.setVisibility(View.INVISIBLE);
                ageTV.setVisibility(View.INVISIBLE);
                userType = "employee";
                break;
        }
    }
}