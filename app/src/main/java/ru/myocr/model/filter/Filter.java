package ru.myocr.model.filter;


import java.io.Serializable;

public class Filter implements Serializable {

    private String city;
    private String shop;
    private String query;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
