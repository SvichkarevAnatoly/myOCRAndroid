package ru.myocr;

import org.junit.Before;

public class SleepTest {
    @Before
    public void init() throws Exception {
        // temporary solution for parallel test
        Thread.sleep(200);

        setUp();
    }

    public void setUp() {
    }
}
