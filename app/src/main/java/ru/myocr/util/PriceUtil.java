package ru.myocr.util;


import ru.myocr.api.ocr.ParsedPrice;

public class PriceUtil {
    public static String getValue(ParsedPrice price) {
        final String priceValue;
        if (price.getIntValue() != null) {
            priceValue = getStringWithDot(price.getIntValue());
        } else {
            priceValue = price.getStringValue();
        }
        return priceValue;
    }

    private static String getStringWithDot(int intValue) {
        final String stringValue = Integer.toString(intValue);
        final int priceLength = stringValue.length();
        return stringValue.substring(0, priceLength - 2) + "." +
                stringValue.substring(priceLength - 2, priceLength);
    }
}
