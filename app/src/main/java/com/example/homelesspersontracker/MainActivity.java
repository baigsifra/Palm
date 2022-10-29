package com.example.homelesspersontracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "LFRA";
    EditText emailET, passwordET;
    public static FirebaseHelper firebaseHelper;
    public static String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailET = findViewById(R.id.emailSignInET);
        passwordET = findViewById(R.id.passwordSignInET);

        firebaseHelper = new FirebaseHelper();

        updateIfLoggedIn();
    }

    public void goToSignUpScreen(View v) {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        startActivity(intent);
    }

    public void signIn(View v) {

        // Get user data
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        // verify all user data is entered
        if (email.length() == 0 || password.length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter all fields", Toast.LENGTH_SHORT).show();
        }
        // verify password is at least 6 char long (otherwise firebase will deny)
        else if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password must be at least 6 char long", Toast.LENGTH_SHORT).show();
        } else {
            // code to sign in user
            firebaseHelper.getmAuth().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                // updating MY var for uid of current user
                                firebaseHelper.updateUid(firebaseHelper.getmAuth().getCurrentUser().getUid());
                                Toast.makeText(getApplicationContext(), "signed in", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, email + " is signed in");

                                updateIfLoggedIn();

                            } else {
                                // sign in failed
                                Toast.makeText(getApplicationContext(), "failed to log in", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, email + " failed to log in");
                            }
                        }
                    });
        }
    }

    public void signInAsGuest(View v) {
        firebaseHelper.getmAuth().signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = firebaseHelper.getmAuth().getCurrentUser();
                            updateIfLoggedIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateIfLoggedIn();
                        }
                    }
                });
    }

    public void goToResetPassword(View v) {
        Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
        startActivity(intent);
    }

    public void updateIfLoggedIn() {
        FirebaseUser user = firebaseHelper.getmAuth().getCurrentUser();
        if(user != null) {
            Intent intent = new Intent(getApplicationContext(), ExtraScreen.class);
            startActivity(intent);
        }

        //TODO https://firebase.google.com/docs/auth/android/manage-users
        // 1. update the profile of the user (user.getDisplayName())
        // 2. send verification email
    }
}