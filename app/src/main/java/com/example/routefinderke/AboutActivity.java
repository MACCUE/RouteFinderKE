package com.example.routefinderke;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * AboutActivity - Features "About Us", "Night Mode" Toggle, and "Share App" 🌙📲
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Button btnNightMode = findViewById(R.id.btnNightMode);
        Button btnShareApp = findViewById(R.id.btnShareApp);

        // 1. Handle Night Mode Toggle
        btnNightMode.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
            boolean isNightMode = sharedPreferences.getBoolean("isNightMode", false);

            if (isNightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedPreferences.edit().putBoolean("isNightMode", false).apply();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                sharedPreferences.edit().putBoolean("isNightMode", true).apply();
            }
            recreate();
        });

        // 2. Handle Share App (WhatsApp / Social Media)
        btnShareApp.setOnClickListener(v -> {
            String shareMessage = "Hey! Check out RouteFinderKE 🚌. It's a smart app for Kenyan routes with Live Traffic and AR Navigation. \n\nDownload the APK and try it out!";
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            
            // This opens the sharing menu (including WhatsApp)
            startActivity(Intent.createChooser(shareIntent, "Share RouteFinderKE via"));
        });
    }
}
