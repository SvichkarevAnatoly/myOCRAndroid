package ru.myocr.model;


import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ReceiptDataImpl implements ReceiptData {
    private final static String EMPTY_LINE = "";
    private final List<String> prices;
    private List<String> products;

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
    public void setProducts(List<String> products) {
        this.products = products;
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

    @Override
    public int getProductSize() {
        return products.size();
    }

    @Override
    public int getPriceSize() {
        return prices.size();
    }

    @Override
    public void removeProduct(int idx) {
        products.remove(idx);
    }

    @Override
    public void removePrice(int idx) {
        prices.remove(idx);
    }

    @Override
    public void shiftProductUp(int idx) {
        final String curProduct = products.get(idx);
        final String prevProduct = products.get(idx - 1);
        products.set(idx - 1, prevProduct + ' ' + curProduct);
        removeProduct(idx);
    }

    @Override
    public void shiftProductDown(int idx) {
        final String curProduct = products.get(idx);
        final String nextProduct = products.get(idx + 1);
        products.set(idx + 1, curProduct + ' ' + nextProduct);
        removeProduct(idx);
    }

    @Override
    public void shiftPriceDown(int idx) {
        prices.add(idx, EMPTY_LINE);
    }

    private String getPrice(int index) {
        return index >= prices.size() ? null : prices.get(index);
    }

    private String getProduct(int index) {
        return index >= products.size() ? null : products.get(index);
    }

    @Override
    public void setProductItem(String productItem, int index) {
        products.set(index, productItem);
    }
}
