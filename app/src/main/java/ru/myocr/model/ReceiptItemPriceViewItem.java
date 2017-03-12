package ru.myocr.model;


import java.util.ArrayList;
import java.util.List;

import ru.myocr.api.ocr.Match;
import ru.myocr.api.ocr.ParsedPrice;
import ru.myocr.api.ocr.ReceiptItemMatches;
import ru.myocr.util.PriceUtil;

public class ReceiptItemPriceViewItem {
    private final ReceiptItemMatches receiptItemMatches;

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

    public List<String> getMatches() {
        final List<String> matches = new ArrayList<>();
        for (Match match : receiptItemMatches.getMatches()) {
            matches.add(match.getMatch());
        }

        return matches;
    }
}
