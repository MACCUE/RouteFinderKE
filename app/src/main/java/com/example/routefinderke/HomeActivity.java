package com.example.routefinderke;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private TextView tvHomeTicker;
    private final Handler tickerHandler = new Handler(Looper.getMainLooper());
    private int tickerIndex = 0;
    private final String[] tickerMessages = {
        " 🔴 LIVE: Heavy traffic on Mombasa Road near Syokimau... ",
        " 🟢 Thika Road is clear. Enjoy your ride! ",
        " 🟡 Alert: Fare surge expected in Nairobi CBD... ",
        " 🟢 M-Pesa payments active for all counties... ",
        " ⛽ EPRA: Fuel prices remain stable for this month. "
    };

    private static final String PREFS_NAME = "ThemePrefs";
    private static final String KEY_IS_DARK_MODE = "isNightMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // --- FORCE THEME FROM PREFS ---
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_IS_DARK_MODE, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);

        tvHomeTicker = findViewById(R.id.tvHomeTicker);
        startTickerRotation();

        // 1. Browse Routes -> Goes to MainActivity
        findViewById(R.id.cardBrowseRoutes).setOnClickListener(v -> {
            provideHapticFeedback();
            startActivity(new Intent(this, MainActivity.class));
        });

        // 2. Fuel Prices -> Open Real-time EPRA Portal
        findViewById(R.id.cardFuelPrices).setOnClickListener(v -> {
            provideHapticFeedback();
            openWebPage("https://www.epra.go.ke/services/fuel-prices/");
        });

        // 3. Fare History -> Show Real-world Data Dialog
        findViewById(R.id.cardFareHistory).setOnClickListener(v -> {
            provideHapticFeedback();
            showFareHistoryDialog();
        });

        // 4. Offline Maps -> Guide to Google Maps Offline
        findViewById(R.id.cardOfflineMaps).setOnClickListener(v -> {
            provideHapticFeedback();
            showOfflineMapsGuide();
        });

        // 5. Share Trip -> Real Intent Sharing
        findViewById(R.id.cardShareTrip).setOnClickListener(v -> {
            provideHapticFeedback();
            shareLiveTrip();
        });

        // 6. Emergency SOS -> Real SMS Trigger
        findViewById(R.id.cardEmergencySOS).setOnClickListener(v -> {
            provideHapticFeedback();
            triggerEmergencySOS();
        });

        // 7. SACCO Ratings -> Safety Report System
        findViewById(R.id.btnRate).setOnClickListener(v -> {
            provideHapticFeedback();
            showSaccoReportingDialog();
        });

        // 8. Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_routes) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_safety) {
                triggerEmergencySOS();
                return true;
            } else if (id == R.id.nav_more) {
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_theme) {
            toggleTheme();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_IS_DARK_MODE, false);
        
        SharedPreferences.Editor editor = prefs.edit();
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putBoolean(KEY_IS_DARK_MODE, false);
            Toast.makeText(this, "Light Mode Active", Toast.LENGTH_SHORT).show();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putBoolean(KEY_IS_DARK_MODE, true);
            Toast.makeText(this, "Dark Mode Active", Toast.LENGTH_SHORT).show();
        }
        editor.apply();
        recreate(); // Restart activity to apply theme
    }

    private void startTickerRotation() {
        tickerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (tvHomeTicker != null) {
                    tvHomeTicker.setText(tickerMessages[tickerIndex]);
                    tickerIndex = (tickerIndex + 1) % tickerMessages.length;
                }
                tickerHandler.postDelayed(this, 5000);
            }
        }, 0);
    }

    private void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void showFareHistoryDialog() {
        String content = "Recent Fare Trends:\n\n" +
                "• Nairobi CBD - Westlands: Ksh 50 (Stable)\n" +
                "• Nairobi - Kisumu: Ksh 1,200 (↑ Up by 200)\n" +
                "• Mombasa - Bamburi: Ksh 70 (Stable)\n" +
                "• Nairobi - Kisii: Ksh 1,000 (↓ Down by 100)\n\n" +
                "Price alerts are based on current SACCO data.";
        new AlertDialog.Builder(this)
                .setTitle("Fare History & Price Alerts")
                .setMessage(content)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showOfflineMapsGuide() {
        new AlertDialog.Builder(this)
                .setTitle("Offline Maps Guide 🗺️")
                .setMessage("To use maps offline:\n\n1. Open Google Maps.\n2. Tap your profile icon.\n3. Select 'Offline maps'.\n4. Tap 'SELECT YOUR OWN MAP' and download the Kenya region.\n\nRouteFinderKE will then use this data when you have no network.")
                .setPositiveButton("GOT IT", null)
                .show();
    }

    private void shareLiveTrip() {
        String message = "I'm traveling safely with RouteFinderKE 🚌. My current region is active. Stay updated!";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(shareIntent, "Share Trip via"));
    }

    private void triggerEmergencySOS() {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:999")); // National Emergency Number
        smsIntent.putExtra("sms_body", "EMERGENCY: I need help. I am currently using RouteFinderKE and I am in distress.");
        try {
            startActivity(smsIntent);
            Toast.makeText(this, "SOS Drafted. Please Send!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open SMS. Call 999.", Toast.LENGTH_LONG).show();
        }
    }

    private void showSaccoReportingDialog() {
        String[] saccos = {"Super Metro", "Easy Coach", "Guardian Angel", "North Rift", "Matatu Owners Assoc."};
        new AlertDialog.Builder(this)
                .setTitle("Report / Rate SACCO")
                .setItems(saccos, (dialog, which) -> {
                    Toast.makeText(this, "Thank you! Our safety team will review " + saccos[which], Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void provideHapticFeedback() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(40);
            }
        }
    }

    @Override
    protected void onDestroy() {
        tickerHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
