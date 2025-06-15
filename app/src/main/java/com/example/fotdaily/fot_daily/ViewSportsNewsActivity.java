package com.example.fotdaily.fot_daily;

import android.content.Intent; // Keep if you plan to navigate to a NewsDetailActivity
import android.os.Bundle;
import android.view.View;       // Required for View
import android.widget.ImageView; // Required for ImageView
import android.widget.TextView;  // Required for TextView
// Removed: import android.view.MenuItem;
// Removed: import android.widget.Toast; (unless used for other debugging)
// Removed: import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//  import com.google.android.material.bottomnavigation.BottomNavigationView;
//  import com.google.android.material.navigation.NavigationBarView;

public class ViewSportsNewsActivity extends AppCompatActivity {

    // Views for the included sports news card
    private View sportsNewsCardLayout;
    private ImageView imageSportsNews;
    private TextView titleSportsNews;
    private TextView dateSportsNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sports_news);

        // --- ActionBar/Toolbar Setup (Optional but Recommended) ---
        // If you want an ActionBar with an Up button:
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setTitle("Sports News"); // Or use a string resource

        // --- Setup Sports News Card (from item_news_card2) ---
        sportsNewsCardLayout = findViewById(R.id.sports_news_card_container); // ID of the <include> tag

        // Find views within the included layout.
        // Make sure these IDs (news_image, news_title, news_date) exist in item_news_card2.xml
        if (sportsNewsCardLayout != null) { // Good practice to check if the include layout was found
            imageSportsNews = sportsNewsCardLayout.findViewById(R.id.news_image);
            titleSportsNews = sportsNewsCardLayout.findViewById(R.id.news_title);
            dateSportsNews = sportsNewsCardLayout.findViewById(R.id.news_date);
        }


        // Example:
        if (imageSportsNews != null) {
            // Assuming you have a drawable named 'news2'
            imageSportsNews.setImageResource(R.drawable.news2); // Replace with actual image from Firebase/data source
            imageSportsNews.setContentDescription(getString(R.string.cd_sports_news_image_placeholder)); // Use a string resource
        }
        if (titleSportsNews != null) {
            titleSportsNews.setText("Scoreboard of TECHNO sports fest"); // Replace with actual title
        }
        if (dateSportsNews != null) {
            dateSportsNews.setText("2025/06/01"); // Replace with actual date
        }

        // Set OnClickListener for the sports news card if it should lead to a detail view
        if (sportsNewsCardLayout != null) {
            sportsNewsCardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openNewsDetail("sports_item_id_123");
                }
            });
        }
    }


    private void openNewsDetail(String newsId) {
        Intent intent = new Intent(ViewSportsNewsActivity.this, NewsDetailActivity.class);
        // Make sure NewsDetailActivity.EXTRA_NEWS_ID is a public static final String in NewsDetailActivity
        intent.putExtra(NewsDetailActivity.EXTRA_NEWS_ID, newsId);
        startActivity(intent);
    }


}