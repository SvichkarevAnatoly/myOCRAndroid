package ru.myocr;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    public static final String FILE_PROVIDER_AUTHORITY = "ru.myocr.fileprovider";

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

}
