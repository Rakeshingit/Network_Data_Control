package com.example.network_data_control;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {



    private NetworkMonitor networkMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkMonitor = new NetworkMonitor(this);
    }

    // No need to unregister receiver for NetworkMonitor as it doesn't extend BroadcastReceiver
}