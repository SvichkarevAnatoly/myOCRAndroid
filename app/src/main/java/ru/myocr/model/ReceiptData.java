package ru.myocr.model;


import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.myocr.api.ocr.Match;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.api.ocr.ParsedPrice;
import ru.myocr.api.ocr.ReceiptItemMatches;

public class ReceiptData implements Serializable {
    private final static String EMPTY_LINE = "";

    private final List<ReceiptItemPriceViewItem> receiptItemPriceViewItems;

    public ReceiptData(OcrReceiptResponse response) {
        receiptItemPriceViewItems = new ArrayList<>();
        final List<ReceiptItemMatches> receiptItemMatches = response.getItemMatches();
        final List<ParsedPrice> parsedPrices = response.getPrices();
        final int n = Math.min(receiptItemMatches.size(), parsedPrices.size());
        for (int i = 0; i < n; i++) {
            final ReceiptItemPriceViewItem item = new ReceiptItemPriceViewItem(
                    receiptItemMatches.get(i), parsedPrices.get(i));
            receiptItemPriceViewItems.add(item);
        }

        if (receiptItemMatches.size() > n) {
            for (int i = n; i < receiptItemMatches.size(); i++) {
                final ReceiptItemPriceViewItem item = new ReceiptItemPriceViewItem(
                        receiptItemMatches.get(i), new ParsedPrice(EMPTY_LINE));
                receiptItemPriceViewItems.add(item);
            }
        }

        if (parsedPrices.size() > n) {
            for (int i = n; i < parsedPrices.size(); i++) {
                final ReceiptItemPriceViewItem item = new ReceiptItemPriceViewItem(
                        new ReceiptItemMatches(EMPTY_LINE, new ArrayList<>()), parsedPrices.get(i));
                receiptItemPriceViewItems.add(item);
            }
        }
    }

    public ReceiptData(ReceiptData receiptData) {
        receiptItemPriceViewItems = new ArrayList<>();
        List<ReceiptItemPriceViewItem> receiptItemPriceViewItems = receiptData.receiptItemPriceViewItems;
        for (ReceiptItemPriceViewItem receiptItemPriceViewItem : receiptItemPriceViewItems) {
            final ReceiptItemMatches receiptItemMatches = receiptItemPriceViewItem.getReceiptItemMatches();
            final ArrayList<Match> matches = new ArrayList<>();
            for (Match match : receiptItemMatches.getMatches()) {
                final Match copyMatch = new Match(match.getMatch(), match.getScore());
                matches.add(copyMatch);
            }
            final ReceiptItemMatches copyReceiptItemMatches = new ReceiptItemMatches(receiptItemMatches.getSource(), matches);
            final String copyReceiptItem = receiptItemPriceViewItem.getReceiptItem();
            final String price = receiptItemPriceViewItem.getPrice();
            final ReceiptItemPriceViewItem copyReceiptItemPriceViewItem = new ReceiptItemPriceViewItem(copyReceiptItemMatches, copyReceiptItem, price);
            this.receiptItemPriceViewItems.add(copyReceiptItemPriceViewItem);
        }
    }

    public ReceiptItemPriceViewItem getReceiptItemPriceViewItem(int index) {
        return index >= receiptItemPriceViewItems.size() ? null : receiptItemPriceViewItems.get(index);
    }

    public List<Pair<String, String>> getProductsPricesPairs() {
        final List<Pair<String, String>> pairs = new ArrayList<>();
        for (ReceiptItemPriceViewItem item : receiptItemPriceViewItems) {
            final String receiptItem = item.getReceiptItem();
            final String price = item.getPrice();
            pairs.add(new Pair<>(receiptItem, price));
        }

        return pairs;
    }

    public void removeReceiptItem(int idx) {
        final int size = receiptItemPriceViewItems.size();
        final int lastIndex = size - 1;
        if (idx == lastIndex) {
            final ReceiptItemPriceViewItem lastItem = receiptItemPriceViewItems.get(idx);
            lastItem.setReceiptItem(EMPTY_LINE);
            lastItem.setReceiptItemMatches(new ReceiptItemMatches(EMPTY_LINE, new ArrayList<>()));
        } else {
            for (int i = idx; i < lastIndex; i++) {
                final ReceiptItemPriceViewItem item = receiptItemPriceViewItems.get(i);
                final ReceiptItemPriceViewItem nextItem = receiptItemPriceViewItems.get(i + 1);
                item.replaceReceiptItemInfo(nextItem);
            }
        }

        if (receiptItemPriceViewItems.get(lastIndex).isEmpty()) {
            receiptItemPriceViewItems.remove(lastIndex);
        }
    }

    public void removePrice(int idx) {
        final int size = receiptItemPriceViewItems.size();
        final int lastIndex = size - 1;
        if (idx == lastIndex) {
            receiptItemPriceViewItems.get(idx).setPrice(EMPTY_LINE);
        } else {
            for (int i = idx; i < lastIndex; i++) {
                final ReceiptItemPriceViewItem item = receiptItemPriceViewItems.get(i);
                final ReceiptItemPriceViewItem nextItem = receiptItemPriceViewItems.get(i + 1);
                item.replacePrice(nextItem);
            }
        }

        if (receiptItemPriceViewItems.get(lastIndex).isEmpty()) {
            receiptItemPriceViewItems.remove(lastIndex);
        }
    }

    public int size() {
        return receiptItemPriceViewItems.size();
    }

    public ReceiptData getCompletedList() {
        final ReceiptData completedReceiptData = new ReceiptData(this);

        final Iterator<ReceiptItemPriceViewItem> iterator = completedReceiptData.receiptItemPriceViewItems.iterator();
        while (iterator.hasNext()) {
            final ReceiptItemPriceViewItem item = iterator.next();
            if (item.isPartEmpty()) {
                iterator.remove();
            }
        }
        return completedReceiptData;
    }
}
