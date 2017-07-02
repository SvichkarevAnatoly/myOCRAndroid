package ru.myocr.viewmodel;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import ru.myocr.api.ApiHelper;
import ru.myocr.model.City;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShopAddingViewModel extends ViewModel {

    private MutableLiveData<List<City>> cities = new MutableLiveData<>();
    private MutableLiveData<Boolean> progress = new MutableLiveData<>();

    private Subscription subscription;
    private DataSource<List<City>> shopDataSource;

    public ShopAddingViewModel(DataSource<List<City>> shopDataSource) {
        this.shopDataSource = shopDataSource;
        loadCities();
    }

    public void addCity(String string, int selectedItemPosition) {
        progress.setValue(true);
        ApiHelper.makeApiRequest(null,
                (requester, var) -> requester.addShop(),
                throwable -> {
                },
                status -> {
                    progress.setValue(false);
                },
                null);
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<List<City>> getCities() {
        return cities;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (null != subscription) {
            subscription.unsubscribe();
        }
    }

    private void loadCities() {
        subscription = shopDataSource.get()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadedCities -> cities.setValue(loadedCities),
                        Throwable::printStackTrace);

    }
}
