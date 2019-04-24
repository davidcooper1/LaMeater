package com.example.lameater;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TempSelectActivity extends PermissionActivity {
    public static final String EXTRA_MESSAGE = "com.example.lameater.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_select);

        Button category_title = findViewById(R.id.categoryTitle);
        category_title.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
                startActivity(new Intent(TempSelectActivity.this, CategorySelectionActivity.class));
            }
        });

        Button meat_title = findViewById(R.id.CurTempHomeBtn);
        meat_title.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
                startActivity(new Intent(TempSelectActivity.this, MeatSelectionActivity.class));
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
}
