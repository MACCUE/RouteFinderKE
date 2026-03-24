package com.example.routefinderke;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * MainActivity - The Ultimate Kenya Routes Hub.
 * Features: Live News Ticker, National County Database, AI Voice, and Advanced Nairobi Network.
 */
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, SensorEventListener {
    private static List<Route> allRoutes;
    private List<Route> displayList;
    private RecyclerView recyclerView;
    private RouteAdapter adapter;
    private boolean isShowingCounties = true;
    private TextToSpeech tts;
    private SensorManager sensorManager;
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    
    private final Handler tickerHandler = new Handler(Looper.getMainLooper());
    private int tickerIndex = 0;
    private final String[] tickerMessages = {
        " 🔴 LIVE UPDATES: Heavy traffic on Mombasa Road near Syokimau... ",
        " 🟢 Thika Road is clear. Enjoy your ride! ",
        " 🟡 Alert: Fare surge expected in Nairobi CBD due to rush hour... ",
        " 🟢 M-Pesa STK Push payments enabled for all 47 counties... ",
        " 🌧️ Weather: Light rain in Kisumu. Carry an umbrella! ",
        " 🎫 NEW: Show your Digital QR Ticket to the conductor... "
    };

    private final ActivityResultLauncher<Intent> voiceSearchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && !matches.isEmpty()) {
                        handleVoiceCommand(matches.get(0).toLowerCase());
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Kenya Routes Live");
            toolbar.setLogo(R.drawable.ic_bus);
        }

        // 1. DYNAMIC LIVE TICKER
        TextView tvTicker = findViewById(R.id.tvTrafficTicker);
        if (tvTicker != null) {
            tvTicker.setSelected(true);
            startTickerRotation(tvTicker);
        }

        // 2. CITY LIVE STREAM CARD
        View cardLiveCam = findViewById(R.id.cardLiveCam);
        if (cardLiveCam != null) {
            cardLiveCam.setOnClickListener(v -> {
                provideHapticFeedback();
                startActivity(new Intent(this, TrafficCamActivity.class));
            });
        }

        // 3. ANIMATED BACKGROUND
        ImageView animatedBg = findViewById(R.id.ivAnimatedBg);
        if (animatedBg != null) {
            animatedBg.setImageResource(R.drawable.img_4);
            animatedBg.animate()
                    .translationX(-150f)
                    .setDuration(15000)
                    .withEndAction(() -> animatedBg.animate().translationX(150f).setDuration(15000).start())
                    .start();
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
        acceleration = 10f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;

        tts = new TextToSpeech(this, this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        initializeRoutes();
        showCountyList();

        ExtendedFloatingActionButton fab = findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                provideHapticFeedback();
                if (!isShowingCounties) {
                    showCountyList();
                    speak("Returning to county selection.");
                } else {
                    Toast.makeText(this, "Shake your phone to refresh!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void startTickerRotation(TextView tv) {
        tickerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv.setText(tickerMessages[tickerIndex]);
                tickerIndex = (tickerIndex + 1) % tickerMessages.length;
                tickerHandler.postDelayed(this, 10000); 
            }
        }, 0);
    }

    private void initializeRoutes() {
        if (allRoutes == null) {
            allRoutes = new ArrayList<>();
            
            // --- NAIROBI NETWORK (20 ROUTES) ---
            addNairobiRoute("NRB-01", "CBD", "Kayole", Arrays.asList("Donholm", "Komarock"), -1.2833, 36.8233, -1.2783, 36.9123, "554433");
            addNairobiRoute("NRB-02", "CBD", "Pipeline", Collections.singletonList("Imara Daima"), -1.2833, 36.8233, -1.3180, 36.8920, "112233");
            addNairobiRoute("NRB-03", "CBD", "Kibera", Collections.singletonList("Olympic"), -1.2833, 36.8233, -1.313, 36.788, "998877");
            addNairobiRoute("NRB-04", "CBD", "Westlands", Collections.singletonList("Museum Hill"), -1.2833, 36.8233, -1.263, 36.804, "445566");
            addNairobiRoute("NRB-05", "CBD", "Kawangware", Arrays.asList("Valley Road", "Adams"), -1.2833, 36.8233, -1.284, 36.741, "223344");
            addNairobiRoute("NRB-06", "CBD", "Githurai 45", Collections.singletonList("Roysambu"), -1.2833, 36.8233, -1.203, 36.911, "667788");
            addNairobiRoute("NRB-07", "CBD", "Kasarani", Collections.singletonList("Mwiki"), -1.2833, 36.8233, -1.221, 36.897, "112244");
            addNairobiRoute("NRB-08", "CBD", "Rongai", Collections.singletonList("Langata"), -1.2833, 36.8233, -1.394, 36.762, "556677");
            addNairobiRoute("NRB-09", "CBD", "Ngong", Collections.singletonList("Karen"), -1.2833, 36.8233, -1.361, 36.656, "889900");
            addNairobiRoute("NRB-10", "CBD", "Kikuyu", Collections.singletonList("Kangemi"), -1.2833, 36.8233, -1.248, 36.661, "334455");
            addNairobiRoute("NRB-11", "CBD", "Embakasi", Collections.singletonList("Mombasa Road"), -1.2833, 36.8233, -1.321, 36.914, "778899");
            addNairobiRoute("NRB-12", "CBD", "Dandora", Collections.singletonList("Civil Servants"), -1.2833, 36.8233, -1.247, 36.897, "113355");
            addNairobiRoute("NRB-13", "CBD", "South B", Collections.singletonList("South C"), -1.2833, 36.8233, -1.311, 36.834, "224466");
            addNairobiRoute("NRB-14", "CBD", "Uthiru", Collections.singletonList("Kangemi"), -1.2833, 36.8233, -1.258, 36.734, "446688");
            addNairobiRoute("NRB-15", "CBD", "Buru Buru", Collections.singletonList("Jogoo Road"), -1.2833, 36.8233, -1.284, 36.867, "557799");
            addNairobiRoute("NRB-16", "CBD", "Zimmerman", Collections.singletonList("Safari Park"), -1.2833, 36.8233, -1.211, 36.891, "668800");
            addNairobiRoute("NRB-17", "CBD", "Huruma", Collections.singletonList("Kariobangi"), -1.2833, 36.8233, -1.254, 36.871, "779911");
            addNairobiRoute("NRB-18", "CBD", "Madaraka", Collections.singletonList("Strathmore"), -1.2833, 36.8233, -1.306, 36.814, "880022");
            addNairobiRoute("NRB-19", "CBD", "Eastleigh", Collections.singletonList("Starehe"), -1.2833, 36.8233, -1.274, 36.847, "991133");
            addNairobiRoute("NRB-20", "CBD", "Kahawa West", Collections.singletonList("Roysambu"), -1.2833, 36.8233, -1.181, 36.891, "112255");

            // --- KISUMU NETWORK (20 ROUTES) ---
            addKisumuRoute("KSM-01", "Main Bus Park", "Kondele", Arrays.asList("Patel", "Kibuye"), -0.102, 34.761, -0.091, 34.778, "882211");
            addKisumuRoute("KSM-02", "CBD", "Mamboleo", Collections.singletonList("Coptic"), -0.102, 34.761, -0.065, 34.795, "334455");
            addKisumuRoute("KSM-03", "Bus Park", "Maseno", Collections.singletonList("Kisian"), -0.102, 34.761, -0.004, 34.603, "119922");
            addKisumuRoute("KSM-04", "CBD", "Ahero", Collections.singletonList("Nyamasaria"), -0.102, 34.761, -0.173, 34.921, "556677");
            addKisumuRoute("KSM-05", "Town", "Milimani", Collections.singletonList("Tom Mboya"), -0.102, 34.761, -0.112, 34.755, "443322");
            addKisumuRoute("KSM-06", "Town", "Manyatta", Collections.singletonList("Flamingo"), -0.102, 34.761, -0.095, 34.785, "221100");
            addKisumuRoute("KSM-07", "CBD", "Nyalenda", Collections.singletonList("Kachok"), -0.102, 34.761, -0.125, 34.768, "667788");
            addKisumuRoute("KSM-08", "Bus Park", "Otonglo", Collections.singletonList("Bandani"), -0.102, 34.761, -0.105, 34.715, "112244");
            addKisumuRoute("KSM-09", "CBD", "Kibos", Collections.singletonList("Manyatta"), -0.102, 34.761, -0.081, 34.812, "993311");
            addKisumuRoute("KSM-10", "Town", "Kisian", Collections.singletonList("Otonglo"), -0.102, 34.761, -0.085, 34.667, "774411");
            addKisumuRoute("KSM-11", "CBD", "Katito", Collections.singletonList("Ahero"), -0.102, 34.761, -0.233, 34.933, "225588");
            addKisumuRoute("KSM-12", "Town", "Airport", Collections.singletonList("Bandani"), -0.102, 34.761, -0.085, 34.728, "112233");
            addKisumuRoute("KSM-13", "CBD", "Kibuye", Collections.singletonList("Jomo Kenyatta"), -0.102, 34.761, -0.098, 34.765, "445566");
            addKisumuRoute("KSM-14", "Town", "Nyamasaria", Collections.singletonList("Kachok"), -0.102, 34.761, -0.128, 34.805, "778899");
            addKisumuRoute("KSM-15", "CBD", "Migosi", Collections.singletonList("Kondele"), -0.102, 34.761, -0.085, 34.782, "113355");
            addKisumuRoute("KSM-16", "Town", "Lolwe", Collections.singletonList("Mamboleo"), -0.102, 34.761, -0.075, 34.792, "224466");
            addKisumuRoute("KSM-17", "CBD", "Riat", Collections.singletonList("Mamboleo"), -0.102, 34.761, -0.052, 34.785, "335577");
            addKisumuRoute("KSM-18", "Bus Park", "Dago", Collections.singletonList("Riat"), -0.102, 34.761, -0.035, 34.775, "446688");
            addKisumuRoute("KSM-19", "Town", "Kiboswa", Collections.singletonList("Riat"), -0.102, 34.761, -0.015, 34.785, "557799");
            addKisumuRoute("KSM-20", "CBD", "Paw Akuche", Collections.singletonList("Kondele"), -0.102, 34.761, -0.065, 34.825, "668800");

            // --- MOMBASA NETWORK (20 ROUTES) ---
            addMombasaRoute("MSA-01", "Posta", "Bamburi", Arrays.asList("Nyali", "Kengeleni"), -4.066, 39.666, -3.985, 39.712, "112233");
            addMombasaRoute("MSA-02", "Ferry", "Likoni", Collections.singletonList("Shelly Beach"), -4.078, 39.662, -4.095, 39.655, "445566");
            addMombasaRoute("MSA-03", "Posta", "Mtwapa", Collections.singletonList("Bamburi"), -4.066, 39.666, -3.945, 39.735, "778899");
            addMombasaRoute("MSA-04", "Town", "Changamwe", Collections.singletonList("Makande"), -4.066, 39.666, -4.025, 39.625, "113355");
            addMombasaRoute("MSA-05", "Posta", "Nyali", Collections.singletonList("Lights"), -4.066, 39.666, -4.035, 39.705, "224466");
            addMombasaRoute("MSA-06", "Town", "Mikindani", Collections.singletonList("Changamwe"), -4.066, 39.666, -4.005, 39.605, "335577");
            addMombasaRoute("MSA-07", "Ferry", "Diani", Collections.singletonList("Likoni"), -4.078, 39.662, -4.285, 39.585, "446688");
            addMombasaRoute("MSA-08", "Posta", "Kiembeni", Collections.singletonList("Bamburi"), -4.066, 39.666, -3.965, 39.705, "557799");
            addMombasaRoute("MSA-09", "Town", "Magongo", Collections.singletonList("Changamwe"), -4.066, 39.666, -4.015, 39.615, "668800");
            addMombasaRoute("MSA-10", "Posta", "Shanzu", Collections.singletonList("Mtwapa"), -4.066, 39.666, -3.955, 39.725, "779911");
            addMombasaRoute("MSA-11", "Town", "Jomvu", Collections.singletonList("Mikindani"), -4.066, 39.666, -3.995, 39.595, "880022");
            addMombasaRoute("MSA-12", "Ferry", "Tiwi", Collections.singletonList("Likoni"), -4.078, 39.662, -4.225, 39.595, "991133");
            addMombasaRoute("MSA-13", "Posta", "Kisauni", Collections.singletonList("Nyali"), -4.066, 39.666, -4.045, 39.685, "112244");
            addMombasaRoute("MSA-14", "Town", "Mazeras", Collections.singletonList("Jomvu"), -4.066, 39.666, -3.975, 39.555, "223355");
            addMombasaRoute("MSA-15", "Posta", "Utange", Collections.singletonList("Bamburi"), -4.066, 39.666, -3.975, 39.725, "334466");
            addMombasaRoute("MSA-16", "Town", "Mariakani", Collections.singletonList("Mazeras"), -4.066, 39.666, -3.865, 39.475, "445577");
            addMombasaRoute("MSA-17", "Ferry", "Msambweni", Collections.singletonList("Diani"), -4.078, 39.662, -4.465, 39.485, "556688");
            addMombasaRoute("MSA-18", "Posta", "Vipingo", Collections.singletonList("Mtwapa"), -4.066, 39.666, -3.815, 39.815, "667799");
            addMombasaRoute("MSA-19", "Town", "Miritini", Collections.singletonList("Jomvu"), -4.066, 39.666, -4.005, 39.575, "778800");
            addMombasaRoute("MSA-20", "Posta", "Kilifi", Collections.singletonList("Mtwapa"), -4.066, 39.666, -3.635, 39.855, "889911");

            // --- NATIONAL COUNTY FALLBACKS ---
            String[] counties = {"Kwale", "Kilifi", "Tana River", "Lamu", "Taita Taveta", "Garissa", "Wajir", "Mandera", "Marsabit", "Isiolo", "Meru", "Tharaka-Nithi", "Embu", "Kitui", "Machakos", "Makueni", "Nyandarua", "Nyeri", "Kirinyaga", "Murang'a", "Kiambu", "Turkana", "West Pokot", "Samburu", "Trans Nzoia", "Uasin Gishu", "Elgeyo Marakwet", "Nandi", "Baringo", "Laikipia", "Nakuru", "Narok", "Kajiado", "Kericho", "Bomet", "Kakamega", "Vihiga", "Bungoma", "Busia", "Siaya", "Homa Bay", "Migori", "Kisii", "Nyamira"};
            for (String c : counties) {
                addCountyRoutes(c, "Main Town", "Sub-County", Collections.singletonList("Main Stage"), -1.0, 36.0, -1.1, 36.1, "000000");
            }
        }
    }

    private void addNairobiRoute(String code, String start, String dest, List<String> stops, double sLat, double sLng, double dLat, double dLng, String till) {
        allRoutes.add(new Route(code, start, dest, stops, "30-100", R.drawable.img_2, "Nairobi", sLat, sLng, dLat, dLng, till));
    }

    private void addKisumuRoute(String code, String start, String dest, List<String> stops, double sLat, double sLng, double dLat, double dLng, String till) {
        allRoutes.add(new Route(code, start, dest, stops, "30-150", R.drawable.photo, "Kisumu", sLat, sLng, dLat, dLng, till));
    }

    private void addMombasaRoute(String code, String start, String dest, List<String> stops, double sLat, double sLng, double dLat, double dLng, String till) {
        allRoutes.add(new Route(code, start, dest, stops, "50-120", R.drawable.img_2, "Mombasa", sLat, sLng, dLat, dLng, till));
    }

    private void addCountyRoutes(String county, String start, String dest, List<String> stops, double sLat, double sLng, double dLat, double dLng, String till) {
        allRoutes.add(new Route("RT-" + (allRoutes.size() + 1), start, dest, stops, "50-200", R.drawable.img_2, county, sLat, sLng, dLat, dLng, till));
    }

    private void showCountyList() {
        isShowingCounties = true;
        displayList = new ArrayList<>();
        List<String> seen = new ArrayList<>();
        for (Route r : allRoutes) {
            if (!seen.contains(r.getCounty())) {
                displayList.add(r);
                seen.add(r.getCounty());
            }
        }
        updateAdapter();
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Kenya Counties (47)");
    }

    public void onCountyTapped(String county) {
        provideHapticFeedback();
        isShowingCounties = false;
        displayList = new ArrayList<>();
        for (Route r : allRoutes) {
            if (r.getCounty().equalsIgnoreCase(county)) displayList.add(r);
        }
        updateAdapter();
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(county + " Routes");
        speak("Showing routes for " + county);
    }

    private void updateAdapter() {
        adapter = new RouteAdapter(this, displayList);
        recyclerView.setAdapter(adapter);
    }

    private void handleVoiceCommand(String query) {
        if (query.contains("live") || query.contains("nairobi city")) {
            startActivity(new Intent(this, TrafficCamActivity.class));
            return;
        }
        for (Route r : allRoutes) {
            if (r.getCounty().toLowerCase().contains(query) || r.getDestination().toLowerCase().contains(query)) {
                onCountyTapped(r.getCounty());
                return;
            }
        }
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

    private void speak(String text) {
        if (tts != null) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) tts.setLanguage(Locale.UK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) { return false; }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        applyFilter(newText);
                        return true;
                    }
                });
            }
        }
        return true;
    }

    private void applyFilter(String text) {
        displayList.clear();
        for (Route r : allRoutes) {
            if (isShowingCounties) {
                if (r.getCounty().toLowerCase().contains(text.toLowerCase())) {
                    boolean alreadyIn = false;
                    for (Route d : displayList) if (d.getCounty().equalsIgnoreCase(r.getCounty())) alreadyIn = true;
                    if (!alreadyIn) displayList.add(r);
                }
            } else {
                if (r.getDestination().toLowerCase().contains(text.toLowerCase()) || 
                    r.getRouteNumber().toLowerCase().contains(text.toLowerCase())) {
                    displayList.add(r);
                }
            }
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        provideHapticFeedback();
        int id = item.getItemId();
        if (id == R.id.action_share_app) {
            shareApp();
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareApp() {
        String shareMessage = "Hey! Check out RouteFinderKE 🚌. It's a smart app for Kenyan routes with Live Traffic and AR Navigation. \n\nDownload the APK and try it out!";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Share RouteFinderKE via"));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0], y = event.values[1], z = event.values[2];
        lastAcceleration = currentAcceleration;
        currentAcceleration = (float) Math.sqrt(x * x + y * y + z * z);
        float delta = currentAcceleration - lastAcceleration;
        acceleration = acceleration * 0.9f + delta;
        if (acceleration > 12) {
            provideHapticFeedback();
            showCountyList();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        if (sensorManager != null) sensorManager.unregisterListener(this);
        tickerHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public boolean isShowingCounties() { return isShowingCounties; }
}
