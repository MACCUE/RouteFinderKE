package com.example.routefinderke;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * TrafficCamActivity - Integrated with RouteFinderKE Node.js Backend.
 * Optimized for Hackathon 2026 Presentation. 🇰🇪🚀
 */
public class TrafficCamActivity extends AppCompatActivity {

    private final Handler liveHandler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    private TextView tvTimestamp, tvUnitId, tvGps, tvFlow;
    private View liveDot;
    private WebView webView;
    private ProgressBar progressBar;
    private FrameLayout aiOverlay;

    // TODO: REPLACE THIS WITH YOUR IP FROM IPCONFIG
    private final String BACKEND_IP = "192.168.1.11";
    private final String BACKEND_URL = "http://" + BACKEND_IP + ":3000/api/traffic";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        setContentView(R.layout.activity_traffic_cam);

        webView = findViewById(R.id.webViewLiveTraffic);
        aiOverlay = findViewById(R.id.aiOverlayContainer);
        progressBar = findViewById(R.id.pbLoadingCam);
        tvTimestamp = findViewById(R.id.tvLiveTimestamp);
        liveDot = findViewById(R.id.viewLiveDot);
        tvUnitId = findViewById(R.id.tvUnitId);
        tvGps = findViewById(R.id.tvGpsCoords);
        tvFlow = findViewById(R.id.tvTrafficFlow);
        View scanLine = findViewById(R.id.viewScanLine);

        setupLiveStream();

        if (scanLine != null) {
            TranslateAnimation anim = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 1f);
            anim.setDuration(3000);
            anim.setRepeatCount(Animation.INFINITE);
            scanLine.startAnimation(anim);
        }

        startLiveSystem();

        findViewById(R.id.btnCloseFeed).setOnClickListener(v -> finish());
    }

    private void setupLiveStream() {
        if (webView == null) return;

        webView.setBackgroundColor(Color.BLACK);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(TrafficCamActivity.this, "Backend Offline: Check Node.js Terminal", Toast.LENGTH_LONG).show();
            }
        });

        // Points to your Node.js server!
        webView.loadUrl(BACKEND_URL);
    }

    private void startLiveSystem() {
        liveHandler.post(new Runnable() {
            @Override
            public void run() {
                if (tvTimestamp != null) {
                    tvTimestamp.setText(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
                }

                if (liveDot != null) {
                    liveDot.setVisibility(liveDot.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                }

                simulateAIDetection();
                liveHandler.postDelayed(this, 1000);
            }
        });
    }

    private void simulateAIDetection() {
        if (aiOverlay == null) return;
        aiOverlay.removeAllViews();

        if (tvUnitId != null) tvUnitId.setText(String.format(Locale.getDefault(), "SYS-NODE: %s", BACKEND_IP));

        int count = 2 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            View box = new View(this);
            int size = 120 + random.nextInt(80);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size, size);

            int w = Math.max(aiOverlay.getWidth(), 1);
            int h = Math.max(aiOverlay.getHeight(), 1);
            if (w > size && h > size) {
                lp.leftMargin = random.nextInt(w - size);
                lp.topMargin = random.nextInt(h - size);
                box.setLayoutParams(lp);
                box.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_accent_bg));
                box.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4000FF00")));
                aiOverlay.addView(box);
            }
        }
    }

    @Override
    protected void onPause() { super.onPause(); if (webView != null) webView.onPause(); }

    @Override
    protected void onResume() { super.onResume(); if (webView != null) webView.onResume(); }

    @Override
    protected void onDestroy() {
        if (webView != null) webView.destroy();
        liveHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}