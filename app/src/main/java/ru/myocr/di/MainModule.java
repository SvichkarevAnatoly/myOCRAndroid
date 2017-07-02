package ru.myocr.di;

import android.app.Application;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.myocr.model.City;
import ru.myocr.viewmodel.DataSource;
import ru.myocr.viewmodel.ShopDataSourceImpl;

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
    DataSource<List<City>> provideDataSource() {
        return new ShopDataSourceImpl();
    }
}
