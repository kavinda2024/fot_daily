package com.example.fotdaily.fot_daily; // Your package name

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
// Remove Toast if you no longer need it for debugging these navigation items
// import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ViewNewsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_news);

        // --- Initialize BottomNavigationView ---
        bottomNavigationView = findViewById(R.id.bottom_navigation_view); // Ensure this ID matches your layout
        if (bottomNavigationView != null) {
            // --- Explicitly select the 'Events' (news) item ---
            bottomNavigationView.setSelectedItemId(R.id.nav_events);

            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_events) {
                        // Already on the ViewNewsActivity (Events/News screen)
                        return true;
                    } else if (itemId == R.id.nav_academic) {
                        // Navigate to Academic News Activity
                        Intent academicIntent = new Intent(ViewNewsActivity.this, ViewAcademicNewsActivity.class);
                        startActivity(academicIntent);

                        return true;
                    } else if (itemId == R.id.nav_sports) {
                        // Navigate to Sports News Activity
                        Intent sportsIntent = new Intent(ViewNewsActivity.this, ViewSportsNewsActivity.class);
                        startActivity(sportsIntent);

                        return true;
                    } else if (itemId == R.id.nav_profile) {
                        Intent profileIntent = new Intent(ViewNewsActivity.this, UserProfileActivity.class);
                        startActivity(profileIntent);



                        return true; // Placeholder, implement Profile navigation if needed
                    }
                    return false; // Return false if the item ID is not handled
                }
            });
        }


        // --- News Card 1 ---
        View cardCareerFestLayout = findViewById(R.id.news_card_career_fest_container);
        // Ensure R.id.news_card_career_fest_container exists in activity_view_news.xml
        if (cardCareerFestLayout != null) {
            ImageView imageCareerFest = cardCareerFestLayout.findViewById(R.id.news_image);
            TextView titleCareerFest = cardCareerFestLayout.findViewById(R.id.news_title);
            TextView dateCareerFest = cardCareerFestLayout.findViewById(R.id.news_date);

            // Make sure these resources exist
            imageCareerFest.setImageResource(R.drawable.news1);
            imageCareerFest.setContentDescription(getString(R.string.cd_news_image_career_fest));
            titleCareerFest.setText(getString(R.string.news_title_career_fest));
            dateCareerFest.setText(getString(R.string.news_date_career_fest));

            cardCareerFestLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openNewsDetail("news_item_1"); // Firebase ID for Career Fest
                }
            });
        }


        // --- News Card 2 ---
        View cardTechnoGameLayout = findViewById(R.id.news_card_techno_game_container);
        // Ensure R.id.news_card_techno_game_container exists in activity_view_news.xml
        if (cardTechnoGameLayout != null) {
            ImageView imageTechnoGame = cardTechnoGameLayout.findViewById(R.id.news_image);
            TextView titleTechnoGame = cardTechnoGameLayout.findViewById(R.id.news_title);
            TextView dateTechnoGame = cardTechnoGameLayout.findViewById(R.id.news_date);

            imageTechnoGame.setImageResource(R.drawable.news2);
            imageTechnoGame.setContentDescription(getString(R.string.cd_news_image_techno_game));
            titleTechnoGame.setText(getString(R.string.news_title_techno_game));
            dateTechnoGame.setText(getString(R.string.news_date_techno_game));

            cardTechnoGameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openNewsDetail("news_item_2"); // Firebase ID for Techno Game Fest
                }
            });
        }


        // --- News Card 3 ---
        View cardFotSymposiumLayout = findViewById(R.id.news_card_fot_symposium_container);
        // Ensure R.id.news_card_fot_symposium_container exists in activity_view_news.xml
        if (cardFotSymposiumLayout != null) {
            ImageView imageFotSymposium = cardFotSymposiumLayout.findViewById(R.id.news_image);
            TextView titleFotSymposium = cardFotSymposiumLayout.findViewById(R.id.news_title);
            TextView dateFotSymposium = cardFotSymposiumLayout.findViewById(R.id.news_date);

            imageFotSymposium.setImageResource(R.drawable.news3);
            imageFotSymposium.setContentDescription(getString(R.string.cd_news_image_fot_symposium));
            titleFotSymposium.setText(getString(R.string.news_title_fot_symposium));
            dateFotSymposium.setText(getString(R.string.news_date_fot_symposium));

            cardFotSymposiumLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openNewsDetail("news_item_3"); // Firebase ID for FOT Symposium
                }
            });
        }

    } // <-- Closing onCreate()

    // Helper method to open NewsDetailActivity
    private void openNewsDetail(String newsId) {
        Intent intent = new Intent(ViewNewsActivity.this, NewsDetailActivity.class);
        // Ensure NewsDetailActivity.EXTRA_NEWS_ID is a public static final String in NewsDetailActivity
        intent.putExtra(NewsDetailActivity.EXTRA_NEWS_ID, newsId);
        startActivity(intent);
    }

} // <-- Closing class


