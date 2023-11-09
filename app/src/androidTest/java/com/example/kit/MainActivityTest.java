package com.example.kit;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isNotSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.util.Log;

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

    @Test
    public void testCreateItem() {
        // Test if an item is properly created. Check field values
    }

    @Test
    public void testAddTag() {
        // Test if a tag gets properly added to an item
    }

    @Test
    public void testChangeName() {
        // Test if changing the 'name' field is properly reflected
    }

    @Test
    public void testChangeDate() {
        // Test if changing the 'name' field is properly reflected
    }

    @Test
    public void testChangeValue() {
        // Test if changing the 'name' field is properly reflected
    }

    @Test
    public void testTotalValuation() {
        // Test if total valuation equals the sum of all values in item list
        // onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).check(matches((withText("$1.00"))));
    }

    @Test
    public void testDeleteItem() {
        // Test if an item is properly deleted
    }
}
