package com.example.network_data_control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
//import android.os.Looper;
//import androidx.core.content.ContextCompat;
import android.net.ConnectivityManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.concurrent.Executor;

public class NetworkMonitor {

    private Context context;
    private ConnectivityManager connectivityManager;
    private TelephonyManager telephonyManager;

    public NetworkMonitor(Context context) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        registerNetworkChangeReceiver();
        registerServiceStateListener();
    }

    private void registerNetworkChangeReceiver() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
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
        // Check for the READ_PHONE_STATE permission
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            // TODO: Handle the case where permission is not granted. You can request the permission here.
            Log.e("NetworkMonitor","READ_PHONE_STATE permission not granted");
            return;
        }
        int networkType = telephonyManager.getDataNetworkType();

        if (networkType == TelephonyManager.NETWORK_TYPE_LTE) {
            showToast("LTE network detected.");
        } else if (networkType == TelephonyManager.NETWORK_TYPE_NR) {
            showToast("5G network detected.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
