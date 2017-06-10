package ru.myocr.api;


public class ReceiptItemsInShopRequest {
    private long cityId;
    private long shopId;

    public ReceiptItemsInShopRequest() {
    }

    public ReceiptItemsInShopRequest(long cityId, long shopId) {
        this.cityId = cityId;
        this.shopId = shopId;
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
}
