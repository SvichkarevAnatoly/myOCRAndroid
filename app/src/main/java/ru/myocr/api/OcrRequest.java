package ru.myocr.api;


import android.graphics.Bitmap;

public class OcrRequest {

    public Bitmap receiptItems;
    public Bitmap prices;
    public String city;
    public String shop;

    public OcrRequest(Bitmap receiptItems, Bitmap prices, String city, String shop) {
        this.receiptItems = receiptItems;
        this.prices = prices;
        this.city = city;
        this.shop = shop;
    }
}
