package com.example.kit.data.source;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;

import java.util.ArrayList;
import java.util.HashMap;

public class TestItemDataSource extends AbstractItemDataSource {

    public TestItemDataSource() {
        itemCache = new HashMap<>();
    }

    @Override
    public void addData(Item item) {
        itemCache.put(item.findID(), item);
    }

    @Override
    public void deleteDataByID(String id) {
        itemCache.remove(id);
    }

    @Override
    public Item getDataByID(String id) {
        return itemCache.get(id);
    }

    @Override
    public ItemSet getDataSet() {
        ItemSet itemSet = new ItemSet();
        ArrayList<Item> itemArray = new ArrayList<>(itemCache.values());
        for (Item item : itemArray) {
            itemSet.addItem(item);
        }
        return itemSet;
    }
}
