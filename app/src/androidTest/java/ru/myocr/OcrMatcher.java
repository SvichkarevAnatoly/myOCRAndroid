package ru.myocr;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ListView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class OcrMatcher {
    public static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public static Matcher<View> isListEmpty() {

        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(final View view) {
                if (!(view instanceof ListView)) {
                    return false;
                }
                return ((ListView) view).getCount() == 0;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("ListView should be empty");
            }
        };
    }
}