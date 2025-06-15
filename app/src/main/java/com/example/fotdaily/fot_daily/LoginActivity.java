package com.example.fotdaily.fot_daily; // Your package name

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView signUpLink;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get references to the UI elements
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        signUpLink = findViewById(R.id.signUpLink);

        progressBar.setVisibility(View.GONE);  // Initially hide the progress bar

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            loginUser(email, password);
        });

        signUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        // MODIFIED: Redirect to ViewNewsActivity
                        Intent intent = new Intent(LoginActivity.this, ViewNewsActivity.class);
                        // Optional: Add flags to clear the task stack if ViewNewsActivity is your new main hub
                        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();  // Close the login activity so user can't go back to it with back button
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}



















//package com.example.fotdaily.fot_daily;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//
//public class LoginActivity extends AppCompatActivity {
//
//    private EditText emailEditText, passwordEditText;
//    private Button loginButton;
//    private ProgressBar progressBar;
//    private TextView signUpLink;  // TextView for sign-up link
//
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        // Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
//
//        // Get references to the UI elements
//        emailEditText = findViewById(R.id.email);
//        passwordEditText = findViewById(R.id.password);
//        loginButton = findViewById(R.id.loginButton);
//        progressBar = findViewById(R.id.progressBar);
//        signUpLink = findViewById(R.id.signUpLink);  // Reference to the sign-up link
//
//        progressBar.setVisibility(View.GONE);  // Initially hide the progress bar
//
//        loginButton.setOnClickListener(v -> {
//            String email = emailEditText.getText().toString().trim();
//            String password = passwordEditText.getText().toString().trim();
//
//            // Validate input
//            if (email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(LoginActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            progressBar.setVisibility(View.VISIBLE);  // Show progress bar during login
//
//            // Perform login
//            loginUser(email, password);
//        });
//
//        // Handle the sign-up link click
//        signUpLink.setOnClickListener(v -> {
//            // Redirect to SignUpActivity
//            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
//            startActivity(intent);
//        });
//    }
//
//    private void loginUser(String email, String password) {
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    progressBar.setVisibility(View.GONE);  // Hide progress bar after login attempt
//
//                    if (task.isSuccessful()) {
//                        // Login successful, navigate to MainActivity or HomeActivity
//                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();  // Close the login activity
//                    } else {
//                        // If login fails, display error message
//                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
//}
