package ru.myocr.model.align.util;


import java.util.Random;

public class RussianAlphabetGenerator extends WordGeneratorAbstract {
    private static final char[] ALPHABET =
            "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray();

    private Random random;

    public RussianAlphabetGenerator(long seed) {
        random = new Random(seed);
    }

    @Override
    public char getChar() {
        int randomCharIndex = random.nextInt(ALPHABET.length);
        return ALPHABET[randomCharIndex];
    }
}
