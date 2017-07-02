package ru.myocr;

import android.app.Application;
import android.content.Context;

import ru.myocr.di.DaggerGraphComponent;

public class App extends Application {

    public static final String FILE_PROVIDER_AUTHORITY = "ru.myocr.fileprovider";

    private static App instance;
    private static DaggerGraphComponent graph;

    public static App getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static DaggerGraphComponent component() {
        return graph;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        buildComponentGraph();
    }

    private void buildComponentGraph() {
        graph = DaggerGraphComponent.Initializer.init(instance);
    }
}
