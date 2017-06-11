package ru.myocr.preference;

import android.content.SharedPreferences;

import ru.myocr.App;

public class Preference {
    private static final String SHOP = "SHOP";

    private static final String APP_PREFS = "APP_PREFS";
    private static SharedPreferences sharedPreferences = App.getContext().getSharedPreferences(APP_PREFS, 0);

    public static long getShopId() {
        return getLong(SHOP);
    }

    public static void setShopId(long shop) {
        setLong(SHOP, shop);
    }

    private static void setLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    private static long getLong(String key) {
        return sharedPreferences.getLong(key, -1);
    }
}
