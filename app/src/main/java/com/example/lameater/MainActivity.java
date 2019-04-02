package com.example.lameater;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends PermissionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
