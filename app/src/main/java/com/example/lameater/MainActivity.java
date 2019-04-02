package com.example.lameater;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends PermissionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        obtainPermission(PERMISSION_REQUEST_COARSE_LOCATION);
    }

    public void openMeatChoices(View view) {
        Intent intent = new Intent(this, MeatSelectionActivity.class);
        startActivity(intent);
    }

    public void onPermissionGranted(int requestCode) {
        final TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();
        final TextView tempOverview = (TextView)findViewById(R.id.tempOverview);

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
        fetcher.connect();

    }

    protected void onDestroy() {
        super.onDestroy();
        MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
    }

}
