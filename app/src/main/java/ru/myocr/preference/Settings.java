package ru.myocr.preference;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.myocr.App;

public class Settings {
    private static final String CITY = "city";

    private static final SharedPreferences defaultPreferences =
            PreferenceManager.getDefaultSharedPreferences(App.getContext());

    public static long getCity() {
        return Long.valueOf(getString(CITY));
    }

    public static void setCity(long city) {
        setString(CITY, String.valueOf(city));
    }

    private static void setString(String key, String value) {
        defaultPreferences.edit().putString(key, value).apply();
    }

    private static String getString(String key) {
        return defaultPreferences.getString(key, null);
    }
}
