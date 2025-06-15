package com.example.fotdaily.fot_daily;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewsDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NEWS_ID = "com.example.fotdaily.fot_daily.NEWS_ID"; // Ensure this matches your package
    private static final String TAG = "NewsDetailActivity";

    private ImageView imageNewsDetail;
    private TextView textNewsTitleDetail;
    private TextView textNewsDateDetail;
    private TextView textNewsDescriptionDetail;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_news_detail);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imageNewsDetail = findViewById(R.id.image_news_detail);
        textNewsTitleDetail = findViewById(R.id.text_news_title_detail);
        textNewsDateDetail = findViewById(R.id.text_news_date_detail);
        textNewsDescriptionDetail = findViewById(R.id.text_news_description_detail);

        String newsId = getIntent().getStringExtra(EXTRA_NEWS_ID);

        if (newsId != null && !newsId.isEmpty()) {
            // MODIFICATION: Specify the correct database URL
            String databaseUrl = "https://fotnews-app-project-default-rtdb.asia-southeast1.firebasedatabase.app";
            databaseReference = FirebaseDatabase.getInstance(databaseUrl).getReference("news_articles").child(newsId);
            loadNewsData();
        } else {
            Log.e(TAG, "News ID is null or empty. Cannot load data.");
            Toast.makeText(this, "Error: News item not found.", Toast.LENGTH_LONG).show();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Error");
            }
            // Optionally finish();
        }
    }

    private void loadNewsData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String date = dataSnapshot.child("date").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    // IMPORTANT: Use "Description" as per your Firebase structure
                    String description = dataSnapshot.child("Description").getValue(String.class);

                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(title != null ? title : "News Details");
                    }
                    textNewsTitleDetail.setText(title);
                    textNewsDateDetail.setText(date);
                    textNewsDescriptionDetail.setText(description);

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // Make sure your Imgur URLs are direct image links (e.g., end in .png, .jpg)
                        Glide.with(NewsDetailActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_launcher_background) // Replace with a proper placeholder
                                .error(com.google.android.material.R.drawable.mtrl_ic_error) // Replace with a proper error drawable
                                .into(imageNewsDetail);
                    } else {
                        // Set a default or error image if imageUrl is null or empty
                        imageNewsDetail.setImageResource(com.google.android.material.R.drawable.mtrl_ic_error); // Example
                        Log.w(TAG, "Image URL is null or empty for newsId: " + databaseReference.getKey());
                    }

                } else {
                    Log.w(TAG, "News item with ID " + databaseReference.getKey() + " does not exist.");
                    Toast.makeText(NewsDetailActivity.this, "News item not found.", Toast.LENGTH_SHORT).show();
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle("Not Found");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read news data.", databaseError.toException());
                Toast.makeText(NewsDetailActivity.this, "Failed to load news data. Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Handles the Up button to go back
        return true;
    }
}










//package com.example.fotdaily.fot_daily;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class NewsDetailActivity extends AppCompatActivity {
//
//    public static final String EXTRA_NEWS_ID = "com.example.fotdaily.fot_daily.NEWS_ID"; // Ensure this matches your package
//    private static final String TAG = "NewsDetailActivity";
//
//    private ImageView imageNewsDetail;
//    private TextView textNewsTitleDetail;
//    private TextView textNewsDateDetail;
//    private TextView textNewsDescriptionDetail;
//
//    private DatabaseReference databaseReference;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_news_detail);
//
//        Toolbar toolbar = findViewById(R.id.toolbar_news_detail);
//        setSupportActionBar(toolbar);
//
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
//
//        imageNewsDetail = findViewById(R.id.image_news_detail);
//        textNewsTitleDetail = findViewById(R.id.text_news_title_detail);
//        textNewsDateDetail = findViewById(R.id.text_news_date_detail);
//        textNewsDescriptionDetail = findViewById(R.id.text_news_description_detail);
//
//        String newsId = getIntent().getStringExtra(EXTRA_NEWS_ID);
//
//        if (newsId != null && !newsId.isEmpty()) {
//            databaseReference = FirebaseDatabase.getInstance().getReference("news_articles").child(newsId);
//            loadNewsData();
//        } else {
//            Log.e(TAG, "News ID is null or empty. Cannot load data.");
//            Toast.makeText(this, "Error: News item not found.", Toast.LENGTH_LONG).show();
//            if (getSupportActionBar() != null) {
//                getSupportActionBar().setTitle("Error");
//            }
//            // Optionally finish();
//        }
//    }
//
//    private void loadNewsData() {
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    String title = dataSnapshot.child("title").getValue(String.class);
//                    String date = dataSnapshot.child("date").getValue(String.class);
//                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
//                    // IMPORTANT: Use "Description" as per your Firebase structure
//                    String description = dataSnapshot.child("Description").getValue(String.class);
//
//                    if (getSupportActionBar() != null) {
//                        getSupportActionBar().setTitle(title != null ? title : "News Details");
//                    }
//                    textNewsTitleDetail.setText(title);
//                    textNewsDateDetail.setText(date);
//                    textNewsDescriptionDetail.setText(description);
//
//                    if (imageUrl != null && !imageUrl.isEmpty()) {
//                        // Make sure your Imgur URLs are direct image links (e.g., end in .png, .jpg)
//                        Glide.with(NewsDetailActivity.this)
//                                .load(imageUrl)
//                                .placeholder(R.drawable.ic_launcher_background) // Replace with a proper placeholder
//                                .error(com.google.android.material.R.drawable.mtrl_ic_error) // Replace with a proper error drawable
//                                .into(imageNewsDetail);
//                    } else {
//                        imageNewsDetail.setImageResource(com.google.android.material.R.drawable.mtrl_ic_error); // Example
//                    }
//
//                } else {
//                    Log.w(TAG, "News item with ID " + databaseReference.getKey() + " does not exist.");
//                    Toast.makeText(NewsDetailActivity.this, "News item not found.", Toast.LENGTH_SHORT).show();
//                    if (getSupportActionBar() != null) {
//                        getSupportActionBar().setTitle("Not Found");
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "Failed to read news data.", databaseError.toException());
//                Toast.makeText(NewsDetailActivity.this, "Failed to load news data.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed(); // Handles the Up button to go back
//        return true;
//    }
//}