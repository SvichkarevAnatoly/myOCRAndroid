package ru.myocr.model;

import com.google.gson.annotations.SerializedName;

public class Shop extends DbModel<Shop> {

    @SerializedName("name")
    private String name;

    public Shop() {
    }

    public Shop(long id, String name) {
        this._id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    protected Class<Shop> getEntityClass() {
        return Shop.class;
    }
}
