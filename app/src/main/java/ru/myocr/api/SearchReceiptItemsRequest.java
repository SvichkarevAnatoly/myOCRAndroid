package ru.myocr.api;


public class SearchReceiptItemsRequest {
    private String city;
    private String shop;

    public SearchReceiptItemsRequest(String city, String shop) {
        this.city = city;
        this.shop = shop;
    }

    public String getCity() {
        return city;
    }

    public String getShop() {
        return shop;
    }
}
