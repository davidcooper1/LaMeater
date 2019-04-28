package com.example.lameater;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

// This class is used as a wrapper for activities.

public abstract class PermissionActivity extends AppCompatActivity {

    protected final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    protected final int REQUEST_ENABLE_BT = 2;
    protected final int REQUEST_PERMISSION_SETTING = 4;

    private boolean receiverAdded = false;


    protected void obtainPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_COARSE_LOCATION
            );
        } else {
            TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();
            onPermissionGranted(PERMISSION_REQUEST_COARSE_LOCATION);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();
                    onPermissionGranted(requestCode);
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    onPermissionDenied(requestCode);
                }
                break;
        }
    }

    protected void onPermissionGranted(int requestCode) {
        MeaterData.getInstance().getFetcher().connect();
    }

    protected void onPermissionDenied(int requestCode) {
        Log.d("ALERT", "Alert made.");
        if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                boolean shouldShowRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION);
                if (!shouldShowRationale) {
                    new AlertDialog.Builder(this)
                            .setTitle("Need Location Permission")
                            .setMessage("To enable Bluetooth discovery, this app must have location permissions. If connecting manually, the PIN is 1234.")
                            .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                }
                            })
                            .setNegativeButton("Manually Connect", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                }
                            })
                            .show();
                } else {
                    showRationale(requestCode);
                }
            } else {
                showRationale(requestCode);
            }
        }
    }

    protected void showRationale(int requestCode) {
        if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION) {
            new AlertDialog.Builder(this)
                    .setTitle("Need Location Permission")
                    .setMessage("To enable Bluetooth discovery, this app must have location permissions.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            obtainPermissions();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            obtainPermissions();
                        }
                    })
                    .show();
        }
    }

    protected void setCallbacks() { }

    protected void onStart() {
        super.onStart();
        if (BluetoothAdapter.getDefaultAdapter().isDiscovering())
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        final TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();

        fetcher.setCallback(TemperatureFetcher.CALLBACK_BLUETOOTH_DISABLED, new Runnable() {
            public void run() {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        });

        fetcher.setCallback(TemperatureFetcher.CALLBACK_DEVICE_NOT_FOUND, new Runnable(){
            //Runs the alert dialog pop-up when LaMeater device is not found.
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("DATA", "Device not found");
                        //Creates the Alert itself
                        new AlertDialog.Builder(PermissionActivity.this)
                                .setTitle("Unable to Find LaMeater Device")
                                .setMessage("Make sure the device is turned on then press retry.")
                                //Confirmation button. If pressed, device will attempt to reconnect to LaMeater.
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        fetcher.connect();
                                    }
                                })
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    public void onCancel(DialogInterface dialog) {
                                        fetcher.connect();
                                    }
                                })
                                .show(); // Create the alert
                    }
                });
            }
        });

        fetcher.setCallback(TemperatureFetcher.CALLBACK_NO_PERMISSION, new Runnable() {
            public void run() {
                obtainPermissions();
            }
        });

        setCallbacks();

        int status = fetcher.getStatus();
        if (status == TemperatureFetcher.STATUS_DISCONNECTED) {
            if (BluetoothAdapter.getDefaultAdapter().isDiscovering())
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            fetcher.setCallbacksEnabled(true);
            fetcher.connect();
        } else {
            fetcher.setCallbacksEnabled(true);
        }
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        Log.d("DATA", "Receiver added");
        return super.registerReceiver(receiver, filter);
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
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                fetcher.connect();
                            }
                        })
                        .show();
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
