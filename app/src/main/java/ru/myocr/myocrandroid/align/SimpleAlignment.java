package ru.myocr.myocrandroid.align;

public class SimpleAlignment {
    // штрафы
    private int deletion;
    private int mismatch;

    private int scoreMatrix[][];
    private char backtrack[][];

    public SimpleAlignment(int deletion, int mismatch) {
        this.deletion = deletion;
        this.mismatch = mismatch;
    }

    public int align(String string1, String string2) {
        final int s1Len = string1.length();
        final int s2Len = string2.length();

        scoreMatrix = new int[s1Len + 1][];
        for (int i = 0; i < s1Len + 1; i++) {
            scoreMatrix[i] = new int[s2Len + 1];
        }

        // TODO: доделать
        
        return 0;
    }
}
