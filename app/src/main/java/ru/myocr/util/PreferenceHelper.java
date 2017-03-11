package ru.myocr.util;

import android.content.SharedPreferences;

import ru.myocr.App;
import ru.myocr.model.R;

public class PreferenceHelper {

    public static final String KEY_CITY = "KEY_CITY";
    public static final String APP_PREFS = "APP_PREFS";
    public static final String KEY_SERVER_URL = "KEY_SERVER_URL";

    private static SharedPreferences sharedPreferences = App.getAppContext().getSharedPreferences(APP_PREFS, 0);

    public static String getCurrentServerUrl() {
        return sharedPreferences.getString(KEY_SERVER_URL, App.getAppContext().getString(R.string.localhost));
    }

    public static void setString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return sharedPreferences.getString(key, null);
    }
}
