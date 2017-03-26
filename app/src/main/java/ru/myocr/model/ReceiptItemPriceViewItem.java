package ru.myocr.model;


import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.myocr.api.ocr.Match;
import ru.myocr.api.ocr.ParsedPrice;
import ru.myocr.api.ocr.ReceiptItemMatches;
import ru.myocr.util.PriceUtil;

public class ReceiptItemPriceViewItem implements Serializable {
    private final static String EMPTY_LINE = "";

    private ReceiptItemMatches receiptItemMatches;

    private String receiptItem;
    private String price;

    public ReceiptItemPriceViewItem(ReceiptItemMatches receiptItemMatches, ParsedPrice parsedPrice) {
        this.receiptItemMatches = receiptItemMatches;

        receiptItem = initReceiptItem();
        price = PriceUtil.getValue(parsedPrice);
    }

    public String initReceiptItem() {
        final List<Match> matches = receiptItemMatches.getMatches();
        if (!matches.isEmpty()) {
            return matches.get(0).getMatch();
        }

        return receiptItemMatches.getSource();
    }

    public List<String> getMatches() {
        final List<String> matches = new ArrayList<>();
        for (Match match : receiptItemMatches.getMatches()) {
            matches.add(match.getMatch());
        }

        return matches;
    }

    public String getSource() {
        return receiptItemMatches.getSource();
    }

    public String getReceiptItem() {
        return receiptItem;
    }

    public void setReceiptItem(String receiptItem) {
        this.receiptItem = receiptItem;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ReceiptItemMatches getReceiptItemMatches() {
        return receiptItemMatches;
    }

    public void setReceiptItemMatches(ReceiptItemMatches receiptItemMatches) {
        this.receiptItemMatches = receiptItemMatches;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(receiptItem) && TextUtils.isEmpty(price);
    }

    public void replaceReceiptItemInfo(ReceiptItemPriceViewItem item) {
        setReceiptItem(item.getReceiptItem());
        setReceiptItemMatches(item.getReceiptItemMatches());
        item.setReceiptItem(EMPTY_LINE);
        item.setReceiptItemMatches(new ReceiptItemMatches(EMPTY_LINE, new ArrayList<>()));
    }

    public void replacePrice(ReceiptItemPriceViewItem item) {
        setPrice(item.getPrice());
        item.setPrice(EMPTY_LINE);
    }
}
