package com.example.lameater;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

public class MeatApp extends Application {

    private static Context context;
    private int activityCount;
    ReceiverHandler handler;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        activityCount = 0;
        handler = new ReceiverHandler();
        registerActivityLifecycleCallbacks(handler);
    }

    public static Context getAppContext() {
         return context;
    }

    private class ReceiverHandler implements ActivityLifecycleCallbacks {

        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (activityCount == 0) {
                TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();
                registerReceiver(fetcher.getReceiver(), fetcher.getFilter());
            }
            activityCount++;
        }

        public void onActivityDestroyed(Activity activity) {
            activityCount--;
            if (activityCount == 0) {
                TemperatureFetcher fetcher = MeaterData.getInstance().getFetcher();
                unregisterReceiver(fetcher.getReceiver());
            }
        }

        public void onActivityPaused(Activity activity) { }

        public void onActivityResumed(Activity activity) { }

        public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }

        public void onActivityStarted(Activity activity) { }

        public void onActivityStopped(Activity activity) { }

    }

}
