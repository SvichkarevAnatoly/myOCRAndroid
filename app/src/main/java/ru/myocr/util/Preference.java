package ru.myocr.util;

import android.content.SharedPreferences;

import ru.myocr.App;

public class Preference {

    public static final String SERVER = "SERVER";
    public static final String CITY = "CITY";
    public static final String SHOP = "SHOP";

    private static final String APP_PREFS = "APP_PREFS";

    private static SharedPreferences sharedPreferences = App.getContext().getSharedPreferences(APP_PREFS, 0);

    public static void setString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public static String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }
}
