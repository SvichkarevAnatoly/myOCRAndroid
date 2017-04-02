package ru.myocr.preference;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.myocr.App;

import static ru.myocr.R.string.localhost;
import static ru.myocr.R.string.remote;

public class Server {

    private static final String SERVER_REMOTE_KEY = "url_remote";
    private static final String SERVER_LOCAL_KEY = "url_localhost";

    private static final String SERVER_SWITCH_KEY = "use_localhost";

    public static String getUrl() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        final boolean isLocal = preferences.getBoolean(SERVER_SWITCH_KEY, false);
        if (isLocal) {
            return preferences.getString(SERVER_LOCAL_KEY, App.getContext().getString(localhost));
        } else {
            return preferences.getString(SERVER_REMOTE_KEY, App.getContext().getString(remote));
        }
    }
}
