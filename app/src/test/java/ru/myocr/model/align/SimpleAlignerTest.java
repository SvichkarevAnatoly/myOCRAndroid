package ru.myocr.model.align;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class SimpleAlignerTest {
    @Test
    public void scoreNucleotides() throws Exception {
        final SimpleAligner alignment = new SimpleAligner(1, 1);
        assertThat(alignment.align("ACT", "ATT"), is(1));
    }

    @Test
    public void alignNucleotides() throws Exception {
        final SimpleAligner alignment = new SimpleAligner(2, 1);
        final String v = "ACT";
        final String w = "ATT";
        alignment.align(v, w);
        assertThat(alignment.getAlignString1(), is(v));
        assertThat(alignment.getAlignString2(), is(w));
    }

    @Test
    public void scoreEnglishWords() throws Exception {
        final SimpleAligner alignment = new SimpleAligner(1, 1);
        final String v = "PLEASANTLY";
        final String w = "MEANLY";
        assertThat(alignment.align(v, w), is(0));
    }

    @Test
    public void alignEnglishWords() throws Exception {
        final SimpleAligner alignment = new SimpleAligner();
        final String v = "PLEASANTLY";
        final String w = "MEANLY";
        alignment.align(v, w);
        assertThat(alignment.getAlignString1(), is(v));
        assertThat(alignment.getAlignString2(), is("M-EA--N-LY"));
    }
}
