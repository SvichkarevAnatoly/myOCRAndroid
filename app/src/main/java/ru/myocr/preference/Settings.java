package ru.myocr.preference;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.myocr.App;

public class Settings {
    public static final String CITY = "city";

    private static final SharedPreferences defaultPreferences =
            PreferenceManager.getDefaultSharedPreferences(App.getContext());

    public static void setString(String key, String value) {
        defaultPreferences.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return defaultPreferences.getString(key, null);
    }
}
