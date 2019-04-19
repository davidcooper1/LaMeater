package com.example.lameater;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MeatSelectionActivity extends PermissionActivity {
    public static final String EXTRA_MESSAGE = "com.example.lameater.MESSAGE";
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

    public void MeatSelected(View view) {

        /*switch(view.getId()) {
            case R.id.SelBtnBeef:
                MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
                startActivity(new Intent(this, BeefOptionsActivity.class));
                break;
            case R.id.SelBtnPoultry:
                //startActivity(new Intent(this, PoultryOptionsActivity.class));
                break;
            case R.id.SelBtnPork:
                //startActivity(new Intent(this, PorkOptionsActivity.class));
                break;
            case R.id.SelBtnFish:
                //startActivity(new Intent(this, FishOptionsActivity.class));
                break;
            case R.id.SelBtnOther:
                //startActivity(new Intent(this, OtherOptionsActivity.class));
                break;
            case R.id.CustTempSetBtn:
                //code
                break;
            case R.id.CurTempHomeBtn:
                MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
                startActivity((new Intent( this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
                break;
            default:
                throw new RuntimeException("Unknown button ID");


        }
        */
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
