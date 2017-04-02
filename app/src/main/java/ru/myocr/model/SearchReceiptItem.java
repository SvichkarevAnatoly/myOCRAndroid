package ru.myocr.model;


public class SearchReceiptItem {
    private String item;
    private int price;
    private String date;

    public SearchReceiptItem(String item, int price, String date) {
        this.item = item;
        this.price = price;
        this.date = date;
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
