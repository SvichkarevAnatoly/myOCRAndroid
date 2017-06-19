package ru.myocr.activity;


import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.myocr.R;
import ru.myocr.SleepTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainTest extends SleepTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Test
    public void openLastAddedReceiptItem() {
        onView(withId(R.id.list)).perform(actionOnItemAtPosition(0, click()));

        onView(allOf(withText(startsWith("Чек от")), isDisplayed()))
                .check(matches(isDisplayed()));

        // back btn
        onView(allOf(withContentDescription("Navigate up"), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.action_delete), withContentDescription("Удалить"), isDisplayed()))
                .check(matches(withText("")));

        onView(allOf(withId(R.id.action_edit), withContentDescription("Редактировать"), isDisplayed()))
                .check(matches(withText("")));

        onView(allOf(withText("Продукты"), isDisplayed()))
                .check(matches(withText("Продукты")));

        onView(allOf(withId(R.id.market_name), isDisplayed()))
                .check(matches(withText(not(isEmptyString()))));

        onView(allOf(withId(R.id.date), isDisplayed()))
                .check(matches(withText(not(isEmptyString()))));

        // scroll to bottom for display
        onView(withText("ИТОГО")).perform(ViewActions.scrollTo());

        onView(withText("ИТОГО"))
                .check(matches(allOf(withText("ИТОГО"), isDisplayed())));

        onView(withId(R.id.sum))
                .check(matches(allOf(withText(startsWith("=")), isDisplayed())));
    }

}
