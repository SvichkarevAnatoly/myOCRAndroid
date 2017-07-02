package ru.myocr.activity;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import java.util.List;

import ru.myocr.model.City;
import ru.myocr.viewmodel.DataSource;
import ru.myocr.viewmodel.ShopAddingViewModel;

public class ShopAddingViewModelFactory implements ViewModelProvider.Factory {

    private DataSource<List<City>> dataSource;

    public ShopAddingViewModelFactory(DataSource<List<City>> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ShopAddingViewModel(dataSource);
    }
}
