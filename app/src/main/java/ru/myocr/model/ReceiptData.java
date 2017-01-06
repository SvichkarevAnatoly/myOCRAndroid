package ru.myocr.model;


import android.util.Pair;

import java.util.List;

public interface ReceiptData {

    List<String> getProducts();

    List<String> getPrices();

    List<Pair<String, String>> getProductsPricesPairs();

    void setProductItem(String productItem, int index);
}
