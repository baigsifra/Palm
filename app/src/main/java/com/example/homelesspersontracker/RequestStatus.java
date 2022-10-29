package com.example.homelesspersontracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;

public class RequestStatus extends AppCompatActivity {
    private ArrayList<Request> requestsAL;
    private RecyclerView recyclerView;
    private Button helpRSCVBtn, donationRSCVBtn;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button confirmButton, cancelConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_status);
        requestsAL = new ArrayList<>();
        recyclerView = findViewById(R.id.requestsRV);
        helpRSCVBtn = findViewById(R.id.initiateHelpRequestCVBtn);
        donationRSCVBtn = findViewById(R.id.initiateDonationRequestCVBtn);

        createUI();

        requestsAL = ExtraScreen.myRequests;

        setAdapter(true);

        helpRSCVBtn.setTextColor(getResources().getColor(R.color.black));
        SpannableString btnText = new SpannableString("help requests");
        btnText.setSpan(new UnderlineSpan(), 0, btnText.length(), 0);
        helpRSCVBtn.setText(btnText);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void createUI() {
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

        if(MainActivity.userType.equals("employee")) {
            Button menuBtn1 = findViewById(R.id.requestStatusMenuBtn);
            Button menuBtn2 = findViewById(R.id.initiateRequestMenuBtn);

            menuBtn1.setCompoundDrawablesWithIntrinsicBounds
                    (null, this.getDrawable(R.drawable.ic_hand_pointing),
                    null, null);
            menuBtn2.setCompoundDrawablesWithIntrinsicBounds
                    (null, this.getDrawable(R.drawable.ic_your_requests),
                    null, null);

            menuBtn1.setBackgroundTintList(AppCompatResources.getColorStateList(this, R.color.white));
            menuBtn2.setBackgroundTintList(AppCompatResources.getColorStateList(this, R.color.dark_teal));
        }

    }

    private void setAdapter(boolean isHelpRequest) {
        Log.d("LFRA", "requestsAL: " + requestsAL);
        RecyclerAdapter adapter = new RecyclerAdapter(requestsAL, isHelpRequest);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public void changeViewRequestStatus(View v) {
        switch (v.getId()) {
            case R.id.initiateHelpRequestCVBtn:
                // highlight the button
                helpRSCVBtn.setTextColor(getResources().getColor(R.color.black));
                SpannableString btnText = new SpannableString("help requests");
                btnText.setSpan(new UnderlineSpan(), 0, btnText.length(), 0);
                helpRSCVBtn.setText(btnText);

                // reset other button
                donationRSCVBtn.setTextColor(getResources().getColor(R.color.grey));
                donationRSCVBtn.setText("donation requests");

                // change the layout
                setAdapter(true);

                break;
            case R.id.initiateDonationRequestCVBtn:
                // highlight the button
                donationRSCVBtn.setTextColor(getResources().getColor(R.color.black));
                SpannableString btnText2 = new SpannableString("donation requests");
                btnText2.setSpan(new UnderlineSpan(), 0, btnText2.length(), 0);
                donationRSCVBtn.setText(btnText2);

                // reset the other button
                helpRSCVBtn.setTextColor(getResources().getColor(R.color.grey));
                helpRSCVBtn.setText("help requests");

                // change the layout
                setAdapter(false);

                break;
        }
    }

    /* Note: this should be called from the open_confirm_dialog of the donation_request_status_item.xml file
     * Will probably need an id of the donation_request_status_item that caused this popup to open.
     */
    public void createNewConfirmDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View confirmPopupView = getLayoutInflater().inflate(R.layout.confirm_popup, null);
        confirmButton = (Button) confirmPopupView.findViewById(R.id.confirm_request);
        cancelConfirmButton = (Button) confirmPopupView.findViewById(R.id.goback_confirm);
        dialogBuilder.setView(confirmPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define confirm button's functionality here.
            }
        });

        cancelConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define cancel button's functionality here.
                dialog.dismiss();
            }
        });
    }
}