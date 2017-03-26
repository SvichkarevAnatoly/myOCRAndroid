package ru.myocr.api;

import java.util.ArrayList;
import java.util.List;

public class SavePriceRequest {
    private String cityName;
    private String shopName;
    private String time;

    private List<ReceiptPriceItem> items = new ArrayList<>();

    public SavePriceRequest() {
    }

    public SavePriceRequest(String cityName, String shopName, List<ReceiptPriceItem> items) {
        this.cityName = cityName;
        this.shopName = shopName;
        this.items = items;
    }

    public SavePriceRequest(String cityName, String shopName, String time, List<ReceiptPriceItem> items) {
        this.cityName = cityName;
        this.shopName = shopName;
        this.time = time;
        this.items = items;
    }

    public String getCityName() {
        return cityName;
    }

    public String getShopName() {
        return shopName;
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
