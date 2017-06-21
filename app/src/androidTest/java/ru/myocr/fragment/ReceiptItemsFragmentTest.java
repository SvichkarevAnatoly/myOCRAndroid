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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ReceiptItemsFragmentTest extends SleepTest {

    @Rule
    public ReceiptItemsFragmentTestRule fragmentTestRule = new ReceiptItemsFragmentTestRule();

    @Override
    public void setUp() {
        Settings.setCityId(1L);
        Preference.setShopId(1L);
    }

    @Test
    public void viewOneItem() {
        // Launch the activity to make the fragment visible
        fragmentTestRule.setData(generateOcrReceiptResponse());
        fragmentTestRule.launchActivity(null);

        onView(allOf(withText("Продукт"), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withText("Цена"), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.button_product_remove), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withText("item1"), withId(R.id.text_product)))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.button_price_remove), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withText("42.00"), withId(R.id.text_price)))
                .check(matches(isDisplayed()));
    }

    private OcrReceiptResponse generateOcrReceiptResponse() {
        final List<Match> matches = Collections.singletonList(new Match("item1", 5));
        final List<ReceiptItemMatches> itemMatches =
                Collections.singletonList(new ReceiptItemMatches("itemN", matches));
        final List<ParsedPrice> prices =
                Collections.singletonList(new ParsedPrice("42.00", 4200));
        return new OcrReceiptResponse(itemMatches, prices);
    }
}