package ru.myocr.model;


import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.api.ocr.ParsedPrice;
import ru.myocr.api.ocr.ReceiptItemMatches;

public class ReceiptData {
    private final static String EMPTY_LINE = "";

    private final List<ReceiptItemPriceViewItem> receiptItemPriceViewItems;

    public ReceiptData(OcrReceiptResponse response) {
        receiptItemPriceViewItems = new ArrayList<>();
        final List<ReceiptItemMatches> receiptItemMatches = response.getItemMatches();
        final List<ParsedPrice> parsedPrices = response.getPrices();
        final int n = Math.min(receiptItemMatches.size(), receiptItemMatches.size());
        for (int i = 0; i < n; i++) {
            final ReceiptItemPriceViewItem item = new ReceiptItemPriceViewItem(
                    receiptItemMatches.get(i), parsedPrices.get(i));
            receiptItemPriceViewItems.add(item);
        }

        if (receiptItemMatches.size() > n) {
            for (int i = 0; i < receiptItemMatches.size(); i++) {
                final ReceiptItemPriceViewItem item = new ReceiptItemPriceViewItem(
                        receiptItemMatches.get(i), new ParsedPrice(EMPTY_LINE));
                receiptItemPriceViewItems.add(item);
            }
        }

        if (parsedPrices.size() > n) {
            for (int i = 0; i < parsedPrices.size(); i++) {
                final ReceiptItemPriceViewItem item = new ReceiptItemPriceViewItem(
                        new ReceiptItemMatches(EMPTY_LINE, new ArrayList<>()), parsedPrices.get(i));
                receiptItemPriceViewItems.add(item);
            }
        }
    }

    public ReceiptItemPriceViewItem getReceiptItemPriceViewItem(int index) {
        return index >= receiptItemPriceViewItems.size() ? null : receiptItemPriceViewItems.get(index);
    }

    public List<Pair<String, String>> getProductsPricesPairs() {
        final List<Pair<String, String>> pairs = new ArrayList<>();
        for (ReceiptItemPriceViewItem item : receiptItemPriceViewItems) {
            pairs.add(new Pair<>(item.getReceiptItem(), item.getPrice()));
        }

        return pairs;
    }

    public void removeProduct(int idx) {
        // products.remove(idx);
    }

    public void removePrice(int idx) {
        // prices.remove(idx);
    }

    public void shiftProductUp(int idx) {
        /*final String curProduct = products.get(idx);
        final String prevProduct = products.get(idx - 1);
        products.set(idx - 1, prevProduct + ' ' + curProduct);
        removeProduct(idx);*/
    }

    public void shiftProductDown(int idx) {
        /*final String curProduct = products.get(idx);
        final String nextProduct = products.get(idx + 1);
        products.set(idx + 1, curProduct + ' ' + nextProduct);
        removeProduct(idx);*/
    }

    public void shiftPriceDown(int idx) {
        // prices.add(idx, EMPTY_LINE);
    }

    public int size() {
        return receiptItemPriceViewItems.size();
    }
}
