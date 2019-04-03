package com.example.lameater;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BeefOptionsActivity extends PermissionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beef_options);
    }

    public void TypeSelected(View view) {
        switch (view.getId()) {
            case R.id.SelBtnBreast:
                startActivity(new Intent(this, SteakTempActivity.class));
                break;
            case R.id.SelBtnThigh:
                //startActivity(new Intent(this, PoultryOptionsActivity.class));
                break;
            case R.id.SelBtnWing:
                //startActivity(new Intent(this, PorkOptionsActivity.class));
                break;
            case R.id.SelBtnWholes:
                //startActivity(new Intent(this, FishOptionsActivity.class));
                break;
            case R.id.SelBtnSausage:
                //startActivity(new Intent(this, OtherOptionsActivity.class));
                break;
            case R.id.CurTempHomeBtn:
                startActivity((new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
                break;
            default:
                throw new RuntimeException("Unknown button ID");


        }
    }

    protected void setCallbacks() {
        final TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();
        final Button tempOverview = findViewById(R.id.CurTempHomeBtn);

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
            }
        });

        fetcher.setCallbacksEnabled(true);


    }

    public void onPermissionGranted(int requestCode) {
        TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();
        Button tempOverview = findViewById(R.id.CurTempHomeBtn);

        if (fetcher.getStatus() == fetcher.STATUS_DISCONNECTED) {
            fetcher.connect();
        } else if (fetcher.getStatus() == fetcher.STATUS_CONNECTED) {
            double temp = Double.parseDouble(fetcher.getData());
            temp = Math.floor(temp);
            tempOverview.setText((int)temp + "° / --°");
        }
    }

}
