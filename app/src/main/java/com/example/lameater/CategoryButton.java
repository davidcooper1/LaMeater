package com.example.lameater;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.AppCompatButton;

public class CategoryButton extends AppCompatButton {

    public static final String CATEGORY_ID = "CATEGORY_ID";
    private int cid;

    public CategoryButton(Context c, int cid, String name) {
        super(c);
        setText(name);
        this.cid = cid;

        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                launchSelectionActivity();
            }
        });
    }

    public void launchSelectionActivity() {
        Context context = this.getContext();
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(CATEGORY_ID, cid);
        context.startActivity(intent);
    }

}
