package com.example.homelesspersontracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ForgotPassword extends AppCompatActivity {
    private EditText forgotPwdEmailET;
    private TextView forgotPasswordInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        forgotPwdEmailET = findViewById(R.id.forgotPwdEmailET);
        forgotPasswordInfo = findViewById(R.id.forgotPasswordInfo);
    }

    public void resetPassword(View v) {
        String email = forgotPwdEmailET.getText().toString();

        if(email.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter an email.",
                    Toast.LENGTH_SHORT).show();
        } else {

            MainActivity.firebaseHelper.getmAuth().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(MainActivity.TAG, "Reset password email sent");
                                forgotPasswordInfo.setText("An email was sent to " + email +
                                        ". It may take up to 5 minutes.");
                            } else {
                                Toast.makeText(getApplicationContext(), "Email verification failed. Please try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}