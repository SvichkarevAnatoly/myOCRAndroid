package ru.myocr.activity;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.myocr.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SettingsTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Test
    public void firstOpen() {
        // go to app settings
        onView(allOf(withContentDescription("Open navigation drawer"),
                withParent(withId(R.id.toolbar)), isDisplayed()))
                .perform(click());

        onView(allOf(withId(R.id.design_menu_item_text),
                withText("Настройки"), isDisplayed()))
                .perform(click());

        // asserts
        // back btn
        onView(withContentDescription("Navigate up"))
                .check(matches(isDisplayed()));

        onView(withText("Настройки"))
                .check(matches(isDisplayed()));

        onView(allOf(withId(android.R.id.title), withText("Основные настройки")))
                .check(matches(isDisplayed()));

        onView(allOf(withId(android.R.id.title), withText("Город")))
                .check(matches(isDisplayed()));

        onView(allOf(withId(android.R.id.summary), withText("Нажмите чтобы выбрать город")))
                .check(matches(isDisplayed()));

        onView(allOf(withId(android.R.id.title), withText("Использовать локальный сервер")))
                .check(matches(isDisplayed()));

        onView(IsInstanceOf.instanceOf(android.widget.Switch.class))
                .check(matches(allOf(isDisplayed(), not(isChecked()))));

        onView(allOf(withId(android.R.id.title), withText("Адрес удаленного сервера")))
                .check(matches(isDisplayed()));

        onView(allOf(withId(android.R.id.title), withText("Адрес локального сервера")))
                .check(matches(isDisplayed()));
    }
}
