package com.example.lameater;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

// This class is used as a wrapper for activities.

public class PermissionActivity extends AppCompatActivity {

    protected final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    protected final int REQUEST_ENABLE_BT = 2;

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

    protected void onPermissionDenied(int requestCode) {
        new AlertDialog.Builder(this)
                .setTitle("Need Location Permission")
                .setMessage("In order to automatically connect to the device, this app needs permission to access coarse location.")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        obtainPermissions();
                    }
                }).show();
    }

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
        final TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();

        fetcher.setCallback(fetcher.CALLBACK_BLUETOOTH_DISABLED, new Runnable() {
            public void run() {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        });

        fetcher.setCallback(fetcher.CALLBACK_DEVICE_NOT_FOUND, new Runnable(){
            //Runs the alert dialog pop-up when LaMeater device is not found.
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        //Creates the Alert itself
                        new AlertDialog.Builder(PermissionActivity.this)
                                .setTitle("Unable to Find LaMeater Device")
                                .setMessage("Make sure the device is turned on then press retry.")
                                //Confirmation button. If pressed, device will attempt to reconnect to LaMeater.
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        fetcher.connect();
                                    }
                                }).show(); // Create the alert
                    }
                });
            }
        });

        setCallbacks();
        obtainPermissions();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                new AlertDialog.Builder(PermissionActivity.this)
                        .setTitle("Bluetooth Needs to be Enabled")
                        .setMessage("In order to connect to the device, Bluetooth must be enabled.")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                fetcher.connect();
                            }
                        }).show();
            } else {
                fetcher.connect();
            }
        }
    }

    protected void onStop() {
        super.onStop();
        if (receiverAdded) {
            unregisterReceiver(MeaterData.getInstance().getFetcher().getReceiver());
            receiverAdded = false;
        }
    }

}
