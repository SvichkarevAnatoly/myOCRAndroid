package ru.myocr.activity;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.myocr.R;
import ru.myocr.preference.Settings;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static ru.myocr.OcrMatcher.childAtPosition;
import static ru.myocr.OcrMatcher.isListEmpty;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SettingsTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Before
    public void setUp() throws Exception {
        // temporary solution for parallel test
        sleep(200);

        goToSettings();
    }

    @Test
    public void mainOptions() throws InterruptedException {
        // asserts
        // back btn
        onView(withContentDescription("Navigate up"))
                .check(matches(isDisplayed()));

        onView(withText("Настройки"))
                .check(matches(isDisplayed()));

        onView(allOf(withText("Основные настройки"), withId(android.R.id.title)))
                .check(matches(isDisplayed()));

        onView(allOf(withText("Город"), withId(android.R.id.title)))
                .check(matches(isDisplayed()));

        onView(allOf(withText("Нажмите чтобы выбрать город"), withId(android.R.id.summary)))
                .check(matches(isDisplayed()));

        onView(allOf(withText("Использовать локальный сервер"), withId(android.R.id.title)))
                .check(matches(isDisplayed()));

        onView(instanceOf(android.widget.Switch.class))
                .check(matches(allOf(isDisplayed(), not(isChecked()))));

        onView(allOf(withText("Адрес удаленного сервера"), withId(android.R.id.title)))
                .check(matches(isDisplayed()));

        onView(allOf(withText("Адрес локального сервера"), withId(android.R.id.title)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void viewCityOption() throws InterruptedException {
        // select city
        onView(allOf(childAtPosition(withId(android.R.id.list), 1), isDisplayed()))
                .perform(click());

        onView(allOf(withText("Город"), instanceOf(android.widget.TextView.class)))
                .check(matches(isDisplayed()));

        onView(instanceOf(android.widget.ListView.class))
                .check(matches(not(isListEmpty())));

        onView(withId(android.R.id.button2))
                .check(matches(isDisplayed()));
    }

    @Test
    public void selectAnotherCity() throws InterruptedException {
        chooseCity("Nsk");
        final long NskId = Settings.getCityId();

        chooseCity("Spb");
        final long SpbId = Settings.getCityId();
        assertThat(NskId, is(not(equalTo(SpbId))));

        // return Nsk
        chooseCity("Spb");
    }

    @Test
    public void useLocalServerSwitch() throws InterruptedException {
        onView(allOf(childAtPosition(withId(android.R.id.list), 2), isDisplayed()))
                .perform(click());

        onView(allOf(IsInstanceOf.instanceOf(android.widget.Switch.class), isDisplayed()))
                .check(matches(isChecked()));

        onView(allOf(childAtPosition(withId(android.R.id.list), 2), isDisplayed()))
                .perform(click());

        onView(allOf(IsInstanceOf.instanceOf(android.widget.Switch.class), isDisplayed()))
                .check(matches(isNotChecked()));
    }

    private void goToSettings() {
        onView(allOf(withContentDescription("Open navigation drawer"),
                withParent(withId(R.id.toolbar)), isDisplayed()))
                .perform(click());

        onView(allOf(withId(R.id.design_menu_item_text),
                withText("Настройки"), isDisplayed()))
                .perform(click());
    }

    private void chooseCity(String cityName) {
        // select choose city dialog
        onView(allOf(childAtPosition(withId(android.R.id.list), 1), isDisplayed()))
                .perform(click());
        // click on city
        onView(allOf(withId(android.R.id.text1), withText(cityName), isDisplayed()))
                .perform(click());
    }
}
