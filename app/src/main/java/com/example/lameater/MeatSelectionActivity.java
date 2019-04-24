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
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MeatSelectionActivity extends PermissionActivity {
    public static final String EXTRA_MESSAGE = "com.example.lameater.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meat_selection);

        int cid = getIntent().getIntExtra(CategoryButton.CATEGORY_ID, -1);
        String categoryName = getIntent().getStringExtra(CategoryButton.CATEGORY_NAME);

        SQLiteDatabase db = MeaterData.getInstance().getDatabase();
        Cursor res = db.rawQuery("SELECT * FROM Meats M WHERE cid = ?", new String[] {cid + ""});

        //Changes button at the top of the activity to the category name, and adds formatting
        Button category_selected = findViewById(R.id.category_selected);
        category_selected.setText(categoryName);
        category_selected.setTextColor(0xFFFFFFFF);
        category_selected.setTextSize(21);
        category_selected.setBackgroundResource(R.drawable.text_view_oval);

        category_selected.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
                startActivity(new Intent(MeatSelectionActivity.this, CategorySelectionActivity.class));
            }
        });

        Button current_temp = findViewById(R.id.CurTempHomeBtn);
        current_temp.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
                startActivity(new Intent(MeatSelectionActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        ScrollView meat_scroll = findViewById(R.id.meat_scroll);
        LinearLayout linearlayout = meat_scroll.findViewById(R.id.linearlayout);
        linearlayout.removeAllViews();

        for (int i = 0; i < res.getCount(); i++) {
            // Row controller: res.moveToPosition(i)
            // To get cid: res.getInt(0)
            // To get name: res.getString(2)
            // To get mid: res.getInt(1)
            // To get description: res.getString(3)
            res.moveToPosition(i);
            MeatButton btn = new MeatButton(this, cid, categoryName, res.getInt(1), res.getString(2));

            btn.setTextColor(0xFFFFFFFF);
            //cbutton.setText("TextView " + String.valueOf(i));
            btn.setBackgroundResource(R.drawable.button_oval);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(20,20,20,20);
            btn.setLayoutParams(params);

            linearlayout.addView(btn);

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
