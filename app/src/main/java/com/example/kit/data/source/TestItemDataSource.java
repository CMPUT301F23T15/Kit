package com.example.kit.data.source;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TestItemDataSource extends DataSource<Item, ItemSet> {

    private final HashMap<String, Item> items;

    public TestItemDataSource() {
        items = new HashMap<>();
    }

    @Override
    public void addData(Item item) {
        items.put(item.findID(), item);
    }

    @Override
    public void deleteDataByID(String id) {
        items.remove(id);
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
