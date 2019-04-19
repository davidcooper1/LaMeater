package com.example.lameater;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.AppCompatButton;

public class CategoryButton extends AppCompatButton {

    public static final String CATEGORY_ID = "CATEGORY_ID";
    public static final String CATEGORY_NAME = "CATEGORY_NAME";
    private int cid;
    private String name;

    public CategoryButton(Context c, int cid, String name) {
        super(c);
        this.cid = cid;
        this.name = name;
        setText(name);

        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                launchSelectionActivity();
            }
        });
    }

    public void launchSelectionActivity() {
        Context context = this.getContext();
        Intent intent = new Intent(context, MeatSelectionActivity.class);
        intent.putExtra(CATEGORY_ID, cid);
        intent.putExtra(CATEGORY_NAME, name);
        context.startActivity(intent);
    }

}
