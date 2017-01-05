package ru.myocr.model.align;


import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static ru.myocr.model.align.TestRealData.gigant39OcrProducts;
import static ru.myocr.model.align.TestRealData.gigant39RealProducts;

public class DataBaseFinderTest {
    @Test
    public void gigantOne() throws Exception {
        final List<String> products = Arrays.asList(gigant39RealProducts);
        final DataBaseFinder finder = new DataBaseFinder(products);

        // "МАЙОНП НОВОСИОИЪ’СКИИ"
        int index = 9;
        String ocrProduct = gigant39OcrProducts[index];
        final String product = products.get(index);

        printAlignment(ocrProduct, product);

        final String bestMatchProduct = finder.find(ocrProduct);

        assertThat(bestMatchProduct, is(product));
    }

    @Test
    public void gigantAll() throws Exception {
        final List<String> products = Arrays.asList(gigant39RealProducts);
        final DataBaseFinder finder = new DataBaseFinder(products);

        for (int i = 0; i < gigant39OcrProducts.length; i++) {
            if (i == 5) continue;

            final String ocrProduct = gigant39OcrProducts[i];

            final String bestMatchProduct = finder.find(ocrProduct);

            printAlignment(ocrProduct, bestMatchProduct);
            assertThat(products.get(i), is(bestMatchProduct));
        }
    }

    private void printAlignment(String ocrProduct, String bestMatchProduct) {
        final SimpleAligner aligner = new SimpleAligner();

        final int score = aligner.align(ocrProduct, bestMatchProduct);
        final String align1 = aligner.getAlignString1();
        final String align2 = aligner.getAlignString2();

        System.out.println("score = " + score + '\n' +
                align1 + '\n' + align2 + "\n");
    }
}
