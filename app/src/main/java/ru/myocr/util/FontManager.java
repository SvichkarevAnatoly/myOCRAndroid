package ru.myocr.util;


import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class FontManager {

    private static Map<Font, Typeface> fontMap = new HashMap<>();

    public static Typeface getFont(Context context, Font font) {
        if (fontMap.containsKey(font)) {
            return fontMap.get(font);
        } else {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), font.path);
            fontMap.put(font, tf);
            return tf;
        }
    }

    public enum Font {
        ROBOTO_REGULAR("Roboto-Regular.ttf"),
        ROBOTO_MEDIUM("Roboto-Medium.ttf");

        public final String path;

        Font(String path) {
            this.path = path;
        }
    }
}
