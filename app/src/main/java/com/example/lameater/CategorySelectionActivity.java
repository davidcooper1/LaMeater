package com.example.lameater;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class CategorySelectionActivity extends PermissionActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        SQLiteDatabase db = MeaterData.getInstance().getDatabase();
        Cursor res = db.rawQuery("SELECT DISTINCT C.cid, C.name FROM Categories C, Meats M WHERE C.cid = M.cid", null);

        Button current_temp = findViewById(R.id.CurTempHomeBtn);
        current_temp.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
                startActivity(new Intent(CategorySelectionActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

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

    public void useCustomTemp(View view) {
        EditText temp = findViewById(R.id.customTemp);
        String input = temp.getText().toString();

        MeaterData data = MeaterData.getInstance();

        if (!input.equals("")) {
            data.setMeatName("Custom");
            data.setTargetTemp(Integer.parseInt(input));
            data.setMeatSelected(true);

            startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

}