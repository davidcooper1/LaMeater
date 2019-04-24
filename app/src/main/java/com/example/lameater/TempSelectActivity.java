package com.example.lameater;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TempSelectActivity extends PermissionActivity {
    public static final String EXTRA_MESSAGE = "com.example.lameater.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_select);

        int mid = getIntent().getIntExtra(MeatButton.MEAT_ID, -1);
        int cid = getIntent().getIntExtra(MeatButton.CATEGORY_ID, -1);
        String meatName = getIntent().getStringExtra(MeatButton.MEAT_NAME);
        String categoryName = getIntent().getStringExtra(MeatButton.CATEGORY_NAME);

        SQLiteDatabase db = MeaterData.getInstance().getDatabase();
        Cursor res = db.rawQuery("SELECT * FROM Meats M WHERE cid = ?", new String[] {mid + ""});


        Button category_title = findViewById(R.id.categoryTitle);
        category_title.setText(categoryName);

        Button meat_title = findViewById(R.id.meatTitle);
        meat_title.setText(meatName);

        category_title.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
                startActivity(new Intent(TempSelectActivity.this, CategorySelectionActivity.class));
            }
        });

        meat_title.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                //MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
                //startActivity(new Intent(TempSelectActivity.this, MeatSelectionActivity.class));
             onBackPressed();
            }
        });

        Button current_temp = findViewById(R.id.CurTempHomeBtn);
        current_temp.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
                startActivity(new Intent(TempSelectActivity.this, MainActivity.class));
            }
        });


    }


    protected void setCallbacks() {
        final TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();

        fetcher.setCallback(fetcher.CALLBACK_DATA_RECEIVED, new Runnable() {
            public void run() {

            }
        });

        fetcher.setCallback(fetcher.CALLBACK_DISCONNECT, new Runnable() {
            public void run() {

            }
        });
    }
}
