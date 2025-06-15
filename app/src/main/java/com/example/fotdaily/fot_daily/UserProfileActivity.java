package com.example.fotdaily.fot_daily;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView; // Make sure TextView is imported

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.Toast; // Make sure Toast is imported

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";

    private TextView usernameValueTextView;
    private TextView emailValueTextView;
    private Button editProfileButton;
    private Button signOutButton;
    private Button homeButton;
    private TextView devInfoLinkTextView; // Declare the new TextView

    private DatabaseReference userDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize Views
        usernameValueTextView = findViewById(R.id.username_value);
        emailValueTextView = findViewById(R.id.email_value);
        editProfileButton = findViewById(R.id.edit_profile_button);
        signOutButton = findViewById(R.id.sign_out_button);
        homeButton = findViewById(R.id.home_button);
        devInfoLinkTextView = findViewById(R.id.dev_info_link_text_view); // Initialize the new TextView

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userDatabaseReference = FirebaseDatabase.getInstance("https://fotnews-app-project-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("users")
                    .child(userId);
            loadUserProfile();
        } else {
            Toast.makeText(this, "User not signed in. Please login.", Toast.LENGTH_LONG).show();
            Log.w(TAG, "User is not signed in. Cannot load profile.");
            Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // Set up button listeners
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        signOutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(UserProfileActivity.this)
                    .setTitle(getString(R.string.sign_out_dialog_title))
                    .setMessage(getString(R.string.sign_out_dialog_message))
                    .setPositiveButton(getString(R.string.sign_out_dialog_yes), (dialog, which) -> {
                        mAuth.signOut();
                        Toast.makeText(UserProfileActivity.this, "Signed Out Successfully", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "User signed out.");
                        Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton(getString(R.string.sign_out_dialog_no), (dialog, which) -> {
                        dialog.dismiss();
                        Toast.makeText(UserProfileActivity.this, "Sign out cancelled.", Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, ViewNewsActivity.class);
            startActivity(intent);
            finish();
        });

        // Set up click listener for the new "See developer info" hyperlink
        devInfoLinkTextView.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, DevInfoActivity.class);
            startActivity(intent);
            // Optional: You might want to finish this activity if you don't want it in back stack
            // finish();
        });
    }

    private void loadUserProfile() {
        if (userDatabaseReference == null) {
            Log.e(TAG, "userDatabaseReference is null, cannot load profile.");
            Toast.makeText(this, "Error: Database reference not initialized.", Toast.LENGTH_SHORT).show();
            return;
        }

        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);

                    if (username != null) {
                        usernameValueTextView.setText(username);
                    } else {
                        usernameValueTextView.setText("N/A");
                        Log.w(TAG, "Username not found in database for user: " + currentUser.getUid());
                    }

                    if (email != null) {
                        emailValueTextView.setText(email);
                    } else if (currentUser.getEmail() != null) {
                        emailValueTextView.setText(currentUser.getEmail());
                        Log.w(TAG, "Email not found in database for user: " + currentUser.getUid() + ". Using email from Firebase Auth user.");
                    } else {
                        emailValueTextView.setText("N/A");
                        Log.w(TAG, "Email not found in database or Firebase Auth user for user: " + currentUser.getUid());
                    }

                } else {
                    Toast.makeText(UserProfileActivity.this, "User data not found in database.", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "User data node does not exist for userId: " + currentUser.getUid());
                    usernameValueTextView.setText("N/A");
                    emailValueTextView.setText("N/A");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserProfileActivity.this, "Failed to load user data. " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to read user data.", databaseError.toException());
                usernameValueTextView.setText("Error loading data");
                emailValueTextView.setText("Error loading data");
            }
        });
    }
}








//package com.example.fotdaily.fot_daily;
//
//import android.app.AlertDialog; // For AlertDialog
//import android.content.DialogInterface; // For DialogInterface (used in AlertDialog)
//import android.content.Intent; // For navigation between activities
//import android.os.Bundle;
//import android.util.Log; // For logging messages to Logcat
//import android.widget.Button; // For interactive buttons
//import android.widget.TextView; // For displaying text (username, email values)
//import android.widget.Toast; // For showing short pop-up messages
//
//import androidx.annotation.NonNull; // For non-null annotations
//import androidx.appcompat.app.AppCompatActivity; // Base class for activities
//
//import com.google.firebase.auth.FirebaseAuth; // Firebase Authentication instance
//import com.google.firebase.auth.FirebaseUser; // Current authenticated user
//import com.google.firebase.database.DataSnapshot; // Data from Firebase Realtime Database
//import com.google.firebase.database.DatabaseError; // Errors from Firebase Realtime Database operations
//import com.google.firebase.database.DatabaseReference; // Reference to a location in the database
//import com.google.firebase.database.FirebaseDatabase; // Firebase Realtime Database instance
//import com.google.firebase.database.ValueEventListener; // Listener for data changes in the database
//
//public class UserProfileActivity extends AppCompatActivity {
//
//    private static final String TAG = "UserProfileActivity"; // Tag for Logcat messages
//
//    // Declare UI elements as per activity_view_profile.xml
//    private TextView usernameValueTextView;
//    private TextView emailValueTextView;
//    private Button editProfileButton;
//    private Button signOutButton;
//    private Button homeButton;
//
//    // Declare Firebase instances
//    private DatabaseReference userDatabaseReference; // Reference to the current user's data in Realtime Database
//    private FirebaseAuth mAuth; // Firebase Authentication object
//    private FirebaseUser currentUser; // The currently logged-in Firebase user
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // Set the content view to your activity_view_profile.xml layout
//        setContentView(R.layout.activity_view_profile);
//
//        // Initialize Firebase Authentication instance and get the current user
//        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();
//
//        // Initialize UI elements by finding them by their IDs in the layout
//        usernameValueTextView = findViewById(R.id.username_value);
//        emailValueTextView = findViewById(R.id.email_value);
//        editProfileButton = findViewById(R.id.edit_profile_button);
//        signOutButton = findViewById(R.id.sign_out_button);
//        homeButton = findViewById(R.id.home_button);
//
//        // Check if a user is currently signed in
//        if (currentUser != null) {
//            String userId = currentUser.getUid(); // Get the unique ID of the current user
//            // Initialize Realtime Database reference to the 'users' node, then to the specific userId
//            // Ensure the Firebase Realtime Database URL is correct and DOES NOT contain square brackets
//            userDatabaseReference = FirebaseDatabase.getInstance("https://fotnews-app-project-default-rtdb.asia-southeast1.firebasedatabase.app")
//                    .getReference("users")
//                    .child(userId);
//            loadUserProfile(); // Load the user's profile data
//        } else {
//            // If no user is signed in, inform the user and redirect to the LoginActivity
//            Toast.makeText(this, "User not signed in. Please login.", Toast.LENGTH_LONG).show();
//            Log.w(TAG, "User is not signed in. Cannot load profile.");
//            Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish(); // Close this activity
//        }
//
//        // Set up click listener for the Edit Profile button
//        editProfileButton.setOnClickListener(v -> {
//            // Navigate to the EditProfileActivity
//            Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
//            startActivity(intent);
//            // Optionally, you can call finish() here if you don't want UserProfileActivity
//            // to remain in the back stack when navigating to EditProfileActivity.
//            // finish();
