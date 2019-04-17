package com.example.lameater;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "meats"; //DB Name
    public static final String ASSETS_PATH="databases";
    public static final int DATABASE_VERSION = 1;

    private SharedPreferences preferences;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        preferences = context.getSharedPreferences(
                context.getPackageName() + ".database_versions",
                Context.MODE_PRIVATE
        );
    }

    private boolean installedDatabaseIsOutdated() {
        return preferences.getInt(DATABASE_NAME, 0) < DATABASE_VERSION;
    }

    private void writeDatabaseVersionInPreferences() {
        preferences.edit()
                .putInt(DATABASE_NAME, DATABASE_VERSION)
                .apply();
    }

    private void installDatabaseFromAssets() {
        try {
            InputStream inputStream = context.getAssets().open(ASSETS_PATH + "/" + DATABASE_NAME + ".db");
            File outputFile = context.getDatabasePath(DATABASE_NAME);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            copy(inputStream, outputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Log.e("ERROR", "Database couldn't be installed from assets.", e);
        }
    }

    private void installOrUpdateIfNecessary() {
        if (installedDatabaseIsOutdated()) {
            context.deleteDatabase(DATABASE_NAME);
            installDatabaseFromAssets();
            writeDatabaseVersionInPreferences();
        }
    }

    public SQLiteDatabase getWritableDatabase() {
        return null;
    }

    public SQLiteDatabase getReadableDatabase() {
        installOrUpdateIfNecessary();
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int bytesRead = in.read(buffer);
            if (bytesRead == -1)
                break;
            out.write(buffer, 0, bytesRead);
        }
    }


}
