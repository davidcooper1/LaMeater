package com.example.lameater;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MeatSelectionActivity extends AppCompatActivity{
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meat_selection);
    }

    public void MeatSelected(View view){
        switch(view.getId())
        {
            case R.id.SelBtnBeef:
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
                startActivity((new Intent( this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
                break;
            default:
                throw new RuntimeException("Unknown button ID");


        }


    }
}
