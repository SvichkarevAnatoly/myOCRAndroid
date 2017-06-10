package ru.myocr.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import nl.littlerobots.cupboard.tools.provider.UriHelper;
import ru.myocr.App;
import ru.myocr.db.ReceiptContentProvider;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class City extends DbModel<City> {

    @SerializedName("name")
    private String name;

    public City() {
    }

    public City(long id, String name) {
        this._id = id;
        this.name = name;
    }

    public static List<City> getCities() {
        return getProviderCompartment().query(UriHelper.with(ReceiptContentProvider.AUTHORITY)
                .getUri(City.class), City.class).list();
    }

    public void putIfNotExist() {
        City city = cupboard().withContext(App.getContext())
                .query(getTableUri(), getEntityClass())
                .withSelection("_id = ?", String.valueOf(_id)).get();
        if (city != null) {
            this._id = city._id;
        }
        getProviderCompartment().put(getTableUri(), this);
    }

    public String getName() {
        return name;
    }

    @Override
    protected Class<City> getEntityClass() {
        return City.class;
    }
}
