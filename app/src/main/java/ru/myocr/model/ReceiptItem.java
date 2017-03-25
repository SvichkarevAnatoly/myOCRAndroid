package ru.myocr.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ReceiptItem {

    public static final Uri URI;

    static {
        URI = DbModel.getUriHelper().getUri(ReceiptItem.class);
    }

    public Long _id;
    @SerializedName("number")
    public int number;
    @SerializedName("title")
    public String title;
    @SerializedName("datetime")
    public Date date;
    @SerializedName("price")
    public int price;
    @SerializedName("amount")
    public float amount;
    @SerializedName("cost")
    public int cost;
    public long receiptId;

    public ReceiptItem() {
    }

    public ReceiptItem(int number, String title, Date date, int price, float amount, int cost) {
        this.number = number;
        this.title = title;
        this.date = date;
        this.price = price;
        this.amount = amount;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Item{" +
                "number=" + number +
                ", title='" + title + '\'' +
                ", date='" + date +
                ", price=" + price +
                ", amount=" + amount +
                ", cost=" + cost +
                '}';
    }
}
