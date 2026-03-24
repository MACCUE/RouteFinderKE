package com.example.routefinderke;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import io.github.sceneview.ar.ArSceneView;
import io.github.sceneview.ar.node.ArModelNode;

/**
 * ARNavigationActivity - Advanced Sceneview Version 🚀
 */
public class ARNavigationActivity extends AppCompatActivity {
    
    private ArSceneView sceneView;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_navigation);

        // 1. Setup UI
        tvStatus = findViewById(R.id.tvArStatus);
        findViewById(R.id.btnCloseAR).setOnClickListener(v -> finish());

        // 2. Initialize SceneView
        sceneView = findViewById(R.id.sceneView);
        
        // Advanced: Place the arrow as soon as the user is ready
        tvStatus.setText("SYSTEM: INITIALIZING SCENEVIEW...");
        
        // In a real hackathon, you'd trigger this on a button click or surface detection
        placeSceneviewArrow();
    }

    /**
     * The "Cheat Code" method for placing 3D models easily.
     */
    private void placeSceneviewArrow() {
        try {
            // 1. Create the Model Node (The Arrow)
            ArModelNode arrowNode = new ArModelNode(sceneView.getEngine());
            
            // 2. Load the 3D model from your 'assets' folder
            // Make sure you have an 'assets/models' folder with your .glb file!
            // FIXED: boolean parameter cannot be null in Java. Using 'true' for autoAnimate.
            arrowNode.loadModelGlbAsync(
                "models/navigation_arrow.glb", 
                true, 
                null, 
                null, 
                null, 
                null
            );
            
            // 3. Pin it to the center of the screen
            sceneView.addChild(arrowNode);
            arrowNode.anchor(); 
            
            tvStatus.setText("SYSTEM: NAVIGATION ARROW ACTIVE");
            Toast.makeText(this, "Navigation Arrow Fixed to Road!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("ARNavigation", "Error placing arrow", e);
            tvStatus.setText("SYSTEM: AR PLACEMENT FAILED");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Sceneview handles session pausing automatically!
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources
    }
}
