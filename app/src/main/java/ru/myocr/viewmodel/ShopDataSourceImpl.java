package ru.myocr.viewmodel;


import java.util.List;

import ru.myocr.api.ApiHelper;
import ru.myocr.model.City;


public class ShopDataSourceImpl extends DataSourceImpl<List<City>> {
    public ShopDataSourceImpl() {
        super(() -> ApiHelper.getInstance().getAllCities(null));
    }
}
