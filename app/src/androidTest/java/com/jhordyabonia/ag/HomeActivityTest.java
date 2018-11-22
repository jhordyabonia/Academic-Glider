package com.jhordyabonia.ag;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    @Rule
    public ActivityTestRule<HomeActivity> mActivityTestRule = new ActivityTestRule<>(HomeActivity.class);

    @Test
    public void homeActivityTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3598361);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DataInteraction frameLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.list),
                        childAtPosition(
                                withId(R.id.FrameLayout1),
                                0)))
                .atPosition(0);
        frameLayout.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3573796);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DataInteraction frameLayout2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.list),
                        childAtPosition(
                                withId(R.id.FrameLayout1),
                                0)))
                .atPosition(0);
        frameLayout2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3587591);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction linearLayout = onView(
                allOf(withContentDescription("Ver Apuntes, Navegar a casa"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.widget.ActionBarView")),
                                        childAtPosition(
                                                withClassName(is("com.android.internal.widget.ActionBarContainer")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3589140);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction linearLayout2 = onView(
                allOf(withContentDescription("INFORMÁTICA I, Navegar a casa"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.widget.ActionBarView")),
                                        childAtPosition(
                                                withClassName(is("com.android.internal.widget.ActionBarContainer")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3571574);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction linearLayout3 = onView(
                allOf(withContentDescription("Asignaturas, Open navigation drawer"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.widget.ActionBarView")),
                                        childAtPosition(
                                                withClassName(is("com.android.internal.widget.ActionBarContainer")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout3.perform(click());

        DataInteraction tableRow = onData(anything())
                .inAdapterView(allOf(withId(R.id.navigation_drawer),
                        childAtPosition(
                                withId(R.id.drawer_layout),
                                1)))
                .atPosition(0);
        tableRow.perform(click());

        ViewInteraction linearLayout4 = onView(
                allOf(withContentDescription("Inicio, Close navigation drawer"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.widget.ActionBarView")),
                                        childAtPosition(
                                                withClassName(is("com.android.internal.widget.ActionBarContainer")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout4.perform(click());

        ViewInteraction linearLayout5 = onView(
                allOf(withContentDescription("Inicio, Open navigation drawer"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.widget.ActionBarView")),
                                        childAtPosition(
                                                withClassName(is("com.android.internal.widget.ActionBarContainer")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout5.perform(click());

        DataInteraction tableRow2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.navigation_drawer),
                        childAtPosition(
                                withId(R.id.drawer_layout),
                                1)))
                .atPosition(1);
        tableRow2.perform(click());

        ViewInteraction linearLayout6 = onView(
                allOf(withContentDescription("Horarios, Open navigation drawer"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.widget.ActionBarView")),
                                        childAtPosition(
                                                withClassName(is("com.android.internal.widget.ActionBarContainer")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout6.perform(click());

        DataInteraction tableRow3 = onData(anything())
                .inAdapterView(allOf(withId(R.id.navigation_drawer),
                        childAtPosition(
                                withId(R.id.drawer_layout),
                                1)))
                .atPosition(3);
        tableRow3.perform(click());

        ViewInteraction linearLayout7 = onView(
                allOf(withContentDescription("Compañeros, Open navigation drawer"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.widget.ActionBarView")),
                                        childAtPosition(
                                                withClassName(is("com.android.internal.widget.ActionBarContainer")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout7.perform(click());

        DataInteraction tableRow4 = onData(anything())
                .inAdapterView(allOf(withId(R.id.navigation_drawer),
                        childAtPosition(
                                withId(R.id.drawer_layout),
                                1)))
                .atPosition(4);
        tableRow4.perform(click());

        ViewInteraction linearLayout8 = onView(
                allOf(withContentDescription("Conversaciones, Open navigation drawer"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.widget.ActionBarView")),
                                        childAtPosition(
                                                withClassName(is("com.android.internal.widget.ActionBarContainer")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout8.perform(click());

        DataInteraction tableRow5 = onData(anything())
                .inAdapterView(allOf(withId(R.id.navigation_drawer),
                        childAtPosition(
                                withId(R.id.drawer_layout),
                                1)))
                .atPosition(5);
        tableRow5.perform(click());

        ViewInteraction linearLayout9 = onView(
                allOf(withContentDescription("Grupos, Open navigation drawer"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.widget.ActionBarView")),
                                        childAtPosition(
                                                withClassName(is("com.android.internal.widget.ActionBarContainer")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout9.perform(click());

        DataInteraction tableRow6 = onData(anything())
                .inAdapterView(allOf(withId(R.id.navigation_drawer),
                        childAtPosition(
                                withId(R.id.drawer_layout),
                                1)))
                .atPosition(6);
        tableRow6.perform(click());

        ViewInteraction linearLayout10 = onView(
                allOf(withContentDescription("Asignaturas, Open navigation drawer"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.widget.ActionBarView")),
                                        childAtPosition(
                                                withClassName(is("com.android.internal.widget.ActionBarContainer")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout10.perform(click());

        DataInteraction tableRow7 = onData(anything())
                .inAdapterView(allOf(withId(R.id.navigation_drawer),
                        childAtPosition(
                                withId(R.id.drawer_layout),
                                1)))
                .atPosition(7);
        tableRow7.perform(click());

        DataInteraction tableRow8 = onData(anything())
                .inAdapterView(allOf(withId(R.id.navigation_drawer),
                        childAtPosition(
                                withId(R.id.drawer_layout),
                                1)))
                .atPosition(8);
        tableRow8.perform(click());

        ViewInteraction linearLayout11 = onView(
                allOf(withContentDescription("Información, Open navigation drawer"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.widget.ActionBarView")),
                                        childAtPosition(
                                                withClassName(is("com.android.internal.widget.ActionBarContainer")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout11.perform(click());

        DataInteraction tableRow9 = onData(anything())
                .inAdapterView(allOf(withId(R.id.navigation_drawer),
                        childAtPosition(
                                withId(R.id.drawer_layout),
                                1)))
                .atPosition(9);
        tableRow9.perform(click());

        DataInteraction textView = onData(anything())
                .inAdapterView(allOf(withId(R.id.gridview),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1)))
                .atPosition(14);
        textView.perform(click());

        DataInteraction textView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.gridview),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1)))
                .atPosition(0);
        textView2.perform(click());

        ViewInteraction checkBox = onView(
                allOf(withId(R.id.sound), withText("Sonidos:"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0),
                                2),
                        isDisplayed()));
        checkBox.perform(click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.chat), withContentDescription("Grupos"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.widget.ActionBarView")),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction linearLayout12 = onView(
                allOf(withContentDescription("Grupos, Open navigation drawer"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.widget.ActionBarView")),
                                        childAtPosition(
                                                withClassName(is("com.android.internal.widget.ActionBarContainer")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout12.perform(click());

        DataInteraction tableRow10 = onData(anything())
                .inAdapterView(allOf(withId(R.id.navigation_drawer),
                        childAtPosition(
                                withId(R.id.drawer_layout),
                                1)))
                .atPosition(2);
        tableRow10.perform(click());

        ViewInteraction linearLayout13 = onView(
                allOf(withContentDescription("Grupos, Open navigation drawer"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.widget.ActionBarView")),
                                        childAtPosition(
                                                withClassName(is("com.android.internal.widget.ActionBarContainer")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout13.perform(click());

        DataInteraction tableRow11 = onData(anything())
                .inAdapterView(allOf(withId(R.id.navigation_drawer),
                        childAtPosition(
                                withId(R.id.drawer_layout),
                                1)))
                .atPosition(3);
        tableRow11.perform(click());

    }

    private static Matcher<View> childAtPosition(
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
}
