package com.example.lameater;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class CategorySelectionActivity extends PermissionActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        SQLiteDatabase db = MeaterData.getInstance().getDatabase();
        Cursor res = db.rawQuery("SELECT DISTINCT C.cid, C.name FROM Categories C, Meats M WHERE C.cid = M.cid", null);

        ScrollView scroll = findViewById(R.id.scroll);
        LinearLayout linear_layout = scroll.findViewById(R.id.linear_layout);
        linear_layout.removeAllViews();


        for (int i = 0; i < res.getCount(); i++) {
            // Row controller: res.moveToPosition()
            // To get cid: res.getInt(0)
            // To get name: res.getString(1)
            res.moveToPosition(i);
            CategoryButton cbutton = new CategoryButton(this, (res.getInt(0)), (res.getString(1)));

            cbutton.setTextColor(0xFFFFFFFF);
            //cbutton.setText("TextView " + String.valueOf(i));
            cbutton.setBackgroundResource(R.drawable.button_oval);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(20,20,20,20);
            cbutton.setLayoutParams(params);

            linear_layout.addView(cbutton);
        }
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