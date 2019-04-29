package com.example.lameater;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class TempSelectionActivity extends PermissionActivity {
    public static final String EXTRA_MESSAGE = "com.example.lameater.MESSAGE";

    private int wellDoneTemp;

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

        Button meatTitle = findViewById(R.id.meatTitle);
        meatTitle.setText(meatName);

        Button category_title = findViewById(R.id.categoryTitle);
        category_title.setText(categoryName);

        category_title.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
                startActivity(new Intent(TempSelectionActivity.this, CategorySelectionActivity.class));
            }
        });

        TextView meatDesc = findViewById(R.id.meatDescription);
        meatDesc.setText(description);

        Spinner doneness = findViewById(R.id.spinner);


        ArrayAdapter<TemperatureOption> temps = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        final TextView recommended = findViewById(R.id.recmndTemp);
        final EditText target = findViewById(R.id.targetTemp);

        for (int i = 0; i < res.getCount(); i++) {
            res.moveToPosition(i);
            int tid = res.getInt(2);
            String name = res.getString(3);
            int temp = res.getInt(4);

            if (tid == 1) {
                wellDoneTemp = temp;
            }
            temps.add(new TemperatureOption(name, temp));
        }

        doneness.setAdapter(temps);

        doneness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
                TemperatureOption option = (TemperatureOption)adapter.getItemAtPosition(position);

                recommended.setText(option.getTemp() + " °F");
                target.setText("" + option.getTemp());
            }

            public void onNothingSelected(AdapterView<?> adapter) {

            }
        });

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

    public void tempSelect(View view) {
        EditText temp = findViewById(R.id.targetTemp);
        String input = temp.getText().toString();

        if (!input.equals("")) {
            final int userTemp = Integer.parseInt(input);
            final String meatName = ((Spinner)findViewById(R.id.spinner)).getSelectedItem().toString();
            if (userTemp < wellDoneTemp) {
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Temperature")
                        .setMessage("Consuming undercooked meats can increase chance of foodbourne illness. Do you accept this risk?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                loadMainWithSelection(meatName, userTemp);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

            } else {
                loadMainWithSelection(meatName, userTemp);
            }
        }
    }

    public void loadMainWithSelection(String meatName, int userTemp) {
        MeaterData data = MeaterData.getInstance();
        data.setMeatName(meatName);
        data.setTargetTemp(userTemp);
        data.setMeatSelected(true);
        data.getFetcher().setCallbacksEnabled(false);
        startActivity(new Intent(TempSelectionActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

}
