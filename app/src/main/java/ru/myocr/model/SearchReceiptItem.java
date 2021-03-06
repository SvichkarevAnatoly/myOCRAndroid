package ru.myocr.model;


import java.io.Serializable;

import ru.myocr.util.PriceUtil;

public class SearchReceiptItem implements Serializable {
    private String item;
    private int price;
    private String date;

    public SearchReceiptItem(String item, int price, String date) {
        this.item = item;
        this.price = price;
        this.date = date;
    }

    @Override
    public String toString() {
        return item + "\n" + date + "\n" + PriceUtil.getStringWithDot(price) + " руб.";
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
