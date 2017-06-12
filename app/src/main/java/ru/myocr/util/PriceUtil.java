package ru.myocr.util;


import ru.myocr.api.ocr.ParsedPrice;

public class PriceUtil {
    private static String DOT = ".";

    public static int getIntValue(String price) {
        return Integer.parseInt(price.replace(DOT, ""));
    }

    public static String getValue(ParsedPrice price) {
        final String priceValue;
        if (price.getIntValue() != null) {
            priceValue = getStringWithDot(price.getIntValue());
        } else {
            priceValue = price.getStringValue();
        }
        return priceValue;
    }

    public static String getStringWithDot(int intValue) {
        final String stringValue = Integer.toString(intValue);
        final int priceLength = stringValue.length();
        if (priceLength <= 2) {
            return stringValue;
        } else {
            return stringValue.substring(0, priceLength - 2) + DOT +
                    stringValue.substring(priceLength - 2, priceLength);
        }
    }

    public static boolean isCorrect(String price) {
        int countDots = price.length() - price.replace(DOT, "").length();
        return countDots < 2 && price.length() <= 10;
    }
}
