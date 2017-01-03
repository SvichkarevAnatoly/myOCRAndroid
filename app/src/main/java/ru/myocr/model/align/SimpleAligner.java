package ru.myocr.model.align;

public class SimpleAligner implements Aligner {
    // символ делеции
    public final static char DELETE = '-';

    // внутренние символы для кодирования траектории
    private final static char DOWN = '↓';
    private final static char RIGHT = '→';
    private final static char DIAG = '↘';

    // штрафы
    // за делецию
    private int deletion;
    // за несовпадение
    private int mismatch;
    private int match;

    // сравниваемые строки
    private String str1;
    private String str2;
    // выравнивания сравниваемых строк
    private String alignStr1;
    private String alignStr2;

    // длины сравниваемых строк
    private int s1Len;
    private int s2Len;

    // матрица подсчётов
    private int score[][];
    // матрица для восстановления выравнивания
    private char backtrack[][];

    public SimpleAligner() {
        this(1, 1, 1);
    }

    // от соотношения двух этих параметров зависит выравнивание
    public SimpleAligner(int deletion, int mismatch) {
        this(deletion, mismatch, 1);
    }

    public SimpleAligner(int deletion, int mismatch, int match) {
        this.deletion = deletion;
        this.mismatch = mismatch;
        this.match = match;
    }

    // возвращает меру подобия двух строк,
    // строки не обязательно равной длины
    public int align(String str1, String str2) {
        this.str1 = str1;
        this.str2 = str2;
        s1Len = str1.length();
        s2Len = str2.length();

        initMatrices();
        fillMatrices();

        return score[s2Len][s1Len];
    }

    // вернуть первую выровненную строку
    // предварительно нужно вызвать align(string1, string2)
    public String getAlignString1() {
        recovery();
        return alignStr1;
    }

    // вернуть вторую выровненную строку
    // предварительно нужно вызвать align(string1, string2)
    public String getAlignString2() {
        recovery();
        return alignStr2;
    }

    private void initMatrices() {
        score = initIntMatrix();
        backtrack = initCharMatrix();

        initBoarders();
    }

    private int[][] initIntMatrix() {
        final int[][] matrix = new int[s2Len + 1][];
        for (int i = 0; i < s2Len + 1; i++) {
            matrix[i] = new int[s1Len + 1];
        }

        return matrix;
    }

    private char[][] initCharMatrix() {
        final char[][] matrix = new char[s2Len][];
        for (int i = 0; i < s2Len; i++) {
            matrix[i] = new char[s1Len];
        }

        return matrix;
    }

    private void initBoarders() {
        for (int i = 1; i <= s2Len; i++) {
            score[i][0] = -deletion * i;
        }
        for (int i = 1; i <= s1Len; i++) {
            score[0][i] = -deletion * i;
        }
    }

    private void fillMatrices() {
        fillScoreMatrix();
        fillBacktrack();
    }

    private void fillScoreMatrix() {
        for (int i = 1; i < s2Len + 1; i++) {
            for (int j = 1; j < s1Len + 1; j++) {
                final int up = score[i - 1][j] - deletion;
                final int left = score[i][j - 1] - deletion;

                final int adding = str2.charAt(i - 1) == str1.charAt(j - 1) ? match : -mismatch;
                final int diag = score[i - 1][j - 1] + adding;

                final int maxUpLeft = Math.max(up, left);
                score[i][j] = Math.max(maxUpLeft, diag);
            }
        }
    }

    private void fillBacktrack() {
        for (int i = 1; i < s2Len + 1; i++) {
            for (int j = 1; j < s1Len + 1; j++) {
                if (score[i][j] == score[i - 1][j] - deletion) {
                    backtrack[i - 1][j - 1] = DOWN;
                } else if (score[i][j] == score[i][j - 1] - deletion) {
                    backtrack[i - 1][j - 1] = RIGHT;
                } else {
                    backtrack[i - 1][j - 1] = DIAG;
                }
            }
        }
    }

    private void recovery() {
        int i = s1Len - 1;
        int j = s2Len - 1;

        final StringBuilder sb1 = new StringBuilder();
        final StringBuilder sb2 = new StringBuilder();

        while (i >= 0 && j >= 0) {
            if (backtrack[j][i] == DIAG) {
                sb1.append(str1.charAt(i));
                sb2.append(str2.charAt(j));
                i--;
                j--;
            } else if (backtrack[j][i] == DOWN) {
                sb1.append(DELETE);
                sb2.append(str2.charAt(j));
                j--;
            } else { // RIGHT
                sb1.append(str1.charAt(i));
                sb2.append(DELETE);
                i--;
            }
        }

        while (j < 0 && 0 <= i) {
            sb1.append(str1.charAt(i));
            sb2.append(DELETE);
            i--;
        }
        while (i < 0 && 0 <= j) {
            sb1.append(DELETE);
            sb2.append(str2.charAt(j));
            j--;
        }

        alignStr1 = sb1.reverse().toString();
        alignStr2 = sb2.reverse().toString();
    }

    void printScoreMatrix() {
        final StringBuilder sb = new StringBuilder();
        sb.append(' ').append('\t');
        sb.append(' ').append('\t');
        for (int j = 0; j < s1Len; j++) {
            sb.append(str1.charAt(j)).append('\t');
        }
        sb.append('\n');
        for (int i = 0; i < s2Len + 1; i++) {
            if (i != 0) {
                sb.append(str2.charAt(i - 1)).append('\t');
            } else {
                sb.append(' ').append('\t');
            }
            for (int j = 0; j < s1Len + 1; j++) {
                sb.append(score[i][j]).append('\t');
            }
            sb.append('\n');
        }
        System.out.println(sb);
    }

    void printBacktrack() {
        final StringBuilder sb = new StringBuilder();
        sb.append(' ').append('\t');
        for (int j = 0; j < s1Len; j++) {
            sb.append(str1.charAt(j)).append('\t');
        }
        sb.append('\n');
        for (int i = 0; i < s2Len; i++) {
            sb.append(str2.charAt(i)).append('\t');
            for (int j = 0; j < s1Len; j++) {
                sb.append(backtrack[i][j]).append('\t');
            }
            sb.append('\n');
        }
        System.out.println(sb);
    }
}
