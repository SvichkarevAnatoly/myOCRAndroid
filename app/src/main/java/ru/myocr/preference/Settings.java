package ru.myocr.preference;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.myocr.App;

public class Settings {
    private static final String CITY = "city";

    private static final SharedPreferences defaultPreferences =
            PreferenceManager.getDefaultSharedPreferences(App.getContext());

    public static String getCity() {
        return getString(CITY);
    }

    public static void setCity(String city) {
        setString(CITY, city);
    }

    private static void setString(String key, String value) {
        defaultPreferences.edit().putString(key, value).apply();
    }

    private static String getString(String key) {
        return defaultPreferences.getString(key, null);
    }
}
