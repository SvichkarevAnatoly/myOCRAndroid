package ru.myocr.model.align.util;

import java.util.List;

public class ShuffleGenerator extends WordGeneratorAbstract {
    private final List<String> stringList;

    public ShuffleGenerator(long seed, List<String> stringList) {
        super(seed);
        this.stringList = stringList;
    }

    @Override
    public char getChar() {
        final int elementIndex = random.nextInt(stringList.size());
        final String string = stringList.get(elementIndex);
        final int charIndex = random.nextInt(string.length());
        return string.charAt(charIndex);
    }

    @Override
    public String getString(int length, int averageWordLength) {
        final StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(getChar());
        }

        return sb.toString();
    }
}
