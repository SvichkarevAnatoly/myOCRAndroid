package ru.myocr.preference;

import android.content.SharedPreferences;

import ru.myocr.App;

public class Preference {
    private static final String SHOP = "SHOP";

    private static final String APP_PREFS = "APP_PREFS";
    private static SharedPreferences sharedPreferences = App.getContext().getSharedPreferences(APP_PREFS, 0);

    public static String getShop() {
        return getString(SHOP);
    }

    public static void setShop(String shop) {
        setString(SHOP, shop);
    }

    private static void setString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    private static String getString(String key) {
        return sharedPreferences.getString(key, null);
    }
}
