package ru.myocr.model;


import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ReceiptDataImpl implements ReceiptData {
    private final List<String> products;
    private final List<String> prices;

    public ReceiptDataImpl(List<String> products) {
        this(products, new ArrayList<>());
    }

    public ReceiptDataImpl(List<String> products, List<String> prices) {
        this.products = products;
        this.prices = prices;
    }

    @Override
    public List<String> getProducts() {
        return products;
    }

    @Override
    public List<String> getPrices() {
        return prices;
    }

    @Override
    public List<Pair<String, String>> getProductsPricesPairs() {
        final List<Pair<String, String>> pairs = new ArrayList<>();

        final int n = Math.max(products.size(), prices.size());
        for (int i = 0; i < n; i++) {
            String product = getProduct(i);
            String price = getPrice(i);
            pairs.add(new Pair<>(product, price));
        }
        return pairs;
    }

    private String getPrice(int index) {
        return index >= prices.size() ? "" : prices.get(index);
    }

    private String getProduct(int index) {
        return index >= products.size() ? "" : products.get(index);
    }

    @Override
    public void setProductItem(String productItem, int index) {
        products.set(index, productItem);
    }
}
