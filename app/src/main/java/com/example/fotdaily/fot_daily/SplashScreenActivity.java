package com.example.fotdaily.fot_daily;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // Redirect to the SignUpActivity after the splash screen
            Intent intent = new Intent(SplashScreenActivity.this, SignUpActivity.class); // Change to SignUpActivity
            startActivity(intent);
            finish(); // Close the splash screen activity so it's removed from the stack
        }, 3000); // 3 seconds delay
    }
}










