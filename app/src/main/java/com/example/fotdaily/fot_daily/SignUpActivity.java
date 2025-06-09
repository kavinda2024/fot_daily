package com.example.fotdaily.fot_daily;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private ProgressBar progressBar;  // ProgressBar to show loading state

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Realtime Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get references to the UI elements
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        signUpButton = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.progressBar);  // Initialize the ProgressBar

        // Initially hide the ProgressBar
        progressBar.setVisibility(View.GONE);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                // Validate the input
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show ProgressBar while sign-up is in progress
                progressBar.setVisibility(View.VISIBLE);

                // Create a new user with Firebase Authentication
                signUpUser(username, email, password);
            }
        });
    }

    private void signUpUser(final String username, String email, String password) {
        // Create new user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Hide the ProgressBar after operation completes
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        // User signed up successfully, store their information in Realtime Database
                        String userId = mAuth.getCurrentUser().getUid();
                        User user = new User(username, email); // Create a User object

                        mDatabase.child("users").child(userId).setValue(user)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        // Display success message
                                        Toast.makeText(SignUpActivity.this, "Sign-up successful", Toast.LENGTH_SHORT).show();

                                        // Optionally, redirect the user to MainActivity or another activity
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();  // Close SignUpActivity
                                    } else {
                                        // If storing user data fails
                                        Toast.makeText(SignUpActivity.this, "Failed to store user data.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // If sign-up fails, display a message to the user
                        Toast.makeText(SignUpActivity.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // User class to store user data
    public static class User {
        public String username;
        public String email;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }
}























//
//package com.example.fotdaily.fot_daily;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class SignUpActivity extends AppCompatActivity {
//
//    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
//    private Button signUpButton;
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
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//
//        // Get references to the UI elements
//        usernameEditText = findViewById(R.id.username);
//        emailEditText = findViewById(R.id.email);
//        passwordEditText = findViewById(R.id.password);
//        confirmPasswordEditText = findViewById(R.id.confirm_password);
//        signUpButton = findViewById(R.id.signUpButton);
//
//        signUpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String username = usernameEditText.getText().toString().trim();
//                String email = emailEditText.getText().toString().trim();
//                String password = passwordEditText.getText().toString().trim();
//                String confirmPassword = confirmPasswordEditText.getText().toString().trim();
//
//                // Validate the input
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
//                // Create a new user with Firebase Authentication
//                signUpUser(username, email, password);
//            }
//        });
//    }
//
//    private void signUpUser(final String username, String email, String password) {
//        // Create new user with Firebase Authentication
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        // User signed up successfully, store their information in Realtime Database
//                        String userId = mAuth.getCurrentUser().getUid();
//                        User user = new User(username, email); // Create a User object
//
//                        mDatabase.child("users").child(userId).setValue(user)
//                                .addOnCompleteListener(task1 -> {
//                                    if (task1.isSuccessful()) {
//                                        Toast.makeText(SignUpActivity.this, "Sign-up successful", Toast.LENGTH_SHORT).show();
//                                        // Proceed to next activity (e.g., login or home page)
//                                    } else {
//                                        Toast.makeText(SignUpActivity.this, "Failed to store user data.", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    } else {
//                        // If sign-up fails, display a message to the user
//                        Toast.makeText(SignUpActivity.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
//
//    // User class to store user data
//    public static class User {
//        public String username;
//        public String email;
//
//        public User() {
//            // Default constructor required for calls to DataSnapshot.getValue(User.class)
//        }
//
//        public User(String username, String email) {
//            this.username = username;
//            this.email = email;
//        }
//    }
//}
