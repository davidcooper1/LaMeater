package com.example.lameater;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class MeaterData {

    private static MeaterData instance;
    private final TemperatureFetcher fetcher;
    private final DatabaseHelper dbHelp;
    private final SQLiteDatabase db;

    private MeaterData() {
        fetcher = new TemperatureFetcher(MeatApp.getAppContext());
        Context context = MeatApp.getAppContext();
        dbHelp = new DatabaseHelper(context);
        db = dbHelp.getReadableDatabase();
    }

    public static MeaterData getInstance() {
        if (instance == null)
            instance = new MeaterData();
        return instance;
    }

    public TemperatureFetcher getFetcher() {
        return fetcher;
    }

    public SQLiteDatabase getDatabase() {
        return db;
    }

}
