package com.example.lameater;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CategorySelection extends PermissionActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_beef_options);
        SQLiteDatabase db = MeaterData.getInstance().getDatabase();
        Cursor res = db.rawQuery("SELECT * FROM Meats M", null);

        for (int i = 0; i < res.getCount(); i++) {
            // Row controller: res.moveToPosition(i)
            // To get cid: res.getInt(i)
            // To get name: res.getString(i+2)
            // To get mid: res.getInt(i+1)
            // To get description: res.getString(i+3)
            res.moveToPosition(i);
            MeatButton mbutton = new MeatButton(this, res.getInt(i), (res.getInt(i+1)), (res.getString(i+2)));

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
                fetcher.connect();
            }
        });

        fetcher.setCallback(fetcher.CALLBACK_DEVICE_NOT_FOUND, new Runnable(){
            //Runs the alert dialog pop-up when LaMeater device is not found.
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        //Creates the Alert itself
                        new AlertDialog.Builder(CategorySelection.this)
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