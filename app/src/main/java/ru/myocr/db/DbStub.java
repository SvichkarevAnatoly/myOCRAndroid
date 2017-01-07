package ru.myocr.db;


import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import ru.myocr.model.OcrParser;

public class DbStub {

    public List<String> getAllProducts(Context context) {
        String dbProductsText = "";
        try {
            dbProductsText = readText("south.txt", context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final OcrParser parser = new OcrParser(dbProductsText);
        return parser.parseProductList();
    }

    private String readText(String fileName, Context context) throws IOException {
        final InputStream openInputStream = context.getAssets().open(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(openInputStream));

        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        openInputStream.close();
        reader.close();
        return stringBuilder.toString();
    }
}
