package ru.myocr.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {
    Application app;

    public MainModule(Application app) {
        this.app = app;
    }

    @Provides
    @Singleton
    protected Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    HelloWorldService provideHelloWorldService() {
        return new HelloWorldServiceManager();
    }
}
