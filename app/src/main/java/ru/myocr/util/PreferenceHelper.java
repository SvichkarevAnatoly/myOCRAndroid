package ru.myocr.util;

import android.content.SharedPreferences;

import ru.myocr.OcrApplication;
import ru.myocr.api.Api;

public class PreferenceHelper {

    public static final String APP_PREFS = "APP_PREFS";
    public static final String KEY_SERVER_URL = "KEY_SERVER_URL";

    public static String getCurrentServerUrl() {
        SharedPreferences sharedPreferences = OcrApplication.getAppContext().getSharedPreferences(APP_PREFS, 0);
        return sharedPreferences.getString(KEY_SERVER_URL, Api.SERVER_URL_LOCAL);
    }

    public static void setCurrentServerUrl(String url) {
        SharedPreferences sharedPreferences = OcrApplication.getAppContext().getSharedPreferences(APP_PREFS, 0);
        sharedPreferences.edit().putString(KEY_SERVER_URL, url).apply();
    }
}
