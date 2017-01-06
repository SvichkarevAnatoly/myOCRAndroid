package ru.myocr.align.util;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static ru.myocr.align.util.RussianFrequencyGenerator.ALPHABET;
import static ru.myocr.align.util.RussianFrequencyGenerator.FREQUENCIES;

public class RussianFrequencyGeneratorTest {
    @Test
    public void getCumulativeFrequency() throws Exception {
        final RussianFrequencyGenerator generator = new RussianFrequencyGenerator(0);
        final int[] cumulativeFrequency = generator.getCumulativeFrequency();

        printCumulativeFrequency(cumulativeFrequency);

        assertThat(cumulativeFrequency[0], is(generator.getFrequency(0)));
        assertThat(cumulativeFrequency[1], is(generator.getFrequency(0) + generator.getFrequency(1)));
    }

    @Test
    public void getCharacter() throws Exception {
        final WordGenerator generator = new RussianFrequencyGenerator(0);
        final String alphabetStr = new String(ALPHABET);

        final int[] alphabetCounters = new int[ALPHABET.length];
        for (int i = 0; i < 100000; i++) {
            final char randChar = generator.getChar();
            final int indexOfChar = alphabetStr.indexOf(randChar);
            alphabetCounters[indexOfChar]++;
        }

        printAlphabetCounters(alphabetCounters);

        assertThat(maxChar(alphabetCounters), is('О'));
        assertThat(minChar(alphabetCounters), is('Ё'));
    }

    @Test
    public void getWord() throws Exception {
        final WordGenerator generator = new RussianFrequencyGenerator(0);

        final int[] alphabetCounters = new int[ALPHABET.length];
        for (int i = 0; i < 10000; i++) {
            final String randWord = generator.getWord(6);
            countCharInWord(randWord, alphabetCounters);
        }

        printAlphabetCounters(alphabetCounters);

        assertThat(maxChar(alphabetCounters), is('О'));
        assertThat(minChar(alphabetCounters), is('Ё'));
    }

    @Test
    public void getString() throws Exception {
        final int averageWordLength = 4;
        final WordGenerator generator = new RussianFrequencyGenerator(0);

        int wordsCounter = 0;
        int cumulativeWordsLength = 0;
        for (int i = 0; i < 1000; i++) {
            final String randString = generator.getString(10, averageWordLength);
            wordsCounter += countWords(randString);
            cumulativeWordsLength += wordsLength(randString);
        }

        final int actualAverageWordLength = cumulativeWordsLength / wordsCounter;
        assertTrue(actualAverageWordLength < averageWordLength);
    }

    private int wordsLength(String string) {
        return string.length() - countWords(string) + 1;
    }

    private int countWords(String string) {
        return string.split("\\s+").length;
    }

    private void countCharInWord(String word, int[] alphabetCounters) {
        final String alphabetStr = new String(ALPHABET);

        for (int i = 0; i < word.length(); i++) {
            final char wordChar = word.charAt(i);
            final int indexOfChar = alphabetStr.indexOf(wordChar);
            alphabetCounters[indexOfChar]++;
        }
    }

    private void printCumulativeFrequency(int[] cumulativeFrequency) {
        for (int i = 0; i < cumulativeFrequency.length; i++) {
            System.out.println(ALPHABET[i] + " - " +
                    FREQUENCIES[i] + " - " + cumulativeFrequency[i]);
        }
    }

    private void printAlphabetCounters(int[] alphabetCounters) {
        for (int i = 0; i < alphabetCounters.length; i++) {
            System.out.println(ALPHABET[i] + " - " + alphabetCounters[i]);
        }
    }

    private char maxChar(int[] alphabetCounters) {
        int max = 0;
        int maxIndex = 0;

        for (int i = 0; i < alphabetCounters.length; i++) {
            if (alphabetCounters[i] > max) {
                max = alphabetCounters[i];
                maxIndex = i;
            }
        }

        return ALPHABET[maxIndex];
    }

    private char minChar(int[] alphabetCounters) {
        int min = Integer.MAX_VALUE;
        int minIndex = 0;

        for (int i = 0; i < alphabetCounters.length; i++) {
            if (alphabetCounters[i] < min) {
                min = alphabetCounters[i];
                minIndex = i;
            }
        }

        return ALPHABET[minIndex];
    }
}
