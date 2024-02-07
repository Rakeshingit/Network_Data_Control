package com.example.network_data_control;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.widget.Toast;

public class NetworkMonitoringService extends Service {

    private NetworkChangeReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Service is started
        return START_STICKY; // Service will be restarted if terminated by the system
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister the receiver when service is destroyed
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // BroadcastReceiver to listen for network change events
    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (isNetworkConnected(context)) {
                // Network is connected, show toast notification
                Toast.makeText(context, "Network status changed", Toast.LENGTH_SHORT).show();
            }
        }

        private boolean isNetworkConnected(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        }
    }
}
