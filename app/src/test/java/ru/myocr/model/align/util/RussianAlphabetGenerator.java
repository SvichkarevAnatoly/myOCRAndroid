package ru.myocr.model.align.util;


public class RussianAlphabetGenerator extends WordGeneratorAbstract {
    private static final char[] ALPHABET =
            "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray();

    public RussianAlphabetGenerator(long seed) {
        super(seed);
    }

    @Override
    public char getChar() {
        int randomCharIndex = random.nextInt(ALPHABET.length);
        return ALPHABET[randomCharIndex];
    }
}
