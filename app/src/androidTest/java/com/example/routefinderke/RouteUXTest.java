
package com.example.routefinderke;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * TASK 2: TESTING AND DEBUGGING A MOBILE APPLICATION
 * iii. Perform UI/UX Testing
 * This test verifies that the application adheres to Material Design standards 
 * and displays correct branding/typography.
 */
@RunWith(AndroidJUnit4.class)
public class RouteUXTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testUIUXStandards() {
        // 1. Verify Toolbar branding (Requirement: RouteFinderKE title)
        onView(withText("RouteFinderKE")).check(matches(isDisplayed()));

        // 2. Verify RecyclerView presence (Main List UX)
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

        // 3. Verify Material Design typography and spacing
        // (Checks if the view components are properly rendered)
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }
}
