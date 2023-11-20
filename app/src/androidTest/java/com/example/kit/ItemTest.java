package com.example.kit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.kit.data.Item;
import com.google.firebase.Timestamp;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class ItemTest {
    public Item mockItem1() {
        Item item = new Item();
        item.setName("test name");
        item.setDescription("This is for testing");
        item.setComment("this is for testing");
        item.setValue("1000");
        item.setAcquisitionDate(new Timestamp(new Date()));
        item.setMake("JUNIT");
        item.setModel("tester90000");
        item.setSerialNumber("1243124124");
        item.attachID("19191919");

        return item;
    }

    public Item mockItem2() {
        Item item = new Item();
        item.setName("test item 2");
        item.setDescription("This is for testing");
        item.setComment("this is for testing");
        item.setValue("1500");
        item.setAcquisitionDate(new Timestamp(new Date()));
        item.setMake("JUNIT");
        item.setModel("tester90020");
        item.setSerialNumber("1243124124");
        item.attachID("28282828");

        return item;
    }

    @Test
    public void testItemRename() {
        Item item = mockItem1();
        assertEquals("test name", item.getName());
        item.setName("new name");
        assertEquals("new name", item.getName());
    }

    @Test
    public void testAddTags() {
        Item item = mockItem1();
        assertFalse(item.getTags().contains("test tag"));
        item.addTag("test tag");
        assertTrue(item.getTags().contains("test tag"));
    }

    @Test
    public void testRemoveTags() {
        Item item = mockItem1();
        String tag = "test tag";
        item.addTag(tag);
        assertTrue(item.getTags().contains(tag));
        item.removeTag(tag);
        assertFalse(item.getTags().contains(tag));
    }

    @Test
    public void testItemEquals() {
        Item item1 = mockItem1();
        Item item2 = mockItem2();
        assertNotEquals(item1, item2);
    }
}
