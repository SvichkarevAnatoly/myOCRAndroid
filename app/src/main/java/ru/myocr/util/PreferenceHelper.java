package ru.myocr.util;

import android.content.SharedPreferences;

import ru.myocr.OcrApplication;
import ru.myocr.model.R;

public class PreferenceHelper {

    public static final String APP_PREFS = "APP_PREFS";
    public static final String KEY_SERVER_URL = "KEY_SERVER_URL";
    public static final String KEY_CITY = "KEY_CITY";

    public static String getCurrentServerUrl() {
        SharedPreferences sharedPreferences = OcrApplication.getAppContext().getSharedPreferences(APP_PREFS, 0);
        return sharedPreferences.getString(KEY_SERVER_URL, OcrApplication.getAppContext().getString(R.string.localhost));
    }

    public static void setString(String key, String value) {
        SharedPreferences sharedPreferences = OcrApplication.getAppContext().getSharedPreferences(APP_PREFS, 0);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        SharedPreferences sharedPreferences = OcrApplication.getAppContext().getSharedPreferences(APP_PREFS, 0);
        return sharedPreferences.getString(key, null);
    }
}
