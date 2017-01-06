package ru.myocr.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OcrParser {
    private String productsText;
    private String pricesText;

    public OcrParser(String productsText) {
        this.productsText = productsText;
    }

    public void setPricesText(String pricesText) {
        this.pricesText = pricesText;
    }

    public List<String> parseProductList() {
        return new ArrayList<>(Arrays.asList(productsText.split("\\n")));
    }

    public List<String> parsePriceList() {
        return new ArrayList<>(Arrays.asList(pricesText.split("\\n")));
    }
}
