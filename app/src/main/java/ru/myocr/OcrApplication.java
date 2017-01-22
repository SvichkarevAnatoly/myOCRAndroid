package ru.myocr;

import android.app.Application;
import android.content.Context;

public class OcrApplication extends Application {

    private static OcrApplication instance;

    public static OcrApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

}
