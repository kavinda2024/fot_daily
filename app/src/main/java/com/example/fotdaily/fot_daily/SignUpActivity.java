package com.example.fotdaily.fot_daily;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity"; // Define a TAG for logging

    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Realtime Database
        mAuth = FirebaseAuth.getInstance();
        // --- IMPORTANT: Ensure the URL is correct and without brackets. ---
        mDatabase = FirebaseDatabase.getInstance("https://fotnews-app-project-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        // Get references to the UI elements
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        signUpButton = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(SignUpActivity.this, "Password should be at least 6 characters long.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "Starting user creation for email: " + email);

                signUpUser(username, email, password);
            }
        });
    }

    private void signUpUser(final String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmailAndPassword SUCCESS.");
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            Log.d(TAG, "Current user UID: " + currentUser.getUid());
                            Log.d(TAG, "Attempting to set display name: " + username);

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();

                            currentUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            Log.d(TAG, "updateProfile SUCCESS: Display name set.");
                                            String userId = currentUser.getUid();
                                            User user = new User(username, email);
                                            Log.d(TAG, "Attempting to save user data to Realtime Database for UID: " + userId);

                                            mDatabase.child("users").child(userId).setValue(user)
                                                    .addOnCompleteListener(dbTask -> {
                                                        progressBar.setVisibility(View.GONE);
                                                        if (dbTask.isSuccessful()) {
                                                            Log.d(TAG, "Realtime Database setValue SUCCESS.");

                                                            // --- NEW: Send email verification ---
                                                            currentUser.sendEmailVerification()
                                                                    .addOnCompleteListener(verificationTask -> {
                                                                        if (verificationTask.isSuccessful()) {
                                                                            Log.d(TAG, "Email verification sent to " + currentUser.getEmail());
                                                                            Toast.makeText(SignUpActivity.this,
                                                                                    getString(R.string.signup_successful_verify_email), // New string
                                                                                    Toast.LENGTH_LONG).show();
                                                                        } else {
                                                                            Log.e(TAG, "Failed to send verification email.", verificationTask.getException());
                                                                            Toast.makeText(SignUpActivity.this,
                                                                                    getString(R.string.signup_successful_but_verification_failed), // New string
                                                                                    Toast.LENGTH_LONG).show();
                                                                        }
                                                                        // After attempting to send verification or failure, redirect
                                                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    });
                                                        } else {
                                                            Log.e(TAG, "Realtime Database setValue FAILED: " + dbTask.getException().getMessage());
                                                            Toast.makeText(SignUpActivity.this, "Failed to store user data in database.", Toast.LENGTH_SHORT).show();
                                                            // Redirect even if DB write failed
                                                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            Log.e(TAG, "updateProfile FAILED: " + profileTask.getException().getMessage());
                                            Toast.makeText(SignUpActivity.this, "Failed to update user display name: " + profileTask.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                            // Even if display name update fails, proceed to store in DB as it's a separate concern
                                            String userId = currentUser.getUid();
                                            User user = new User(username, email);
                                            mDatabase.child("users").child(userId).setValue(user)
                                                    .addOnCompleteListener(dbTask -> {
                                                        progressBar.setVisibility(View.GONE);
                                                        if (dbTask.isSuccessful()) {
                                                            // Attempt to send verification even if profile update failed
                                                            currentUser.sendEmailVerification()
                                                                    .addOnCompleteListener(verificationTask -> {
                                                                        if (verificationTask.isSuccessful()) {
                                                                            Toast.makeText(SignUpActivity.this,
                                                                                    getString(R.string.signup_successful_profile_issue_verify_email), // New string
                                                                                    Toast.LENGTH_LONG).show();
                                                                        } else {
                                                                            Toast.makeText(SignUpActivity.this,
                                                                                    getString(R.string.signup_successful_profile_issue_no_verification), // New string
                                                                                    Toast.LENGTH_LONG).show();
                                                                        }
                                                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    });
                                                        } else {
                                                            Toast.makeText(SignUpActivity.this, "Account created, but profile update and data storage failed.", Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                        }
                                    });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "User creation successful but current user is null.");
                            Toast.makeText(SignUpActivity.this, "User creation successful but user object is null. Please try again.", Toast.LENGTH_SHORT).show();
                            // Redirect to login anyway
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "createUserWithEmailAndPassword FAILED: " + task.getException().getMessage());
                        Toast.makeText(SignUpActivity.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public static class User {
        public String username;
        public String email;

        public User() {
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }
}





//package com.example.fotdaily.fot_daily;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log; // Make sure this is imported
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.UserProfileChangeRequest;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class SignUpActivity extends AppCompatActivity {
//
//    private static final String TAG = "SignUpActivity"; // Define a TAG for logging
//
//    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
//    private Button signUpButton;
//    private ProgressBar progressBar;
//
//    private FirebaseAuth mAuth;
//    private DatabaseReference mDatabase;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_signup);
//
//        // Initialize Firebase Auth and Realtime Database
//        mAuth = FirebaseAuth.getInstance();
//        // --- IMPORTANT CHANGE HERE ---
//        mDatabase = FirebaseDatabase.getInstance("https://fotnews-app-project-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
//
//        // Get references to the UI elements
//        usernameEditText = findViewById(R.id.username);
//        emailEditText = findViewById(R.id.email);
//        passwordEditText = findViewById(R.id.password);
//        confirmPasswordEditText = findViewById(R.id.confirm_password);
//        signUpButton = findViewById(R.id.signUpButton);
//        progressBar = findViewById(R.id.progressBar);
//
//        progressBar.setVisibility(View.GONE);
//
//        signUpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String username = usernameEditText.getText().toString().trim();
//                String email = emailEditText.getText().toString().trim();
//                String password = passwordEditText.getText().toString().trim();
//                String confirmPassword = confirmPasswordEditText.getText().toString().trim();
//
//                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
//                    Toast.makeText(SignUpActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (!password.equals(confirmPassword)) {
//                    Toast.makeText(SignUpActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (password.length() < 6) {
//                    Toast.makeText(SignUpActivity.this, "Password should be at least 6 characters long.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                progressBar.setVisibility(View.VISIBLE);
//                Log.d(TAG, "Starting user creation for email: " + email);
//
//                signUpUser(username, email, password);
//            }
//        });
//    }
//
//