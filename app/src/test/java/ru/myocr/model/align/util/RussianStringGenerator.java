package ru.myocr.model.align.util;


import java.util.Random;

public class RussianStringGenerator {
    private static final char[] CHARSET_AZ =
            "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ    ".toCharArray();

    private Random random;

    public RussianStringGenerator(long seed) {
        random = new Random(seed);
    }

    public String get(int length) {
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            int randomCharIndex = random.nextInt(CHARSET_AZ.length);
            result[i] = CHARSET_AZ[randomCharIndex];
        }
        return new String(result);
    }
}
