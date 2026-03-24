package com.example.routefinderke;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

/**
 * SplashActivity - Fixed black screen and improved animations! 🎬🚀
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 1. Fix the black screen by loading the background safely
        ImageView splashBg = findViewById(R.id.imgSplashBackground);
        if (splashBg != null) {
            Glide.with(this)
                 .load(R.drawable.img_2) // Verified background
                 .centerCrop()
                 .into(splashBg);
        }

        // 2. Load and Animate img_4 (The Path Chooser)
        ImageView smilingCartoon = findViewById(R.id.ivSmilingCartoon);
        if (smilingCartoon != null) {
            // Standard load handles both PNG and GIF automatically
            Glide.with(this)
                 .load(R.drawable.img_4)
                 .placeholder(R.drawable.smiling_cartoon)
                 .into(smilingCartoon);

            // Add a "Pulse" animation to make the static PNG feel like a GIF
            Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
            smilingCartoon.startAnimation(pulse);
        }

        // 3. Animate the Text Container
        LinearLayout logoContainer = findViewById(R.id.splash_content_container);
        if (logoContainer != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            logoContainer.startAnimation(fadeIn);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 4000);
    }
}
