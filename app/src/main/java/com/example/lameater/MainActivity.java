package com.example.lameater;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
    }

    public void openMeatChoices(View view) {
        MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
        startActivity(new Intent(this, CategorySelectionActivity.class));
    }

    public void setCallbacks() {
        final TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();
        final TextView tempOverview = findViewById(R.id.tempOverview);

        fetcher.setCallback(fetcher.CALLBACK_DATA_RECEIVED, new Runnable() {
            public void run() {
                tempOverview.post(new Runnable() {
                    public void run() {
                        MeaterData data = MeaterData.getInstance();
                        int temp = (int)Double.parseDouble(fetcher.getData());
                        if (data.isMeatSelected())
                            tempOverview.setText(getString(R.string.temp_selected, temp, data.getTargetTemp()));
                        else
                            tempOverview.setText(getString(R.string.temp, temp));
                    }
                });
            }
        });

        fetcher.setCallback(fetcher.CALLBACK_DISCONNECT, new Runnable() {
            public void run() {
                tempOverview.post(new Runnable() {
                    public void run() {
                        MeaterData data = MeaterData.getInstance();
                        tempOverview.setText("--");
                    }
                });
                fetcher.connect();
            }
        });
    }

}
