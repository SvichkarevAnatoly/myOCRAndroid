package ru.myocr.util;


import ru.myocr.api.ocr.ParsedPrice;

public class PriceUtil {
    private static String DOT = ".";

    public static String getValue(ParsedPrice price) {
        final String priceValue;
        if (price.getIntValue() != null) {
            priceValue = getStringWithDot(price.getIntValue());
        } else {
            priceValue = price.getStringValue();
        }
        return priceValue;
    }

    public static int getIntValue(String price) {
        if (!isCorrect(price)) {
            throw new IllegalArgumentException("Price " + price + " is not correct");
        }

        final int intPrice = Integer.parseInt(price.replace(DOT, ""));
        if (!price.contains(DOT)) {
            return Integer.parseInt(price) * 100;
        } else {
            final int dotIndex = price.lastIndexOf(DOT);
            if (dotIndex == price.length() - 2) {
                return intPrice * 10;
            }
        }

        return intPrice;
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
        final int length = price.length();
        int countDots = length - price.replace(DOT, "").length();
        if (countDots > 1) {
            return false;
        } else if (countDots == 1) {
            final int dotIndex = price.indexOf(DOT);
            if (dotIndex == 0) { // .20 for example
                return false;
            } else if (dotIndex == length - 1) { // 20. for example
                return false;
            } else if (dotIndex < length - 3) { // 20.567 for example
                return false;
            }
        }

        if (length > 10) {
            return false;
        }

        if (Integer.parseInt(price.replace(DOT, "")) <= 0) {
            return false;
        }

        return true;
    }
}
