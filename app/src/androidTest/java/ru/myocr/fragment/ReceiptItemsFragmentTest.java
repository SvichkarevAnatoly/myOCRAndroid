package ru.myocr.fragment;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import ru.myocr.R;
import ru.myocr.SleepTest;
import ru.myocr.api.ocr.Match;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.api.ocr.ParsedPrice;
import ru.myocr.api.ocr.ReceiptItemMatches;
import ru.myocr.fragment.support.ReceiptItemsFragmentTestRule;
import ru.myocr.preference.Preference;
import ru.myocr.preference.Settings;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ReceiptItemsFragmentTest extends SleepTest {

    private static final String BEST_MATCH = "BEST_MATCH";
    private static final String REAL_OCR = "REAL_OCR";
    private static final String PARSED_OCR_PRICE = "42.00";

    @Rule
    public ReceiptItemsFragmentTestRule fragmentTestRule = new ReceiptItemsFragmentTestRule();

    @Override
    public void setUp() {
        Settings.setCityId(1L);
        Preference.setShopId(1L);

        // Launch the activity to make the fragment visible
        fragmentTestRule.setData(generateOcrReceiptResponse());
        fragmentTestRule.launchActivity(null);
    }

    @Test
    public void viewOneItem() {
        onView(allOf(withText("Продукт"), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withText("Цена"), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.button_product_remove), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withText(BEST_MATCH), withId(R.id.text_product)))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.button_price_remove), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withText(PARSED_OCR_PRICE), withId(R.id.text_price)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void viewEditDialog() {
        // open dialog for editing
        onView(allOf(withText(BEST_MATCH), withId(R.id.text_product), isDisplayed()))
                .perform(click());

        // main field
        onView(allOf(withText(BEST_MATCH), withId(R.id.receipt_item_edit_text)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.receipt_item_edit_text_clear))
                .check(matches(isDisplayed()));

        onView(allOf(withText(PARSED_OCR_PRICE), withId(R.id.price_edit_text)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.price_edit_text_clear))
                .check(matches(isDisplayed()));

        // suggestions
        onView(withText("Другие варианты"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.receipt_item_matches))
                .check(matches(isDisplayed()));

        onView(allOf(withId(android.R.id.text1), withText(REAL_OCR)))
                .check(matches(isDisplayed()));

        onView(withId(android.R.id.button1))
                .check(matches(isDisplayed()));
    }

    private OcrReceiptResponse generateOcrReceiptResponse() {
        final List<Match> matches = Collections.singletonList(new Match(BEST_MATCH, 5));
        final List<ReceiptItemMatches> itemMatches =
                Collections.singletonList(new ReceiptItemMatches(REAL_OCR, matches));
        final List<ParsedPrice> prices =
                Collections.singletonList(new ParsedPrice(PARSED_OCR_PRICE, 4200));
        return new OcrReceiptResponse(itemMatches, prices);
    }
}