package ru.myocr.align.util;

public interface WordGenerator {
    char getChar();

    String getWord(int length);

    String getString(int length, int averageWordLength);
}
