package com.example.kit;

import static androidx.test.espresso.Espresso.onView;

import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.material.chip.Chip;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Helper function to create an item with specified fields
     * @param name name of item
     * @param date acquisition date of item
     * @param value value of item
     * @param desc description of item
     * @param make make of item
     * @param tag tag of item
     */
    public void createItem(String name, String date, String value, String desc, String make, String tag) {
        // Create item to test
        onView(withId(R.id.add_item_button)).perform(click());

        onView(withId(R.id.itemNameDisplay)).perform(ViewActions.typeText(name));
        onView(withId(R.id.itemNameDisplay)).perform(closeSoftKeyboard());

        if(desc != null) {
            onView(withId(R.id.itemDescriptionDisplay)).perform(ViewActions.typeText(desc));
            onView(withId(R.id.itemDescriptionDisplay)).perform(closeSoftKeyboard());
        }

        onView(withId(R.id.itemDateDisplay)).perform(replaceText(date));

        onView(withId(R.id.itemValueDisplay)).perform(ViewActions.typeText(value));
        onView(withId(R.id.itemValueDisplay)).perform(closeSoftKeyboard());

        if(make != null) {
            onView(withId(R.id.itemMakeDisplay)).perform(ViewActions.typeText(make));
            onView(withId(R.id.itemMakeDisplay)).perform(closeSoftKeyboard());
        }

        if(tag != null) {
            onView(withId(R.id.scrollView3)).perform(swipeUp());
            onView(withId(R.id.tagAutoCompleteField)).perform(click())
                    .inRoot(isPlatformPopup())
                    .perform(ViewActions.typeText(tag))
                    .perform(ViewActions.pressImeActionButton());
        }

        onView(withId(R.id.floatingActionButton)).perform(click());
    }

    /**
     * Helper function to create an item with values for all fields
     */
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
        onView(withId(R.id.scrollView3)).perform(swipeUp());
        onView(withId(R.id.tagAutoCompleteField)).perform(click())
                .inRoot(isPlatformPopup())
                .perform(ViewActions.typeText("Test Tag"))
                .perform(ViewActions.pressImeActionButton());

        // Create item
        onView(withId(R.id.floatingActionButton)).perform(click());
    }

    /**
     * US 01.01.01.01 + 01.01.01 + 01.01.01.02 + 02.01.01
     */
    @Test
    public void testCreateItem() {
        createItem("JUnit Test Item", "01/12/2023", "500", null, null, null);
        onView(allOf(withText("JUnit Test Item"), hasSibling(withText("Dec 1, 2023")), hasSibling(withText("$500.00")))).check(matches(isDisplayed()));

        // Delete item(s) after test
        onView(withText("JUnit Test Item")).perform(ViewActions.longClick());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("JUnit Test Item")))))).perform(click());
        onView(withId(R.id.delete_item_button)).perform(click());
    }

    /**
     * US 01.02.01 + 01.01.01.03 + 01.01.01.04 + 01.01.01.05 + 01.01.01.06 + 01.01.01.07
     *
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

        // Delete item(s) after test
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withText("JUnit Test Item")).perform(ViewActions.longClick());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("JUnit Test Item")))))).perform(click());
        onView(withId(R.id.delete_item_button)).perform(click());
    }

    /**
     * US 03.01.01 + 03.02.01
     */
    @Test
    public void testTags() {
        createItem("Test Item", "01/12/2023", "500", null, null, "Tag 1");
        onView(withText("Test Item")).perform(click());
        onView(withId(R.id.floatingActionButton2)).perform(click());
        onView(withId(R.id.scrollView3)).perform(swipeUp());
        onView(withId(R.id.tagAutoCompleteField)).perform(click())
                .inRoot(isPlatformPopup())
                .perform(ViewActions.typeText("Tag 2"))
                .perform(ViewActions.pressImeActionButton());
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withText("Tag 1")).check(matches(isDisplayed()));
        onView(withText("Tag 2")).check(matches(isDisplayed()));

        // Delete item(s) after test
        onView(withText("Test Item")).perform(ViewActions.longClick());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("Test Item")))))).perform(click());
        onView(withId(R.id.delete_item_button)).perform(click());
    }

    /**
     * US 01.03.01
     */
    @Test
    public void testChangeFields() {
        createItem("JUnit Test Item", "01/12/2023", "500", null, null, null);
        onView(withText("JUnit Test Item")).perform(click());
        onView(withId(R.id.floatingActionButton2)).perform(click());
        onView(withId(R.id.itemNameDisplay)).perform(clearText())
                .perform(ViewActions.typeText("Changed JUnit Test Item"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.itemDateDisplay)).perform(clearText())
                .perform(ViewActions.replaceText("06/06/2023"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.itemValueDisplay)).perform(clearText())
                .perform(ViewActions.typeText("250"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withText("Changed JUnit Test Item")).check(matches(isDisplayed()));
        onView(withText("Jun 6, 2023")).check(matches(isDisplayed()));
        onView(allOf(withText("$250.00"), hasSibling(withText("Changed JUnit Test Item"))))
                .check(matches(isDisplayed()));

        // Delete item(s) after test
        onView(withText("Changed JUnit Test Item")).perform(ViewActions.longClick());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("Changed JUnit Test Item")))))).perform(click());
        onView(withId(R.id.delete_item_button)).perform(click());
    }

    /**
     * US 02.03.01.01 + 02.03.01 + 02.03.01.02
     */
    @Test
    public void testDeleteItems() {
        createItem("JUnit Test Item", "01/12/2023", "500", null, null, null);
        createItem("JUnit Test Item 2", "01/12/2023", "500", null, null, null);
        onView(withText("JUnit Test Item")).perform(ViewActions.longClick());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView), hasDescendant(withText("JUnit Test Item")))))).perform(click());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView), hasDescendant(withText("JUnit Test Item 2")))))).perform(click());
        onView(withId(R.id.delete_item_button)).perform(click());
        onView(withText("JUnit Test Item")).check(doesNotExist());
        onView(withText("JUnit Test Item 2")).check(doesNotExist());
    }

    /**
     * US 02.02.01
     */
    @Test
    public void testTotalValue() {
        createItem("JUnit Test Item", "01/12/2023", "500", null, null, null);
        createItem("JUnit Test Item 2", "06/06/2023", "750", null, null, null);
        onView(withText("$1,250.00")).check(matches(isDisplayed()));

        // Delete item(s) after test
        onView(withText("JUnit Test Item")).perform(ViewActions.longClick());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("JUnit Test Item")))))).perform(click());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("JUnit Test Item 2")))))).perform(click());
        onView(withId(R.id.delete_item_button)).perform(click());
    }

    /**
     * US 03.03.01
     */
    @Test
    public void testAddTags() {
        createItem("JUnit Test Item", "01/12/2023", "500", null, null, null);
        onView(withText("JUnit Test Item")).perform(ViewActions.longClick());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("JUnit Test Item")))))).perform(click());
        onView(withId(R.id.add_tags_button)).perform(click());
        onView(withId(R.id.tagAutoCompleteField)).perform(click())
                .inRoot(isPlatformPopup())
                .perform(ViewActions.typeText("JUnit Test Tag"))
                .perform(ViewActions.pressImeActionButton());
        onView(withId(R.id.colorSplotch1)).perform(click());
        onView(withText("Add Tag(s)")).perform(click());
        onView(withText("JUnit Test Item")).perform(click());
        onView(withId(R.id.scrollView3)).perform(swipeUp());
        onView(withText("JUnit Test Tag")).check(matches(isDisplayed()));
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withText("JUnit Test Tag")).check(matches(isDisplayed()));

        // Delete item(s) after test
        onView(withText("JUnit Test Item")).perform(ViewActions.longClick());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("JUnit Test Item")))))).perform(click());
        onView(withId(R.id.delete_item_button)).perform(click());
    }

    /**
     * US 02.06.01.01
     */
    @Test
    public void testSearchKeyword() {
        createItem("Test", "01/12/2023", "500", null, null, null);
        createItem("JUnit", "01/12/2023", "500", null, null, null);
        onView(withId(R.id.searchBar)).perform(typeText("Test"))
                        .perform(closeSoftKeyboard());
        onView(allOf(withText("Test"), hasSibling(withText("Dec 1, 2023")))).check(matches(isDisplayed()));
        onView(withText("JUnit")).check(doesNotExist());

        // Delete item(s) after test
        onView(withId(R.id.searchBar)).perform(clearText());
        onView(allOf(withText("Test"), hasSibling(withText("Dec 1, 2023")))).perform(ViewActions.longClick());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("Test")))))).perform(click());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("JUnit")))))).perform(click());
        onView(withId(R.id.delete_item_button)).perform(click());
    }

    /**
     * US 02.05.01
     */
    @Test
    public void testFilterDate() {
        createItem("Test", "15/12/2023", "500", null, null, null);
        createItem("Test", "20/12/2023", "500", null, null, null);
        createItem("Test", "25/12/2023", "500", null, null, null);

        onView(withId(R.id.searchBar)).perform(swipeUp());
        onView(withId(R.id.dateStart)).perform(replaceText("14/12/2023"));
        onView(withId(R.id.dateEnd)).perform(replaceText("21/12/2023"));
        onView(withId(R.id.searchBar)).perform(swipeDown());

        onView(withText("Dec 15, 2023")).check(matches(isDisplayed()));
        onView(withText("Dec 20, 2023")).check(matches(isDisplayed()));
        onView(withText("Dec 25, 2023")).check(doesNotExist());

        // Delete item(s) after test
        onView(withId(R.id.searchBar)).perform(swipeUp());
        onView(withId(R.id.dateStart)).perform(clearText());
        onView(withId(R.id.dateEnd)).perform(clearText());
        onView(withId(R.id.searchBar)).perform(swipeDown());

        onView(withText("Dec 15, 2023")).perform(ViewActions.longClick());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("Dec 15, 2023")))))).perform(click());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("Dec 20, 2023")))))).perform(click());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("Dec 25, 2023")))))).perform(click());
        onView(withId(R.id.delete_item_button)).perform(click());
    }

    /**
     * US 02.07.01
     */
    @Test
    public void testFilterMake() {
        createItem("Test 1", "15/12/2023", "500", null, "Toyota", null);
        createItem("Test 2", "20/12/2023", "500", null, "Hyundai", null);

        onView(withId(R.id.searchBar)).perform(swipeUp());
        onView(withId(R.id.makeAutoCompleteField)).perform(click())
                .inRoot(isPlatformPopup())
                .perform(ViewActions.typeText("Hyundai"))
                .perform(ViewActions.pressImeActionButton());
        onView(withId(R.id.searchBar)).perform(swipeDown());

        onView(withText("Test 2")).check(matches(isDisplayed()));
        onView(withText("Test 1")).check(doesNotExist());

        // Delete item(s) after test
        onView(withId(R.id.searchBar)).perform(swipeUp());
        onView(withText("Hyundai")).perform(new ClickCloseIconAction());
        onView(withId(R.id.searchBar)).perform(swipeDown());

        onView(withText("Test 2")).perform(ViewActions.longClick());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("Test 2")))))).perform(click());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("Test 1")))))).perform(click());
        onView(withId(R.id.delete_item_button)).perform(click());
    }

    /**
     * US 02.06.01
     */
    @Test
    public void testFilterDescription() {
        createItem("Test 1", "15/12/2023", "500", "Test 1 Desc", null, null);
        createItem("Test 2", "20/12/2023", "500", "Test 2 Desc", null, null);

        onView(withId(R.id.searchBar)).perform(typeText("Test 2 Desc"))
                .perform(closeSoftKeyboard());

        onView(withText("Test 2")).check(matches(isDisplayed()));
        onView(withText("Test 1")).check(doesNotExist());

        // Delete item(s) after test
        onView(withId(R.id.searchBar)).perform(clearText());

        onView(withText("Test 2")).perform(ViewActions.longClick());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("Test 2")))))).perform(click());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("Test 1")))))).perform(click());
        onView(withId(R.id.delete_item_button)).perform(click());
    }

    /**
     * US 03.05.01
     */
    @Test
    public void testFilterTags() {
        createItem("Test 1", "15/12/2023", "500", null, null, "Tag 1");
        createItem("Test 2", "20/12/2023", "500", null, null, "Tag 2");

        onView(withId(R.id.searchBar)).perform(swipeUp());
        onView(withId(R.id.tagAutoCompleteField)).perform(click())
                .inRoot(isPlatformPopup())
                .perform(ViewActions.typeText("Tag 2"))
                .perform(ViewActions.pressImeActionButton());
        onView(withId(R.id.searchBar)).perform(swipeDown());

        onView(withText("Test 2")).check(matches(isDisplayed()));
        onView(withText("Test 1")).check(doesNotExist());

        // Delete item(s) after test
        onView(withId(R.id.searchBar)).perform(swipeUp());
        onView(allOf(withText("Tag 2"), withParent(withId(R.id.tagsFilter))))
                .perform(new ClickCloseIconAction());
        onView(withId(R.id.searchBar)).perform(swipeDown());

        onView(withText("Test 2")).perform(ViewActions.longClick());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("Test 2")))))).perform(click());
        onView(allOf(withId(R.id.checkBox), hasSibling(allOf(withId(R.id.itemCardView),
                hasDescendant(withText("Test 1")))))).perform(click());
        onView(withId(R.id.delete_item_button)).perform(click());
    }
}
