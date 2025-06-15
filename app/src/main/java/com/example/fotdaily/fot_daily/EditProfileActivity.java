
package com.example.fotdaily.fot_daily;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // For the password input in the re-authentication dialog
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    // UI Elements based on activity_edit_profile.xml
    private TextInputEditText editTextUsername;
    private TextInputEditText editTextEmail;
    private Button buttonSaveProfile;
    private Button buttonDiscardChanges;
    private ProgressBar progressBar;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userDatabaseReference;

    // Variables to store initial user data for change detection
    private String initialUsername = "";
    private String initialEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize UI elements by their IDs from activity_edit_profile.xml
        editTextUsername = findViewById(R.id.edit_text_username);
        editTextEmail = findViewById(R.id.edit_text_email);
        buttonSaveProfile = findViewById(R.id.button_save_profile);
        buttonDiscardChanges = findViewById(R.id.button_discard_changes);
        progressBar = findViewById(R.id.edit_profile_progress_bar);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Check if the user is currently logged in. If not, redirect to login screen.
        if (currentUser == null) {
            Toast.makeText(this, getString(R.string.user_not_logged_in), Toast.LENGTH_LONG).show();
            // Redirect to LoginActivity (ensure LoginActivity exists and is declared in AndroidManifest.xml)
            Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close EditProfileActivity as no user is logged in
            return;
        }

        // Initialize Firebase Realtime Database reference for the current user's data
        // IMPORTANT: Ensure the URL is correct and without brackets.
        userDatabaseReference = FirebaseDatabase.getInstance("https://fotnews-app-project-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users")
                .child(currentUser.getUid());

        // Load the current user's profile data into the EditText fields
        loadCurrentUserData();

        // Set click listeners for the Save and Discard buttons
        buttonSaveProfile.setOnClickListener(v -> validateAndSaveChanges());
        buttonDiscardChanges.setOnClickListener(v -> finish()); // Simply close the activity if changes are discarded
    }

    /**
     * Controls the visibility of the ProgressBar and the enabled state of buttons
     * to indicate loading/saving operations.
     * @param isLoading True to show the progress bar and disable buttons; false to hide progress bar and enable buttons.
     */
    private void setLoadingState(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        buttonSaveProfile.setEnabled(!isLoading);
        buttonDiscardChanges.setEnabled(!isLoading);
    }

    /**
     * Loads the current user's username and email from Firebase Realtime Database.
     * It populates the `editTextUsername` and `editTextEmail` fields.
     * If data is not found in RTDB, it falls back to Firebase Authentication's `displayName` and `email`.
     */
    private void loadCurrentUserData() {
        setLoadingState(true); // Show loading indicator
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Data exists in Realtime Database, retrieve username and email
                    initialUsername = dataSnapshot.child("username").getValue(String.class);
                    initialEmail = dataSnapshot.child("email").getValue(String.class);

                    // Set username (handle null case)
                    editTextUsername.setText(initialUsername != null ? initialUsername : "");

                    // Prioritize email from Realtime Database. If empty, fall back to Firebase Auth email.
                    if (!TextUtils.isEmpty(initialEmail)) {
                        editTextEmail.setText(initialEmail);
                    } else if (currentUser.getEmail() != null) {
                        initialEmail = currentUser.getEmail(); // Use Auth email as the initial reference
                        editTextEmail.setText(initialEmail);
                    } else {
                        editTextEmail.setText(""); // No email found anywhere
                        Log.w(TAG, "Email not found in DB or Auth for user: " + currentUser.getUid());
                    }
                } else {
                    // No user data found in Realtime Database. Use Firebase Auth's data.
                    Toast.makeText(EditProfileActivity.this, getString(R.string.could_not_load_user_data), Toast.LENGTH_SHORT).show();
                    initialUsername = currentUser.getDisplayName(); // Note: displayName can be null for email/password users initially
                    initialEmail = currentUser.getEmail();

                    editTextUsername.setText(initialUsername != null ? initialUsername : ""); // Handle null displayName
                    editTextEmail.setText(initialEmail != null ? initialEmail : ""); // Handle null email
                }
                setLoadingState(false); // Hide loading indicator
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                setLoadingState(false); // Hide loading indicator on error
                Toast.makeText(EditProfileActivity.this, getString(R.string.profile_update_failed) + ": " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to load user data from Realtime Database.", databaseError.toException());
            }
        });
    }

    /**
     * Validates user input for username and email, then initiates the process of saving changes.
     * It checks for changes, and decides whether to update Firebase Auth email and/or Realtime Database.
     */
    private void validateAndSaveChanges() {
        String newUsername = editTextUsername.getText().toString().trim();
        String newEmail = editTextEmail.getText().toString().trim();

        // Input validation checks
        if (TextUtils.isEmpty(newUsername)) {
            editTextUsername.setError(getString(R.string.enter_username));
            editTextUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newEmail)) {
            editTextEmail.setError(getString(R.string.enter_valid_email));
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            editTextEmail.setError(getString(R.string.enter_valid_email));
            editTextEmail.requestFocus();
            return;
        }

        // Determine if any changes were made compared to initial loaded data
        boolean usernameChanged = !Objects.equals(initialUsername, newUsername);
        boolean emailChanged = !Objects.equals(initialEmail, newEmail);

        if (!usernameChanged && !emailChanged) {
            Toast.makeText(this, getString(R.string.no_changes_detected), Toast.LENGTH_SHORT).show();
            return; // No changes, no action needed
        }

        setLoadingState(true); // Show loading indicator

        // Prepare data for Realtime Database update
        Map<String, Object> updatedUserDataForDb = new HashMap<>();
        if (usernameChanged) {
            updatedUserDataForDb.put("username", newUsername);
        }

        // Logic to update Firebase Auth email first if it has changed
        if (emailChanged && !newEmail.equals(currentUser.getEmail())) {
            // Email has genuinely changed AND it's different from the email currently in Firebase Auth
            updateFirebaseUserEmail(newEmail, newUsername, updatedUserDataForDb, usernameChanged);
        } else {
            // Case 1: Email did not change
            // Case 2: Email changed in the form, but it already matches currentUser.getEmail() (e.g., initial email was empty, user typed current auth email)
            // Case 3: Only username changed
            if (emailChanged) { // If email value in form was changed and should be saved to DB
                updatedUserDataForDb.put("email", newEmail); // Add to DB map, as Auth update not needed here
            }
            // Proceed to update Realtime Database if any changes were prepared for it, or if username changed
            if (!updatedUserDataForDb.isEmpty() || usernameChanged) {
                updateRealtimeDatabase(updatedUserDataForDb, newUsername, (emailChanged ? newEmail : null));
            } else {
                setLoadingState(false); // Should not happen if validation passed and changes were detected
            }
        }
    }

    /**
     * Attempts to update the user's email address in Firebase Authentication.
     * This is a sensitive operation and may require re-authentication.
     * @param newEmail The new email address to set in Firebase Auth.
     * @param newUsername The new username (passed through for subsequent RTDB/displayName update).
     * @param updatedUserDataForDb Map of data to update in Realtime Database.
     * @param usernameAlsoChanged Boolean indicating if username also changed.
     */
    private void updateFirebaseUserEmail(String newEmail, String newUsername, Map<String, Object> updatedUserDataForDb, boolean usernameAlsoChanged) {
        currentUser.updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User email address updated in Firebase Auth.");
                        updatedUserDataForDb.put("email", newEmail); // Add email to DB map now that Auth update succeeded
                        // Proceed to update Realtime Database and Auth displayName
                        updateRealtimeDatabase(updatedUserDataForDb, newUsername, newEmail);
                    } else {
                        setLoadingState(false); // Hide loading indicator on Auth email update failure
                        Log.e(TAG, "Failed to update user email in Firebase Auth.", task.getException());

                        // Handle the specific case where re-authentication is required
                        if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                            // Prompt the user to re-authenticate with their current password
                            showReauthenticateDialog(newEmail, newUsername, updatedUserDataForDb, usernameAlsoChanged);
                        } else {
                            // Show a generic error message for other failures
                            Toast.makeText(EditProfileActivity.this,
                                    getString(R.string.email_update_failed) + ": " + Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Displays an AlertDialog prompting the user to re-enter their current password for re-authentication.
     * This is called when sensitive operations like email update require recent login.
     * @param newEmail The new email address that was attempted.
     * @param newUsername The new username to be updated.
     * @param updatedUserDataForDb Map containing data to update in Realtime Database.
     * @param usernameAlsoChanged Flag indicating if username also changed.
     */
    private void showReauthenticateDialog(String newEmail, String newUsername, Map<String, Object> updatedUserDataForDb, boolean usernameAlsoChanged) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.reauth_title));
        builder.setMessage(getString(R.string.reauth_message));

        // Set up the input for password
        final EditText passwordInput = new EditText(this);
        passwordInput.setHint(getString(R.string.reauth_password_hint));
        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(passwordInput);

        // Set up the dialog buttons
        builder.setPositiveButton(getString(R.string.reauth_confirm_button), (dialog, which) -> {
            String currentPassword = passwordInput.getText().toString().trim();
            if (TextUtils.isEmpty(currentPassword)) {
                Toast.makeText(EditProfileActivity.this, getString(R.string.reauth_password_empty), Toast.LENGTH_SHORT).show();
                setLoadingState(false); // Hide loading if dialog dismissed or input empty
                return;
            }
            setLoadingState(true); // Show loading during re-authentication attempt
            performReauthentication(currentPassword, newEmail, newUsername, updatedUserDataForDb, usernameAlsoChanged);
        });
        builder.setNegativeButton(getString(R.string.reauth_cancel_button), (dialog, which) -> {
            dialog.cancel();
            setLoadingState(false); // Hide loading if user cancels
            Toast.makeText(EditProfileActivity.this, getString(R.string.reauth_cancelled), Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    /**
     * Performs the re-authentication with the provided current password.
     * If successful, it retries the original email update operation.
     * @param currentPassword The user's current password.
     * @param newEmail The new email address that was attempted.
     * @param newUsername The new username to be updated.
     * @param updatedUserDataForDb Map containing data to update in Realtime Database.
     * @param usernameAlsoChanged Flag indicating if username also changed.
     */
    private void performReauthentication(String currentPassword, String newEmail, String newUsername, Map<String, Object> updatedUserDataForDb, boolean usernameAlsoChanged) {
        if (currentUser == null) {
            setLoadingState(false);
            Toast.makeText(this, getString(R.string.user_not_logged_in), Toast.LENGTH_LONG).show();
            return;
        }

        // Create credentials for re-authentication using the user's current email and the provided password
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);

        currentUser.reauthenticate(credential)
                .addOnCompleteListener(reauthTask -> {
                    if (reauthTask.isSuccessful()) {
                        Log.d(TAG, "User re-authenticated successfully.");
                        // Re-authentication successful, now retry the original email update
                        updateFirebaseUserEmail(newEmail, newUsername, updatedUserDataForDb, usernameAlsoChanged);
                    } else {
                        setLoadingState(false); // Hide loading on re-authentication failure
                        Log.e(TAG, "Re-authentication failed: " + reauthTask.getException().getMessage(), reauthTask.getException());
                        Toast.makeText(EditProfileActivity.this,
                                getString(R.string.reauth_failed) + ": " + Objects.requireNonNull(reauthTask.getException()).getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Updates the user's data in the Firebase Realtime Database and their display name in Firebase Auth.
     * This method is called after email (if changed) has been successfully updated in Firebase Auth,
     * or directly if only username/other DB fields changed.
     * @param updates Map containing fields to update in Realtime Database.
     * @param newUsername The new username to set as display name in Firebase Auth.
     * @param updatedEmail The new email if it was updated through Auth, or null if not.
     */
    private void updateRealtimeDatabase(Map<String, Object> updates, final String newUsername, @Nullable final String updatedEmail) {
        userDatabaseReference.updateChildren(updates)
                .addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        Log.d(TAG, "User data updated in Realtime Database.");

                        // Now, update displayName in Firebase Auth if it was changed
                        // This check prevents unnecessary Auth profile updates
                        if (!Objects.equals(currentUser.getDisplayName(), newUsername)) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(newUsername)
                                    .build();
                            currentUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileUpdateTask -> {
                                        setLoadingState(false); // Hide loading after Auth profile update
                                        if (profileUpdateTask.isSuccessful()) {
                                            Log.d(TAG, "Firebase Auth display name updated successfully.");
                                            // Update initial state variables to reflect successful changes
                                            initialUsername = newUsername;
                                            if (updatedEmail != null) initialEmail = updatedEmail;
                                            Toast.makeText(EditProfileActivity.this, getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show();
                                            finish(); // Close activity after successful update
                                        } else {
                                            // Even if display name update fails, the DB data is saved.
                                            Log.e(TAG, "Failed to update Firebase Auth display name.", profileUpdateTask.getException());
                                            Toast.makeText(EditProfileActivity.this, getString(R.string.profile_updated_db_display_name_failed) + ": " + Objects.requireNonNull(profileUpdateTask.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                            // Still update initial state for current session consistency
                                            initialUsername = newUsername;
                                            if (updatedEmail != null) initialEmail = updatedEmail;
                                            finish(); // Close the activity anyway as most critical data is saved
                                        }
                                    });
                        } else {
                            // No display name update needed, only Realtime Database was updated successfully
                            setLoadingState(false);
                            // Update initial state variables
                            initialUsername = newUsername;
                            if (updatedEmail != null) initialEmail = updatedEmail;
                            Toast.makeText(EditProfileActivity.this, getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show();
                            finish(); // Close activity
                        }
                    } else {
                        setLoadingState(false); // Hide loading on Realtime Database update failure
                        Log.e(TAG, "Failed to update user data in Realtime Database.", dbTask.getException());
                        Toast.makeText(EditProfileActivity.this, getString(R.string.profile_update_failed) + ": " + Objects.requireNonNull(dbTask.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}







