package com.example.lameater;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class MeatButton extends android.support.v7.widget.AppCompatButton {

    public static final String CATEGORY_ID = "CATEGORY_ID";
    public static final String MEAT_ID = "MEAT_ID";
    private int cid;
    private int mid;

    public MeatButton(Context c, int cid, String name) {
        super(c);
        setText(name);
        this.cid = cid;
        this.mid = mid;

        setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchSelectionActivity();
            }
        });
    }

    public void launchSelectionActivity() {
        Context context = this.getContext();
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(CATEGORY_ID, cid);
        intent.putExtra(MEAT_ID, mid);
        context.startActivity(intent);
    }

}
