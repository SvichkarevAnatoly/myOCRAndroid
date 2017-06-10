package ru.myocr.api;

import java.util.ArrayList;
import java.util.List;

public class SavePriceRequest {
    private long cityId;
    private long shopId;
    private String time;

    private List<ReceiptPriceItem> items = new ArrayList<>();

    public SavePriceRequest() {
    }

    public SavePriceRequest(long cityId, long shopId, List<ReceiptPriceItem> items) {
        this.cityId = cityId;
        this.shopId = shopId;
        this.items = items;
    }

    public SavePriceRequest(long cityId, long shopId, String time, List<ReceiptPriceItem> items) {
        this.cityId = cityId;
        this.shopId = shopId;
        this.time = time;
        this.items = items;
    }

    public long getCityId() {
        return cityId;
    }

    public long getShopId() {
        return shopId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<ReceiptPriceItem> getItems() {
        return items;
    }

    public static class ReceiptPriceItem {
        private String name;
        private int price;

        public ReceiptPriceItem() {
        }

        public ReceiptPriceItem(String name, int price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPrice() {
            return price;
        }
    }
}
