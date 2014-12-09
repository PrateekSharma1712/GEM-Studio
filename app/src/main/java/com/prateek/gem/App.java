package com.prateek.gem;

import android.app.Application;

/**
 * Created by prateek on 22/11/14.
 */
public class App extends Application {

    private static App instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static synchronized App getInstance() {
        return instance;
    }
}
