package ru.myocr.api;


import android.graphics.Bitmap;

public class OcrRequest {

    public Bitmap receiptItems;
    public Bitmap prices;
    public long cityId;
    public long shopId;

    public OcrRequest(Bitmap receiptItems, Bitmap prices, long cityId, long shopId) {
        this.receiptItems = receiptItems;
        this.prices = prices;
        this.cityId = cityId;
        this.shopId = shopId;
    }
}
