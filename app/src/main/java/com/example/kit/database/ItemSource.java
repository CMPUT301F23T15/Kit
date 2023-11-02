package com.example.kit.database;

import com.example.kit.data.Item;

public interface ItemSource {
    public void addInstance(Item item);
    public void removeInstance(String uid);
}
