package com.example.kit;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    // Run at least once
    public void createItem() {
        // Create item to test
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.itemNameDisplay)).perform(ViewActions.typeText("JUnit Test Item"));
        onView(withId(R.id.itemNameDisplay)).perform(closeSoftKeyboard());

        onView(withId(R.id.itemDateDisplay)).perform(ViewActions.typeText("10/10/1010"));
        onView(withId(R.id.itemDateDisplay)).perform(closeSoftKeyboard());

        onView(withId(R.id.itemValueDisplay)).perform(ViewActions.typeText("500"));
        onView(withId(R.id.itemValueDisplay)).perform(closeSoftKeyboard());

        onView(withId(R.id.floatingActionButton)).perform(click());
    }

    public void deleteItem(String name) {
        onView(withText(name)).perform(ViewActions.longClick());
        // onView(allOf(withId(R.id.itemCardView), hasDescendant(withText(name))));
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView), hasDescendant(withText(name)))))).perform(click());
    }

    @Test
    public void testChangeName() {
        // Test if changing the 'name' field is properly reflected
        createItem();
        onView(withText("JUnit Test Item")).perform(click());
        onView(withId(R.id.itemNameDisplay)).perform(ViewActions.clearText());
        onView(withId(R.id.itemNameDisplay)).perform(ViewActions.typeText("Changed JUnit Test Item"));
        onView(withId(R.id.itemNameDisplay)).perform(closeSoftKeyboard());
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withText("Changed JUnit Test Item")).check(matches(isDisplayed()));
    }


    @Test
    public void testChangeValue() {
        // Test if changing the 'value' field is properly reflected
        createItem();
        onView(withText("JUnit Test Item")).perform(click());
        onView(withId(R.id.itemValueDisplay)).perform(ViewActions.clearText());
        onView(withId(R.id.itemValueDisplay)).perform(ViewActions.typeText("9999"));
        onView(withId(R.id.itemValueDisplay)).perform(closeSoftKeyboard());
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withText("$9,999.00")).check(matches(isDisplayed()));
    }

    @Test
    public void testTotalValuation() {
        // Test if total valuation equals the sum of all values in item list
        // onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).check(matches((withText("$1.00"))));
        createItem();
    }

    @Test
    public void testDeleteItem() {
        // Test if an item is properly deleted
        createItem();
        deleteItem("JUnit Test Item");
    }
}
