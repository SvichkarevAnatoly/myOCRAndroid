package ru.myocr.activity;


import android.support.test.espresso.ViewInteraction;
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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ShopAddingTest extends SleepTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Test
    public void checkVisualElements() {
        ViewInteraction floatingActionButton = onView(
                allOf(withClassName(is("com.github.clans.fab.FloatingActionButton")),
                        withParent(withId(R.id.floatingMenu)),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.fabAddShop),
                        withParent(withId(R.id.floatingMenu)),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        onView(withId(R.id.city_spinner))
                .check(matches(isDisplayed()));

        onView(withId(R.id.edit_text_shop_name))
                .check(matches(isDisplayed()));

        onView(withId(R.id.add_button))
                .check(matches(isDisplayed()));
    }
}
