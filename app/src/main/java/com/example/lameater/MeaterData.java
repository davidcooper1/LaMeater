package com.example.lameater;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class MeaterData {

    private static MeaterData instance;
    private final TemperatureFetcher fetcher;
    private final DatabaseHelper dbHelp;
    private final SQLiteDatabase db;

    private boolean meatSelected;
    private String meatName;
    private int targetTemp;

    private MeaterData() {
        fetcher = new TemperatureFetcher(MeatApp.getAppContext());
        Context context = MeatApp.getAppContext();
        dbHelp = new DatabaseHelper(context);
        db = dbHelp.getReadableDatabase();

        meatSelected = false;
    }

    public static MeaterData getInstance() {
        if (instance == null)
            instance = new MeaterData();
        return instance;
    }

    public boolean isMeatSelected() {
        return meatSelected;
    }

    public void setMeatSelected(boolean selected) {
        meatSelected = selected;
    }

    public String getMeatName() {
        return meatName;
    }

    public void setMeatName(String name) {
        meatName = name;
    }

    public int getTargetTemp() {
        return targetTemp;
    }

    public void setTargetTemp(int temp) {
        targetTemp = temp;
    }

    public TemperatureFetcher getFetcher() {
        return fetcher;
    }

    public SQLiteDatabase getDatabase() {
        return db;
    }

}
