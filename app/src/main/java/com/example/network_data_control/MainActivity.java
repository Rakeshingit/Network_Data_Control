package com.example.network_data_control;


import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements NetworkMonitor.OnNetworkChangeListener {
    private NetworkMonitor networkMonitor;
    private TextView networkStatusTextView;
    private static final int REQUEST_PHONE_STATE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkStatusTextView = findViewById(R.id.NetStatus); // Reference to the TextView

        // Check if permission is not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE_PERMISSION);
        } else {
            // Permission is already granted, proceed with initializing NetworkMonitor
            initializeNetworkMonitor();
        }

        // Start the background service for network monitoring
        startService(new Intent(this, NetworkMonitoringService.class));
    }

    // Initialize NetworkMonitor
    private void initializeNetworkMonitor() {
        networkMonitor = new NetworkMonitor(this);
        networkMonitor.setOnNetworkChangeListener(this); // Set the listener
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_STATE_PERMISSION) {
            // Check if the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with initializing NetworkMonitor
                initializeNetworkMonitor();
            } else {
                // Permission denied, handle accordingly
                // For example, display a message or disable features that require the permission
            }
        }
    }

    @Override
    public void onNetworkChange(String networkType) {
        // Update the TextView with the current network status
        networkStatusTextView.setText("Network status: " + networkType);
    }
}