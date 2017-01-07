package ru.myocr.align;

import java.util.ArrayList;
import java.util.List;

public class DataBaseFinder {
    private final List<String> products;

    public DataBaseFinder(List<String> products) {
        this.products = products;
    }

    public String find(String ocrProduct) {
        final Aligner aligner = new SimpleAligner();
        return find(ocrProduct, aligner);
    }

    public String find(String ocrProduct, Aligner aligner) {
        int bestScore = Integer.MIN_VALUE;
        String bestScoreProduct = "";
        for (String product : products) {
            int alignScore = aligner.align(ocrProduct, product);
            if (alignScore > bestScore) {
                bestScore = alignScore;
                bestScoreProduct = product;
            }
        }

        return bestScoreProduct;
    }

    public List<String> findAll(List<String> ocrProducts) {
        final ArrayList<String> bestScoreProducts = new ArrayList<>(ocrProducts.size());
        for (String ocrProduct : ocrProducts) {
            final String bestScoreProduct = find(ocrProduct);
            bestScoreProducts.add(bestScoreProduct);
        }
        return bestScoreProducts;
    }
}
