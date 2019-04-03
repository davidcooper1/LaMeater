package com.example.lameater;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

// This class is used as a wrapper for activities.

public class PermissionActivity extends AppCompatActivity {

    protected final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private boolean receiverAdded = false;

    protected void onPostCreate(Bundle savedInstanceBundle) {
        super.onPostCreate(savedInstanceBundle);
    }

    protected void obtainPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_COARSE_LOCATION
            );
        } else {
            TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();
            registerReceiver(fetcher.getReceiver(), fetcher.getFilter());
            receiverAdded = true;

            onPermissionGranted(PERMISSION_REQUEST_COARSE_LOCATION);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();
                    registerReceiver(fetcher.getReceiver(), fetcher.getFilter());
                    receiverAdded = true;

                    onPermissionGranted(requestCode);
                } else {
                    onPermissionDenied(requestCode);
                }
                break;
        }
    }

    protected void onPermissionGranted(int requestCode) { }

    protected void onPermissionDenied(int requestCode) { }

    protected void setCallbacks() { }

    protected void onDestroy() {
        super.onDestroy();
        if (receiverAdded) {
            unregisterReceiver(MeaterData.getInstance().getFetcher().getReceiver());
            receiverAdded = false;
        }
    }

    protected void onStart() {
        super.onStart();
        if (BluetoothAdapter.getDefaultAdapter().isDiscovering())
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        setCallbacks();
        obtainPermissions();
    }

    protected void onStop() {
        super.onStop();
        if (receiverAdded) {
            unregisterReceiver(MeaterData.getInstance().getFetcher().getReceiver());
            receiverAdded = false;
        }
    }

}
