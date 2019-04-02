package com.example.lameater;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class BeefOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beef_options);
    }

    public void TypeSelected(View view) {
        switch (view.getId()) {
            case R.id.SelBtnSteak:
                //startActivity(new Intent(this, BeefOptionsActivity.class));
                break;
            case R.id.SelBtnBrisket:
                //startActivity(new Intent(this, PoultryOptionsActivity.class));
                break;
            case R.id.SelBtnRibs:
                //startActivity(new Intent(this, PorkOptionsActivity.class));
                break;
            case R.id.SelBtnGround:
                //startActivity(new Intent(this, FishOptionsActivity.class));
                break;
            case R.id.SelBtnSausage:
                //startActivity(new Intent(this, OtherOptionsActivity.class));
                break;
            case R.id.CurTempHomeBtn:
                startActivity((new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
                break;
            default:
                throw new RuntimeException("Unknown button ID");


        }
    }
}
