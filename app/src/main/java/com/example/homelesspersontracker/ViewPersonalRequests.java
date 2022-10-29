package com.example.homelesspersontracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ViewPersonalRequests extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_personal_requests);

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
}