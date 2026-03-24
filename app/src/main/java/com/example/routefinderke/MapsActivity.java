package com.example.routefinderke;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

/**
 * MapsActivity - Advanced Version with Voice and Traffic 🗺️
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TextToSpeech.OnInitListener {

    private Route route;
    private TextToSpeech tts;
    private Marker runnerMarker;
    private final Handler animationHandler = new Handler(Looper.getMainLooper());
    private float animationProgress = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize Voice (TTS)
        tts = new TextToSpeech(this, this);

        // Load the Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Get the Route data safely
        route = (Route) getIntent().getSerializableExtra("route_object");
        
        // Provide haptic feedback when entering navigation
        provideHapticFeedback();

        // Advanced: Set up Live Traffic Button
        MaterialButton btnLiveTraffic = findViewById(R.id.btnLiveTraffic);
        if (btnLiveTraffic != null) {
            btnLiveTraffic.setOnClickListener(v -> {
                provideHapticFeedback();
                speak("Accessing Live Traffic Surveillance.");
                // Ensure TrafficCamActivity exists before calling
                try {
                    Intent intent = new Intent(this, Class.forName("com.example.routefinderke.TrafficCamActivity"));
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    Toast.makeText(this, "Traffic Camera system coming soon!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // 1. ENABLE REAL-TIME TRAFFIC (Essential for Nairobi!)
        googleMap.setTrafficEnabled(true);
        Toast.makeText(this, "Nairobi Live Traffic Active 🔴🟡🟢", Toast.LENGTH_SHORT).show();

        // 2. Load Custom Map Style (DarkMode or Silver)
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success) Log.e("MapsActivity", "Style parsing failed.");
        } catch (Exception e) {
            Log.e("MapsActivity", "Could not load map style", e);
        }

        if (route != null) {
            // Get coordinates (Default to Nairobi CBD if coordinates are 0)
            double sLat = route.getStartLat() != 0 ? route.getStartLat() : -1.286389;
            double sLng = route.getStartLng() != 0 ? route.getStartLng() : 36.817223;
            double dLat = route.getDestLat() != 0 ? route.getDestLat() : -1.2633;
            double dLng = route.getDestLng() != 0 ? route.getDestLng() : 36.8044;

            LatLng start = new LatLng(sLat, sLng);
            LatLng destination = new LatLng(dLat, dLng);

            // Add Markers
            googleMap.addMarker(new MarkerOptions().position(start).title("Start: " + route.getStartPoint()));
            googleMap.addMarker(new MarkerOptions().position(destination).title("End: " + route.getDestination()));

            // Draw a high-visibility Green Polyline
            googleMap.addPolyline(new PolylineOptions()
                    .add(start, destination)
                    .width(18)
                    .color(Color.parseColor("#4CAF50")) // Bright Green
                    .geodesic(true));

            // Setup Animated Runner Icon
            runnerMarker = googleMap.addMarker(new MarkerOptions()
                    .position(start)
                    .icon(bitmapDescriptorFromVector(R.drawable.ic_runner))
                    .anchor(0.5f, 0.5f));

            // Focus Camera
            LatLngBounds bounds = new LatLngBounds.Builder().include(start).include(destination).build();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));

            // Start animation and announce route
            startRunnerAnimation(start, destination);
            speak("Route found. Navigating from " + route.getStartPoint() + " to " + route.getDestination());
        }
    }

    private void provideHapticFeedback() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(50);
            }
        }
    }

    private void startRunnerAnimation(LatLng start, LatLng end) {
        animationHandler.post(new Runnable() {
            @Override
            public void run() {
                animationProgress += 0.005f; // Slower, smoother animation
                if (animationProgress > 1.0f) animationProgress = 0f;

                double lat = start.latitude + (end.latitude - start.latitude) * animationProgress;
                double lng = start.longitude + (end.longitude - start.longitude) * animationProgress;
                
                if (runnerMarker != null) {
                    runnerMarker.setPosition(new LatLng(lat, lng));
                }
                animationHandler.postDelayed(this, 80);
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {
        try {
            Drawable vectorDrawable = ContextCompat.getDrawable(this, vectorResId);
            if (vectorDrawable == null) return BitmapDescriptorFactory.defaultMarker();
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        } catch (Exception e) {
            return BitmapDescriptorFactory.defaultMarker();
        }
    }

    private void speak(String text) {
        if (tts != null) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) tts.setLanguage(Locale.UK);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        animationHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
