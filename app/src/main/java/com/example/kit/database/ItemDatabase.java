package com.example.kit.database;

import com.example.kit.data.Item;
import com.google.firebase.firestore.CollectionReference;

import java.util.HashMap;

public class ItemDatabase extends Database {
    private CollectionReference db;

    public ItemDatabase() {
        super();
        this.db = fetchCollection();
    }

    public void addItem(Item item) {
        HashMap<String, String> data = new HashMap<>();

        // TODO: Add to hash map, we want a random index that firestore will generate
    }
    @Override
    public CollectionReference fetchCollection() {
        return getDB().collection("Items");
    }
}
