package com.example.fotdaily.fot_daily;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DevInfoActivity extends AppCompatActivity {

    private Button homeButton; // Declare the Home button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the dev_info.xml layout
        // Ensure your XML layout file is named activity_dev_info.xml or adjust this line accordingly
        setContentView(R.layout.activity_dev_info);

        // Initialize the Home button by its ID from the XML layout
        homeButton = findViewById(R.id.dev_info_home_button);

        // Set an OnClickListener for the Home button
        homeButton.setOnClickListener(v -> {
            // Create an Intent to navigate back to the ViewNewsActivity (your main news screen)
            // Adjust 'ViewNewsActivity.class' if your main activity has a different name
            Intent intent = new Intent(DevInfoActivity.this, ViewNewsActivity.class);
            startActivity(intent); // Start the new activity
            finish(); // Close the current DevInfoActivity to remove it from the back stack
        });
    }
}
