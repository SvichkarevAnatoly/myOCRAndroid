package ru.myocr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Receipt implements Serializable {

    @SerializedName("receipt_number")
    public long id;
    @SerializedName("shift_number")
    public long shiftNumber;
    @SerializedName("datetime")
    public Date date;
    @SerializedName("market")
    public Market market;
    @SerializedName("cashier")
    public String cashier;

    @SerializedName("items")
    public List<ReceiptItem> items;

    @SerializedName("total_cost_sum")
    public int total_cost_sum;
    @SerializedName("payment_sum")
    public int payment_sum;
    @SerializedName("change_sum")
    public int change_sum;
    @SerializedName("discount_sum")
    public int discount_sum;

    @Override
    public String toString() {
        return "GetTicketResponse{" +
                "id=" + id +
                ", shiftNumber=" + shiftNumber +
                ", date='" + date + '\'' +
                ", market=" + market.title +
                ", cashier='" + cashier + '\'' +
                ", items=" + items.size() +
                ", total_cost_sum=" + total_cost_sum +
                ", payment_sum=" + payment_sum +
                ", change_sum=" + change_sum +
                ", discount_sum=" + discount_sum +
                '}';
    }

    public static class Market {
        @SerializedName("title")
        public String title;
        @SerializedName("address")
        public String address;
        @SerializedName("inn")
        public String inn;
    }
}
