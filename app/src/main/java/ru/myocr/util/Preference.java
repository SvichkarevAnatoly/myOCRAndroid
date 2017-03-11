package ru.myocr.util;

import android.content.SharedPreferences;

import ru.myocr.App;
import ru.myocr.model.R;

public class Preference {

    public static final String SERVER_URL = "SERVER_URL";
    public static final String CITY = "CITY";
    public static final String SHOP = "SHOP";

    private static final String APP_PREFS = "APP_PREFS";

    private static SharedPreferences sharedPreferences = App.getAppContext().getSharedPreferences(APP_PREFS, 0);

    public static String getCurrentServerUrl() {
        return sharedPreferences.getString(SERVER_URL, App.getAppContext().getString(R.string.localhost));
    }

    public static void setString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return sharedPreferences.getString(key, null);
    }
}
