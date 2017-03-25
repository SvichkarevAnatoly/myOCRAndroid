package ru.myocr.preference;


import ru.myocr.App;

import static ru.myocr.R.string.localhost;
import static ru.myocr.R.string.remote;
import static ru.myocr.preference.Preference.SERVER;

public class Server {

    private static String localhostUrl = App.getContext().getString(localhost);


    static {
        Preference.setString(SERVER, localhostUrl);
    }

    public static boolean isLocal() {
        return getUrl().equals(localhostUrl);
    }

    public static String getUrl() {
        return Preference.getString(SERVER, localhostUrl);
    }

    public static void change() {
        final int newUrl = isLocal() ? remote : localhost;
        Preference.setString(SERVER, App.getContext().getString(newUrl));
    }
}
