package com.example.lameater;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends PermissionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        setCallbacks();
        obtainPermissions();
    }

    public void openMeatChoices(View view) {
        MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
        startActivity(new Intent(this, MeatSelectionActivity.class));
    }

    public void setCallbacks() {
        final TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();
        final TextView tempOverview = findViewById(R.id.tempOverview);

        fetcher.setCallback(fetcher.CALLBACK_DATA_RECEIVED, new Runnable() {
            public void run() {
                tempOverview.post(new Runnable() {
                    public void run() {
                        double temp = Double.parseDouble(fetcher.getData());
                        temp = Math.floor(temp);
                        tempOverview.setText((int)temp + "° / --°");
                    }
                });
            }
        });

        fetcher.setCallback(fetcher.CALLBACK_DISCONNECT, new Runnable() {
            public void run() {
                tempOverview.post(new Runnable() {
                    public void run() {
                        tempOverview.setText("--° / --°");
                    }
                });
                fetcher.connect();
            }
        });

        fetcher.setCallback(fetcher.CALLBACK_DEVICE_NOT_FOUND, new Runnable(){
            //Runs the alert dialog pop-up when LaMeater device is not found.
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        //Creates the Alert itself
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Unable to Find LaMeater Device")
                                .setMessage("Make sure device is turned on then press retry.")
                                //Confirmation button. If pressed, device will attempt to reconnect to LaMeater.
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        fetcher.connect();
                                    }
                                }).show(); //funtion to actually create the alert
                    }
                });
            }
        });


        fetcher.setCallbacksEnabled(true);
    }

    public void onPermissionGranted(int requestCode) {
        TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();
        TextView tempOverview = findViewById(R.id.tempOverview);

        if (fetcher.getStatus() == fetcher.STATUS_DISCONNECTED) {
            fetcher.connect();
        } else if (fetcher.getStatus() == fetcher.STATUS_CONNECTED) {
            double temp = Double.parseDouble(fetcher.getData());
            temp = Math.floor(temp);
            tempOverview.setText((int)temp + "° / --°");
        }

    }

}
