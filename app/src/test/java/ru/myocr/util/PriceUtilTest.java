package ru.myocr.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class PriceUtilTest {
    @Test
    public void isCorrect() throws Exception {
        assertTrue(PriceUtil.isCorrect("20.57"));
        assertTrue(PriceUtil.isCorrect("20.5"));
        assertTrue(PriceUtil.isCorrect("20"));

        assertFalse(PriceUtil.isCorrect("20."));
        assertFalse(PriceUtil.isCorrect(".20"));
        assertFalse(PriceUtil.isCorrect(".2"));
        assertFalse(PriceUtil.isCorrect("20.5.7"));
        assertFalse(PriceUtil.isCorrect("20.5798"));
        assertFalse(PriceUtil.isCorrect(".5798"));
        assertFalse(PriceUtil.isCorrect("00920000000009009000008000600606"));
        assertFalse(PriceUtil.isCorrect("0"));
        assertFalse(PriceUtil.isCorrect("0.00"));
        assertFalse(PriceUtil.isCorrect("-5"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIntValueWithIncorrectPrice() throws Exception {
        PriceUtil.getIntValue("20.5.7");
    }

    @Test
    public void getIntValueWithoutDotPrice() throws Exception {
        assertEquals(500, PriceUtil.getIntValue("5"));
        assertEquals(2500, PriceUtil.getIntValue("25"));
    }

    @Test
    public void getIntValueWithDotPrice() throws Exception {
        assertEquals(500, PriceUtil.getIntValue("5.00"));
        assertEquals(500, PriceUtil.getIntValue("5.0"));
        assertEquals(520, PriceUtil.getIntValue("5.2"));
        assertEquals(20, PriceUtil.getIntValue("0.2"));
        assertEquals(50020, PriceUtil.getIntValue("500.2"));
    }
}