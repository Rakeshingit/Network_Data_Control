package com.example.network_data_control;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NetworkMonitor {
    private static final String CHANNEL_ID = "network_notification_channel";
    private static final int NOTIFICATION_ID = 123;

    private Context context;
    private TelephonyManager telephonyManager;
    private NotificationManagerCompat notificationManager;
    private OnNetworkChangeListener networkChangeListener;

    public NetworkMonitor(Context context) {
        this.context = context;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        notificationManager = NotificationManagerCompat.from(context);
        registerNetworkChangeReceiver();
        registerServiceStateListener();
    }

    public interface OnNetworkChangeListener {
        void onNetworkChange(String networkType);
    }

    public void setOnNetworkChangeListener(OnNetworkChangeListener listener) {
        this.networkChangeListener = listener;
    }

    private void registerNetworkChangeReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleNetworkChange();
            }
        }, intentFilter);
    }

    private void registerServiceStateListener() {
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onServiceStateChanged(ServiceState serviceState) {
                handleNetworkChange();
            }
        }, PhoneStateListener.LISTEN_SERVICE_STATE);
    }

    private void handleNetworkChange() {
        if (context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.e("NetworkMonitor", "READ_PHONE_STATE permission not granted");
            return;
        }

        try {
            int networkType = telephonyManager.getDataNetworkType();
            String networkStatus = getNetworkStatus(networkType);
            if (networkChangeListener != null) {
                networkChangeListener.onNetworkChange(networkStatus);
            }
            showNotification("Network status changed", "Current network: " + networkStatus);
        } catch (SecurityException e) {
            Log.e("NetworkMonitor", "SecurityException: " + e.getMessage());
        }
    }


    private String getNetworkStatus(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G";
            default:
                return "Unknown";
        }
    }

    private void showNotification(String title, String content) {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Network Changes", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info) // Use the default system icon
                    .setContentTitle(title)
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            Log.e("NetworkMonitor", "Notification permission not granted");
        }
    }
}
