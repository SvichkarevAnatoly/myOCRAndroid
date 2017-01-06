package ru.myocr.model.align.util;

import java.util.Random;

public abstract class WordGeneratorAbstract implements WordGenerator {
    private static final int WORD_LENGTH_DISPERSION = 3;
    protected final Random random;

    public WordGeneratorAbstract(long seed) {
        random = new Random(seed);
    }

    @Override
    public String getWord(int length) {
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getChar();
        }
        return new String(result);
    }

    @Override
    public String getString(int length, int averageWordLength) {
        final StringBuilder sb = new StringBuilder();

        while (sb.length() < length) {
            final int wordLength = averageWordLength +
                    random.nextInt(2 * WORD_LENGTH_DISPERSION) - WORD_LENGTH_DISPERSION;
            sb.append(getWord(wordLength));
            sb.append(' ');
        }
        sb.setLength(length);

        return sb.toString();
    }
}
