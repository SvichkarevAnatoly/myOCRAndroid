package ru.myocr.model.align;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class SimpleAlignmentRealDataTest {
    private final String ocrChicken = "[114ЛЕ Б/К0ЖИ К9РИН0Е 0ХЛ. НА П0187. ММ 187. И";
    final String expectedChickenAlignment = "ФИ--ЛЕ Б/КОЖИ КУРИНОЕ ОХЛ. НА ПО187. 14*1 187. 14";

    @Test
    public void chickenWord() throws Exception {
        final String ocr = "К9РИН0Е";
        final String real = "КУРИНОЕ";
        final SimpleAlignment alignment = new SimpleAlignment();

        assertThat(alignment.align(ocr, real), is(3));

        System.out.println(alignment.getAlignString1());
        System.out.println(alignment.getAlignString2());

        alignment.printScoreMatrix();
        alignment.printBacktrack();

        assertThat(alignment.getAlignString1(), is(ocr));
        assertThat(alignment.getAlignString2(), is(real));
    }

    @Test
    public void chickenFull() throws Exception {
        final String real = "ФИЛЕ Б/КОЖИ КУРИНОЕ ОХЛ. НА ПО187. 14*1 187. 14";
        final SimpleAlignment alignment = new SimpleAlignment();

        System.out.println("score = " + alignment.align(ocrChicken, real));
        assertThat(alignment.align(ocrChicken, real), is(19));

        // alignment.printScoreMatrix();
        // alignment.printBacktrack();

        System.out.println(alignment.getAlignString1());
        System.out.println(alignment.getAlignString2());

        final String expectedAlignStr1 = "[114ЛЕ Б/К0ЖИ К9РИН0Е 0ХЛ. НА П0187. ММ-- 187. И-";

        assertThat(alignment.getAlignString1(), is(expectedAlignStr1));
        assertThat(alignment.getAlignString2(), is(expectedChickenAlignment));
    }

    @Test
    public void findChicken() throws Exception {
        int maxScore = Integer.MIN_VALUE;
        String maxScoreFoodName = "empty";

        final SimpleAlignment alignment = new SimpleAlignment();
        for (String foodName : TestRealData.food30Names) {
            final int score = alignment.align(ocrChicken, foodName);
            System.out.println("score = " + score);
            System.out.println(alignment.getAlignString2());
            System.out.println(alignment.getAlignString1());
            System.out.println();

            if (score > maxScore) {
                maxScore = score;
                maxScoreFoodName = foodName;
            }
        }

        System.out.println("--------------------------------------");
        final int score = alignment.align(ocrChicken, maxScoreFoodName);
        System.out.println("score = " + score);
        final String maxScoreAlignment = alignment.getAlignString2();
        System.out.println(maxScoreAlignment);
        System.out.println(alignment.getAlignString1());
        assertThat(maxScoreAlignment, is(expectedChickenAlignment));
    }
}
