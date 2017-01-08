package ru.myocr.model;


import android.util.Pair;

import java.util.List;

public interface ReceiptData {

    List<String> getProducts();

    List<String> getPrices();

    List<Pair<String, String>> getProductsPricesPairs();

    int getProductSize();

    int getPriceSize();

    void removeProduct(int idx);

    void removePrice(int idx);

    void shiftProductUp(int idx);

    void shiftProductDown(int idx);

    void shiftPriceDown(int idx);

    void setProductItem(String productItem, int index);
}
