package ru.myocr.model.align.util;

import java.util.Random;

public abstract class WordGeneratorAbstract implements WordGenerator {
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
}
