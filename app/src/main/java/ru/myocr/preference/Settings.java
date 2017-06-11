package ru.myocr.preference;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.myocr.App;

public class Settings {
    private static final String CITY = "city";

    private static final SharedPreferences defaultPreferences =
            PreferenceManager.getDefaultSharedPreferences(App.getContext());

    public static boolean hasSelectedCity() {
        return defaultPreferences.contains(CITY);
    }

    public static long getCityId() {
        return Long.valueOf(getString(CITY));
    }

    public static void setCityId(long cityId) {
        setString(CITY, String.valueOf(cityId));
    }

    private static void setString(String key, String value) {
        defaultPreferences.edit().putString(key, value).apply();
    }

    private static String getString(String key) {
        return defaultPreferences.getString(key, null);
    }
}
