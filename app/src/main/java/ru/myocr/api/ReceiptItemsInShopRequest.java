package ru.myocr.api;


public class ReceiptItemsInShopRequest {
    private String city;
    private String shop;

    public ReceiptItemsInShopRequest() {
    }

    public ReceiptItemsInShopRequest(String city, String shop) {
        this.city = city;
        this.shop = shop;
    }

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
}
