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
        setContentView(R.layout.activity_meat_selection);

        Button current_temp = findViewById(R.id.CurTempHomeBtn);
        current_temp.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                MeaterData.getInstance().getFetcher().setCallbacksEnabled(false);
                startActivity(new Intent(TempSelectActivity.this, MainActivity.class));
            }
        });


    }
}
