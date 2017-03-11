package ru.myocr.util;


import ru.myocr.api.ocr.ParsedPrice;

public class PriceUtil {
    public static String getValue(ParsedPrice price) {
        final String priceValue;
        if (price.getIntValue() != null) {
            final Integer intValue = price.getIntValue();
            final String stringValue = intValue.toString();
            final int priceLength = stringValue.length();
            priceValue = stringValue.substring(0, priceLength - 2) + "." +
                    stringValue.substring(priceLength - 2, priceLength);
        } else {
            priceValue = price.getStringValue();
        }
        return priceValue;
    }
}
