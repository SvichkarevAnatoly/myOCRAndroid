package ru.myocr.model;

import android.content.Context;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import nl.littlerobots.cupboard.tools.provider.UriHelper;
import nl.qbusict.cupboard.annotation.Ignore;
import ru.myocr.db.ReceiptContentProvider;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import static ru.myocr.model.DbModel.getProviderCompartment;

public class Receipt implements Serializable {

    public static final Uri URI;

    static {
        URI = DbModel.getUriHelper().getUri(Receipt.class);
    }

    public Long _id;
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

    @Ignore
    @SerializedName("items")
    public List<ReceiptItem> items;

    @SerializedName("totalCostSum")
    public int totalCostSum;
    @SerializedName("paymentSum")
    public int paymentSum;
    @SerializedName("changeSum")
    public int changeSum;
    @SerializedName("discountSum")
    public int discountSum;

    @Ignore
    public List<Tag> tags;

    public static List<Receipt> findReceiptByTag(String tag) {
        return getProviderCompartment().query(ReceiptContentProvider.URI_RECEIPT_BY_TAG, Receipt.class)
                .withSelection("", tag).list();
    }

    public static List<Receipt> findReceiptByTagId(Long id) {
        return getProviderCompartment().query(ReceiptContentProvider.URI_RECEIPT_BY_TAG_ID, Receipt.class)
                .withSelection("", id.toString()).list();
    }

    public static List<Receipt> findReceiptWithoutTag() {
        return getProviderCompartment().query(ReceiptContentProvider.URI_RECEIPT_WITHOUT_TAG, Receipt.class).list();
    }

    public void loadReceiptItems(Context context) {
        UriHelper helper = UriHelper.with(ReceiptContentProvider.AUTHORITY);
        Uri cheeseUri = helper.getUri(ReceiptItem.class);
        items = cupboard()
                .withContext(context)
                .query(cheeseUri, ReceiptItem.class)
                .withSelection("receiptId = ?", String.valueOf(_id)).list();
    }

    public void loadTags(Context context) {
        tags = cupboard()
                .withContext(context)
                .query(ReceiptContentProvider.URI_RECEIPT_TAG, Tag.class)
                .withSelection("", _id.toString()).list();
    }

    @Override
    public String toString() {
        return "GetTicketResponse{" +
                "id=" + id +
                ", shiftNumber=" + shiftNumber +
                ", date='" + date + '\'' +
                ", market=" + market.title +
                ", cashier='" + cashier + '\'' +
                ", items=" + items.size() +
                ", totalCostSum=" + totalCostSum +
                ", paymentSum=" + paymentSum +
                ", changeSum=" + changeSum +
                ", discountSum=" + discountSum +
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
