package ru.myocr.util;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class PriceUtilTest {
    @Test
    public void isCorrect() throws Exception {
        assertTrue(PriceUtil.isCorrect("20.57"));
        assertFalse(PriceUtil.isCorrect("20.5.7"));
        assertTrue(PriceUtil.isCorrect("20.5"));
        assertTrue(PriceUtil.isCorrect("20."));
        assertTrue(PriceUtil.isCorrect("20"));
        assertTrue(PriceUtil.isCorrect(".20"));
        assertTrue(PriceUtil.isCorrect(".2"));
        assertFalse(PriceUtil.isCorrect("00920000000009009000008000600606"));
    }
}