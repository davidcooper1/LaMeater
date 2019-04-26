package com.example.lameater;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class MeatButton extends android.support.v7.widget.AppCompatButton {

    public static final String CATEGORY_ID = "CATEGORY_ID";
    public static final String CATEGORY_NAME = "CATEGORY_NAME";
    public static final String MEAT_ID = "MEAT_ID";
    public static final String MEAT_NAME = "MEAT_NAME";
    public static final String MEAT_DESCRIPTION = "MEAT_DESCRIPTION";
    private int cid;
    private int mid;
    private String cname;
    private String mname;
    private String description;

    public MeatButton(Context c, int cid, String cname, int mid, String mname, String description) {
        super(c);
        this.cid = cid;
        this.cname = cname;
        this.mid = mid;
        this.mname = mname;
        this.description = description;
        setText(mname);

        setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchSelectionActivity();
            }
        });
    }

    public void launchSelectionActivity() {
        Context context = this.getContext();
        Intent intent = new Intent(context, TempSelectionActivity.class);
        intent.putExtra(CATEGORY_ID, cid);
        intent.putExtra(CATEGORY_NAME, cname);
        intent.putExtra(MEAT_ID, mid);
        intent.putExtra(MEAT_NAME, mname);
        intent.putExtra(MEAT_DESCRIPTION, description);
        context.startActivity(intent);
    }

}
