package com.example.proximitysensor;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "ProximitySensorApp";
    private TextView statusTxt;
    private TextView distanceValueTxt;
    private ImageView statusIcon;
    private View topBackground;
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private ConstraintLayout mainLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        mainLayout = findViewById(R.id.main);
        statusTxt = findViewById(R.id.textView2);
        distanceValueTxt = findViewById(R.id.distanceValue);
        statusIcon = findViewById(R.id.statusIcon);
        topBackground = findViewById(R.id.topBackground);

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        if (proximitySensor == null) {
            statusTxt.setText("Sensor Not Found");
            distanceValueTxt.setText("N/A");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "Sensor listener registered");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            Log.d(TAG, "Sensor listener unregistered");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            Log.d(TAG, "Sensor value: " + distance);

            distanceValueTxt.setText(String.format("Distance: %.1f cm", distance));

            if (distance < proximitySensor.getMaximumRange()) {
                // Near state
                statusTxt.setText("Object Detected");
                statusTxt.setTextColor(Color.parseColor("#E74C3C")); // Red
                statusIcon.setColorFilter(Color.parseColor("#E74C3C"));
                topBackground.setBackgroundColor(Color.parseColor("#E74C3C"));
            } else {
                // Far state
                statusTxt.setText("Path Clear");
                statusTxt.setTextColor(Color.parseColor("#2D3436")); // Dark Grey
                statusIcon.setColorFilter(Color.parseColor("#3F51B5")); // Primary Blue
                topBackground.setBackgroundColor(Color.parseColor("#3F51B5"));
            }
        }
    }
}
