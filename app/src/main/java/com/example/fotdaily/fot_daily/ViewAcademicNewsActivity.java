package com.example.fotdaily.fot_daily;

import android.content.Intent; // Keep if you plan to navigate to a NewsDetailActivity
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
// Removed: import android.view.MenuItem;
// Removed: import android.widget.Toast; (unless used for other debugging)
// Removed: import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
// Removed: import com.google.android.material.bottomnavigation.BottomNavigationView;
// Removed: import com.google.android.material.navigation.NavigationBarView;

public class ViewAcademicNewsActivity extends AppCompatActivity {

    // Views for the included academic news card
    private View academicNewsCardLayout;
    private ImageView imageAcademicNews;
    private TextView titleAcademicNews;
    private TextView dateAcademicNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_academic_news);

        // --- ActionBar/Toolbar Setup (Optional but Recommended) ---
        // If you want an ActionBar with an Up button:
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setTitle(getString(R.string.title_academic_news_section)); // Use string resource

        // --- Setup Academic News Card (from item_news_card3) ---
        academicNewsCardLayout = findViewById(R.id.academic_news_card_container); // ID of the <include> tag

        if (academicNewsCardLayout != null) {
            // Find views within the included layout.
            // Make sure these IDs (news_image, news_title, news_date) exist in item_news_card3.xml
            imageAcademicNews = academicNewsCardLayout.findViewById(R.id.news_image);
            titleAcademicNews = academicNewsCardLayout.findViewById(R.id.news_title);
            dateAcademicNews = academicNewsCardLayout.findViewById(R.id.news_date);
        }

        // Example:
        if (imageAcademicNews != null) {
            // Assuming you have a drawable, e.g., 'news3' or a default
            imageAcademicNews.setImageResource(R.drawable.news3); // Replace with actual image
            imageAcademicNews.setContentDescription(getString(R.string.cd_academic_news_image_placeholder)); // Use a string resource
        }
        if (titleAcademicNews != null) {
            titleAcademicNews.setText("FOT Symposium Highlights"); // Replace with actual title
        }
        if (dateAcademicNews != null) {
            dateAcademicNews.setText("2024/01/15"); // Replace with actual date
        }

        // Set OnClickListener for the academic news card if it should lead to a detail view
        if (academicNewsCardLayout != null) {
            academicNewsCardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openNewsDetail("academic_item_id_456");
                }
            });
        }
    }

    // Example helper method to open NewsDetailActivity
    private void openNewsDetail(String newsId) {
        Intent intent = new Intent(ViewAcademicNewsActivity.this, NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.EXTRA_NEWS_ID, newsId);
        startActivity(intent);
    }

    // --- Handle Up Button in ActionBar (if you added getSupportActionBar().setDisplayHomeAsUpEnabled(true);) ---
    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    //     if (item.getItemId() == android.R.id.home) {
    //         finish();
    //         return true;
    //     }
    //     return super.onOptionsItemSelected(item);
    // }
}