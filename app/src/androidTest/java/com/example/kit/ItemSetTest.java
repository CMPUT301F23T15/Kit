package com.example.kit;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.google.firebase.Timestamp;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class ItemSetTest {

    public ItemSet mockItemSet() {
        ItemSet mockItems = new ItemSet();
        Item item1 = new Item();
        item1.setName("Mock Item 1");
        item1.setAcquisitionDate(new Timestamp(new Date()));
        item1.setComment("this is an item for testing");
        item1.setDescription("Its made up!");
        item1.setMake("Junit");
        item1.setModel("Mock Item 9000");
        item1.addTag("test");
        item1.addTag("fake");
        item1.attachID("fakeID");
        item1.setValue("1000");

        return mockItems;
    }

    public Item mockItem() {
        Item item = new Item();
        item.setName("Mock Item 2");
        item.setDescription("Another fake item");
        item.setComment("useful comment");
        item.setAcquisitionDate(new Timestamp(new Date()));
        item.setMake("Junit");
        item.setModel("mock item 9001");
        item.addTag("testing");
        item.addTag("fake");
        item.setValue("500");

        return item;
    }

    @Test
    public void testAddItem() {
        ItemSet items = mockItemSet();
        items.addItem(mockItem(), "testID");
        assertEquals(2, items.getItemsCount());
        assertEquals(mockItem(), items.getItem(1));
    }

    @Test
    public void testClearSet() {
        ItemSet items = mockItemSet();
        assertEquals(1, items.getItemsCount());
        items.clear();
        assertEquals(0, items.getItemsCount());
   }


    @Test
    public void testRemoveItem() {
        ItemSet items = mockItemSet();
        Item item = mockItem();
        item.attachID("testID");
        items.addItem(item, item.findID());
        assertEquals(2, items.getItemsCount());
        items.removeItem(item);
        assertEquals(1, items.getItemsCount());
    }

    @Test
    public void testItemSetValue() {
        ItemSet items = mockItemSet();
        assertEquals(new BigDecimal("1000"), items.getItemSetValue());
        items.addItem(mockItem(), "testID");
        assertEquals(new BigDecimal("1500"), items.getItemSetValue());
        items.removeItem(mockItem());
        assertEquals(new BigDecimal("1000"), items.getItemSetValue());
    }
}
