package ru.myocr.model.filter;


import java.io.Serializable;

public class Filter implements Serializable {

    private long cityId = -1;
    private long shopId = -1;
    private String query;

    public boolean hasCityId() {
        return cityId != -1;
    }

    public boolean hasShopId() {
        return shopId != -1;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
