package ru.myocr.align;


import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import ru.myocr.model.OcrParser;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BigRealDBTest {
    @Test
    public void pashaCheck() throws Exception {
        final String fileName = "/home/anatoly/Documents/self/fan/OCR/myOCRAndroid/app/src/main/assets/south.txt";
        final String productsText = readText(fileName);

        final OcrParser parser = new OcrParser(productsText);
        final List<String> products = parser.parseProductList();
        System.out.println(products.size());

        final DataBaseFinder finder = new DataBaseFinder(products);

        final String ocr = "dfgDIDAS Гель д/д/fgff/уша5 H567appy fбоdfдffр.sд/жен.250";
        final String match = finder.find(ocr);

        printAlignment(ocr, match);

        final String expectedOcr = "ADIDAS Гель д/душа Happy бодр.д/жен.250";
        assertThat(match, is(expectedOcr));
    }

    @Test
    public void photoOcr() throws Exception {
        final String path = "/home/anatoly/Documents/self/fan/OCR/myOCRAndroid/app/src/main/assets/";
        final String productsDBFileName = path + "south.txt";
        final String productsText = readText(productsDBFileName);

        OcrParser parser = new OcrParser(productsText);
        final List<String> products = parser.parseProductList();
        System.out.println(products.size());

        String productsOcrFileName = path + "ocrSouth.txt";
        final String productsOcrText = readText(productsOcrFileName);

        parser = new OcrParser(productsOcrText);
        final List<String> productsOcr = parser.parseProductList();
        System.out.println(productsOcr.size());

        final DataBaseFinder finder = new DataBaseFinder(products);

        for (String productOcr : productsOcr) {
            final String match = finder.find(productOcr);
            printAlignment(productOcr, match);
        }
    }

    private String readText(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return stringBuilder.toString();
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
