package ru.myocr.align;


public interface Aligner {
    int align(String str1, String str2);

    String getAlignString1();

    String getAlignString2();
}
