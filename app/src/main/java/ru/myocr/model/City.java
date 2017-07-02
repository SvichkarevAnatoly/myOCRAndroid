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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        return name != null ? name.equals(city.name) : city.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
