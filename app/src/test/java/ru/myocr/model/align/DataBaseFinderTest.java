package ru.myocr.model.align;


import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ru.myocr.model.align.util.RussianAlphabetGenerator;
import ru.myocr.model.align.util.WordGenerator;

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
        // TODO: tune parameters
        final SimpleAligner aligner = new SimpleAligner(1, 1, 1);

        final List<String> products = Arrays.asList(gigant39RealProducts);
        final DataBaseFinder finder = new DataBaseFinder(products);

        int matchCounter = 0;
        for (int i = 0; i < gigant39OcrProducts.length; i++) {
            final String ocrProduct = gigant39OcrProducts[i];

            final String bestMatchProduct = finder.find(ocrProduct, aligner);

            System.out.println(i);
            printAlignment(ocrProduct, bestMatchProduct);

            final String product = products.get(i);
            matchCounter += bestMatchProduct.equals(product) ? 1 : 0;

            // skip 5
            if (i == 5) {
                continue;
            }
            assertThat(bestMatchProduct, is(product));
        }
        System.out.println("Total: " + matchCounter + '/' + products.size());
    }

    @Test
    public void gigantNproducts() throws Exception {
        final int N = 1000;

        // TODO: tune parameters
        final SimpleAligner aligner = new SimpleAligner(1, 1, 1);

        final List<String> products = Arrays.asList(gigant39RealProducts);
        final List<String> productsWithRandom = new ArrayList<>(products);
        addRandomProducts(productsWithRandom, N);

        final DataBaseFinder finder = new DataBaseFinder(productsWithRandom);

        int matchCounter = 0;
        for (int i = 0; i < gigant39OcrProducts.length; i++) {
            final String ocrProduct = gigant39OcrProducts[i];

            final String bestMatchProduct = finder.find(ocrProduct, aligner);
            final String product = products.get(i);

            final boolean expectedMatch = bestMatchProduct.equals(product);
            System.out.println(i + " " + (expectedMatch ? "" : String.valueOf(false)));
            printAlignment(ocrProduct, bestMatchProduct);

            matchCounter += bestMatchProduct.equals(product) ? 1 : 0;
        }
        System.out.println("Total: " + matchCounter + '/' + products.size());
    }

    private void addRandomProducts(List<String> products, int newSize) {
        int minLength = Integer.MAX_VALUE;
        int maxLength = 0;
        for (String product : products) {
            minLength = Math.min(minLength, product.length());
            maxLength = Math.max(maxLength, product.length());
        }
        final int diffLength = maxLength - minLength;

        final WordGenerator stringGenerator = new RussianAlphabetGenerator(0);
        final Random randomLength = new Random(0);
        for (int i = products.size(); i < newSize; i++) {
            final int length = minLength + randomLength.nextInt(diffLength);
            final String randomString = stringGenerator.getWord(length);
            products.add(randomString);
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
