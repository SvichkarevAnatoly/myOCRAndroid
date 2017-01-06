package ru.myocr.model.align.util;

public abstract class WordGeneratorAbstract implements WordGenerator {
    @Override
    public String getWord(int length) {
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getChar();
        }
        return new String(result);
    }
}
