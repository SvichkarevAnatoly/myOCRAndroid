package ru.myocr.fragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import ru.myocr.R;
import ru.myocr.api.ocr.Match;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.api.ocr.ParsedPrice;
import ru.myocr.api.ocr.ReceiptItemMatches;
import ru.myocr.preference.Preference;
import ru.myocr.preference.Settings;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class ReceiptItemsFragmentTest {

    @Rule
    public ReceiptItemsFragmentTestRule fragmentTestRule = new ReceiptItemsFragmentTestRule();

    @Before
    public void setUp() throws Exception {
        Thread.sleep(200);

        Settings.setCityId(1L);
        Preference.setShopId(1L);
    }

    @Test
    public void viewOcrResults() {
        // Launch the activity to make the fragment visible
        fragmentTestRule.setData(generateOcrReceiptResponse());
        fragmentTestRule.launchActivity(null);

        onView(allOf(withText("Продукт"), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withText("Цена"), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.button_product_remove), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withText("pizZza"), withId(R.id.text_product)))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.button_price_remove), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withText("45.56g"), withId(R.id.text_price)))
                .check(matches(isDisplayed()));
    }

    private OcrReceiptResponse generateOcrReceiptResponse() {
        final ArrayList<ReceiptItemMatches> itemMatches = new ArrayList<>();
        final ArrayList<Match> matches = new ArrayList<>();
        matches.add(new Match("pizZza", 5));
        itemMatches.add(new ReceiptItemMatches("pizza", matches));
        final ArrayList<ParsedPrice> prices = new ArrayList<>();
        prices.add(new ParsedPrice("45.56g"));
        return new OcrReceiptResponse(itemMatches, prices);
    }
}