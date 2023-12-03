package com.example.kit;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;
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

    public void createItem() {
        // Create item to test
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.itemNameDisplay)).perform(ViewActions.typeText("JUnit Test Item"));
        onView(withId(R.id.itemNameDisplay)).perform(closeSoftKeyboard());

        onView(withId(R.id.itemDateDisplay)).perform(replaceText("02/12/2023"));

        onView(withId(R.id.itemValueDisplay)).perform(ViewActions.typeText("500"));
        onView(withId(R.id.itemValueDisplay)).perform(closeSoftKeyboard());

        onView(withId(R.id.floatingActionButton)).perform(click());
    }

    public void createItemAllFields() {
        onView(withId(R.id.add_item_button)).perform(click());

        // Add title
        onView(withId(R.id.itemNameDisplay)).perform(ViewActions.typeText("JUnit Test Item"));
        onView(withId(R.id.itemNameDisplay)).perform(closeSoftKeyboard());

        // Add description
        onView(withId(R.id.itemDescriptionDisplay)).perform(ViewActions.typeText("Test Description"));
        onView(withId(R.id.itemDescriptionDisplay)).perform(closeSoftKeyboard());

        // Add comment
        onView(withId(R.id.itemCommentDisplay)).perform(ViewActions.typeText("Test Comment"));
        onView(withId(R.id.itemCommentDisplay)).perform(closeSoftKeyboard());

        // Add date
        onView(withId(R.id.itemDateDisplay)).perform(replaceText("06/06/2023"));

        // Add value
        onView(withId(R.id.itemValueDisplay)).perform(ViewActions.typeText("500"));
        onView(withId(R.id.itemValueDisplay)).perform(closeSoftKeyboard());

        // Add make
        onView(withId(R.id.itemMakeDisplay)).perform(ViewActions.typeText("Test Make"));
        onView(withId(R.id.itemMakeDisplay)).perform(closeSoftKeyboard());

        // Add model
        onView(withId(R.id.itemModelDisplay)).perform(ViewActions.typeText("Test Model"));
        onView(withId(R.id.itemModelDisplay)).perform(closeSoftKeyboard());

        // Add serial number
        onView(withId(R.id.itemSerialNumberDisplay)).perform(ViewActions.typeText("Test Serial Number"));
        onView(withId(R.id.itemSerialNumberDisplay)).perform(closeSoftKeyboard());

        // Add tag
        onView(withId(R.id.tagAutoCompleteField)).perform(click())
                .inRoot(isPlatformPopup())
                .perform(ViewActions.typeText("Test Tag"))
                .perform(ViewActions.pressImeActionButton());

        // Create item
        onView(withId(R.id.floatingActionButton)).perform(click());
    }

    public void deleteItem(String name) {
        onView(withText(name)).perform(ViewActions.longClick());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView), hasDescendant(withText(name)))))).perform(click());
        onView(withId(R.id.delete_item_button)).perform(click());
    }

    /**
     * US 01.01.01.01 + 01.01.01 + 01.01.01.02 + 02.01.01
     */
    @Test
    public void testCreateItem() {
        createItem();
        onView(allOf(withText("JUnit Test Item"), hasSibling(withText("Feb 12, 2023")), hasSibling(withText("$500.00")))).check(matches(isDisplayed()));
    }

    /**
     * US 01.02.01 + 01.01.01.03 + 01.01.01.04 + 01.01.01.05 + 01.01.01.06 + 01.01.01.07
     * US 03.01.01 + 03.02.01
     */
    @Test
    public void testCreateItemAllFields() {
        createItemAllFields();
        onView(withText("JUnit Test Item")).perform(click());
        onView(withText("JUnit Test Item")).check(matches(isDisplayed()));
        onView(withText("Test Description")).check(matches(isDisplayed()));
        onView(withText("Test Comment")).check(matches(isDisplayed()));
        onView(withText("06/06/2023")).check(matches(isDisplayed()));
        onView(withText("500.00")).check(matches(isDisplayed()));
        onView(withText("Test Make")).check(matches(isDisplayed()));
        onView(withText("Test Model")).check(matches(isDisplayed()));
        onView(withText("Test Serial Number")).check(matches(isDisplayed()));
        onView(withText("Test Tag")).check(matches(isDisplayed()));
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
        deleteItem("Changed JUnit Test Item");
    }

    @Test
    public void testChangeDate() {
        createItem();
        onView(withText("JUnit Test Item")).perform(click());
        onView(withId(R.id.itemDateDisplay)).perform(ViewActions.clearText());
        onView(withId(R.id.itemDateDisplay)).perform(ViewActions.typeText("11/11/1111"));
        onView(withId(R.id.itemDateDisplay)).perform(closeSoftKeyboard());
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(allOf(withText("Nov 11, 1111 12:00:00 AM"), hasSibling(withText("JUnit Test Item")))).check(matches(isDisplayed()));
        deleteItem("JUnit Test Item");
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
        onView(allOf(withText("$9,999.00"), hasSibling(withText("JUnit Test Item")))).check(matches(isDisplayed()));
        deleteItem("JUnit Test Item");
    }

    /**
     * US 02.03.01.02
     */
    @Test
    public void testDeleteItem() {
        // Test if an item is properly deleted
        createItem();
        deleteItem("JUnit Test Item");
        onView(withText("JUnit Test Item")).check(doesNotExist());
    }
}
