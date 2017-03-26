package ru.myocr.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-M-yyyy hh:mm", Locale.US);

    public static Date parse(String time) throws ParseException {
        return DATE_FORMAT.parse(time);
    }

    public static String parse(Date time) {
        return DATE_FORMAT.format(time);
    }
}
