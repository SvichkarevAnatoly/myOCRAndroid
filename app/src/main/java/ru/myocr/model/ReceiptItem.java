package ru.myocr.model;

import com.google.gson.annotations.SerializedName;

public class ReceiptItem {

    public Long _id;
    @SerializedName("number")
    public int number;
    @SerializedName("title")
    public String title;
    @SerializedName("price")
    public int price;
    @SerializedName("amount")
    public float amount;
    @SerializedName("cost")
    public int cost;
    public long receiptId;

    public ReceiptItem() {
    }

    public ReceiptItem(int number, String title, int price, float amount, int cost) {
        this.number = number;
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Item{" +
                "number=" + number +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                ", cost=" + cost +
                '}';
    }
}
