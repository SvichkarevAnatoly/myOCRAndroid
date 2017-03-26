package ru.myocr.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import nl.littlerobots.cupboard.tools.provider.UriHelper;
import ru.myocr.App;
import ru.myocr.db.ReceiptContentProvider;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class City extends DbModel<City> {

    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;

    public City() {
    }

    public City(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<City> getCities() {
        return getProviderCompartment().query(UriHelper.with(ReceiptContentProvider.AUTHORITY)
                .getUri(City.class), City.class).list();
    }

    public void putIfNotExist() {
        City city = cupboard().withContext(App.getContext())
                .query(getTableUri(), getEntityClass())
                .withSelection("id = ?", id).get();
        if (null != city) {
            this._id = city._id;
        }
        updateDb();
    }

    @Override
    protected Class<City> getEntityClass() {
        return City.class;
    }
}
