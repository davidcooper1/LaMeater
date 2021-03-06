package com.example.lameater;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.locks.ReentrantLock;

public class MeaterData {

    private static MeaterData instance;
    private final TemperatureFetcher fetcher;
    private final DatabaseHelper dbHelp;
    private final SQLiteDatabase db;

    private boolean meatSelected;
    private String meatName;
    private int targetTemp;

    private ReentrantLock selectedLock;

    private MeaterData() {
        fetcher = new TemperatureFetcher(MeatApp.getAppContext());
        Context context = MeatApp.getAppContext();
        dbHelp = new DatabaseHelper(context);
        db = dbHelp.getReadableDatabase();
        selectedLock = new ReentrantLock();

        meatSelected = false;
    }

    public static MeaterData getInstance() {
        if (instance == null)
            instance = new MeaterData();
        return instance;
    }

    public boolean isMeatSelected() {
        boolean selected;
        selectedLock.lock();

        try {
            selected = meatSelected;
        } finally {
            selectedLock.unlock();
        }

        return selected;
    }

    public void setMeatSelected(boolean selected) {
        selectedLock.lock();

        try {
            meatSelected = selected;
        } finally {
            selectedLock.unlock();
        }
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
