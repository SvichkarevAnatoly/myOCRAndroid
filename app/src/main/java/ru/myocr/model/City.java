package ru.myocr.model;

import com.google.gson.annotations.SerializedName;

public class City extends DbModel<City> {

    @SerializedName("name")
    private String name;

    public City() {
    }

    public City(long id, String name) {
        this._id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    protected Class<City> getEntityClass() {
        return City.class;
    }
}
