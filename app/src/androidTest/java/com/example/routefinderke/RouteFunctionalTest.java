//conducting functional testing on the app.task 2.
package com.example.routefinderke;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.contrib.RecyclerViewActions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * TASK 2: TESTING AND DEBUGGING A MOBILE APPLICATION
 * ii. Conduct Functional Testing
 * This test simulates a user interaction: clicking a route and verifying the details.
 */
@RunWith(AndroidJUnit4.class)
public class RouteFunctionalTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testClickRouteOpensDetails() {
        // 1. Verify RecyclerView is displayed
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

        // 2. Click on the first item in the list (Route 7C)
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // 3. Verify DetailActivity is opened by checking for the Start Point text
        // Based on Route Catalog: Route 7C starts at CBD
        onView(withId(R.id.tvDetailStart))
                .check(matches(withText(containsString("CBD"))));

        // 4. Verify Destination is correct
        onView(withId(R.id.tvDetailDest))
                .check(matches(withText(containsString("Pipeline"))));
        
        // 5. Verify Fare is displayed
        onView(withId(R.id.tvDetailFare))
                .check(matches(isDisplayed()));
    }
}
