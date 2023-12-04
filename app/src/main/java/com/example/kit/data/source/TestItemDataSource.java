package com.example.kit.data.source;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;

import java.util.ArrayList;
import java.util.HashMap;

public class TestItemDataSource extends AbstractItemDataSource {
    private int itemCount = 0;
    private final HashMap<String, Item> items;

    public TestItemDataSource() {
        items = new HashMap<>();
    }

    @Override
    public void addData(Item item) {
        if (item.findID() == null) {
            item.attachID(String.valueOf(itemCount));
            itemCount++;
        }
        items.put(item.findID(), item);
        onDataChanged();
    }

    @Override
    public void deleteDataByID(String id) {
        items.remove(id);
        onDataChanged();
    }

    @Override
    public Item getDataByID(String id) {
        return items.get(id);
    }

    @Override
    public ItemSet getDataSet() {
        ItemSet itemSet = new ItemSet();
        ArrayList<Item> itemArray = new ArrayList<>(items.values());
        for (Item item : itemArray) {
            itemSet.addItem(item);
        }
        return itemSet;
    }
}
