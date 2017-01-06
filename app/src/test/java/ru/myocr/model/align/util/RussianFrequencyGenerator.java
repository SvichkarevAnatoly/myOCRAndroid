package ru.myocr.model.align.util;


import java.util.Random;

public class RussianFrequencyGenerator {
    static final char[] ALPHABET =
            "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray();
    static final int[] FREQUENCIES = new int[]{
            7998, 1592, 4533, 1687, 2977,
            8483, 13, 94, 1641, 7367,
            1208, 3486, 4343, 3203, 67,
            10983, 2804, 4746, 5473, 6318,
            2615, 267, 966, 486, 145,
            718, 361, 37, 1898, 1735,
            331, 639, 2001,
    };
    private final int[] cumulativeFrequency;
    private Random random;

    public RussianFrequencyGenerator(long seed) {
        random = new Random(seed);
        cumulativeFrequency = getCumulativeFrequency();
    }

    public String getString(int length) {
        return null;
    }

    public char getChar() {
        final int frequencySum = cumulativeFrequency[cumulativeFrequency.length - 1];
        final int cumFreqChar = random.nextInt(frequencySum);

        for (int i = 0; i < cumulativeFrequency.length; i++) {
            if (cumFreqChar < cumulativeFrequency[i]) {
                return ALPHABET[i];
            }
        }

        return 'О';
    }

    int[] getCumulativeFrequency() {
        final int[] cumulativeFrequency = new int[FREQUENCIES.length];
        cumulativeFrequency[0] = FREQUENCIES[0];
        for (int i = 1; i < FREQUENCIES.length; i++) {
            cumulativeFrequency[i] = cumulativeFrequency[i - 1] + FREQUENCIES[i];
        }

        return cumulativeFrequency;
    }

    int getFrequency(int index) {
        return FREQUENCIES[index];
    }
}
