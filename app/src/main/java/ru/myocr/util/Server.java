package ru.myocr.util;


import ru.myocr.App;
import ru.myocr.model.R;

public class Server {
    public static boolean isLocal() {
        return Preference.getCurrentServerUrl().equals(App.getAppContext().getString(R.string.localhost));
    }
}
