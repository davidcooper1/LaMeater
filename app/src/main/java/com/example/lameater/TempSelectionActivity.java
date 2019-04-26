package com.example.lameater;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class TempSelectionActivity extends PermissionActivity {
    public static final String EXTRA_MESSAGE = "com.example.lameater.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_select);

        Intent intent = getIntent();
        int mid = intent.getIntExtra(MeatButton.MEAT_ID, -1);
        int cid = intent.getIntExtra(MeatButton.CATEGORY_ID, -1);
        String meatName = intent.getStringExtra(MeatButton.MEAT_NAME);
        String categoryName = intent.getStringExtra(MeatButton.CATEGORY_NAME);
        String description = intent.getStringExtra(MeatButton.MEAT_DESCRIPTION);

        SQLiteDatabase db = MeaterData.getInstance().getDatabase();
        Cursor res = db.rawQuery("SELECT * FROM CookTemps WHERE cid = ? AND mid = ? ORDER BY tid ASC;", new String[] {cid + "", mid + ""});


        Button category_title = findViewById(R.id.categoryTitle);
        category_title.setText(categoryName);

        Button meat_title = findViewById(R.id.meatTitle);
        meat_title.setText(meatName);

        category_title.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
                startActivity(new Intent(TempSelectionActivity.this, CategorySelectionActivity.class));
            }
        });

        meat_title.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
             onBackPressed();
            }
        });

        Button current_temp = findViewById(R.id.CurTempHomeBtn);
        current_temp.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
                startActivity(new Intent(TempSelectionActivity.this, MainActivity.class));
            }
        });

        TextView meatDesc = findViewById(R.id.meatDescription);
        meatDesc.setText(description);

        Spinner doneness = findViewById(R.id.spinner);


        ArrayAdapter<TemperatureOption> temps = new ArrayAdapter<TemperatureOption>(this, android.R.layout.simple_list_item_1);

        for (int i = 0; i < res.getCount(); i++) {
            res.moveToPosition(i);




        }

            String tempName = res.getString(3);
            int recTemp = res.getInt(4);


            TextView recommended = findViewById(R.id.recmndTemp);
            recommended.setText(recTemp + " °F");

            TextView target = findViewById(R.id.targetTemp);
            target.setHint(recTemp + "");


        res.close();
    }




    protected void setCallbacks() {
        final TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();
        final Button tempOverview = findViewById(R.id.CurTempHomeBtn);

        fetcher.setCallback(fetcher.CALLBACK_DATA_RECEIVED, new Runnable() {
            public void run() {
                tempOverview.post(new Runnable() {
                    public void run() {
                        int temp = (int)Double.parseDouble(fetcher.getData());
                        tempOverview.setText(getString(R.string.temp, temp));
                    }
                });
            }
        });

        fetcher.setCallback(fetcher.CALLBACK_DISCONNECT, new Runnable() {
            public void run() {
                tempOverview.post(new Runnable() {
                    public void run() {
                        tempOverview.setText("--");
                    }
                });
                fetcher.connect();
            }
        });

    }
}
