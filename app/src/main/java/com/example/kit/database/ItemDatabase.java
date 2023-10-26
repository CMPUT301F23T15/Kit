package com.example.kit.database;

import com.example.kit.data.Item;
import com.google.firebase.firestore.CollectionReference;

import java.util.HashMap;

public class ItemDatabase extends Database {
    private CollectionReference db;

    public ItemDatabase(CollectionReference db) {
        super();
        this.db = fetchCollection("Items");
    }

    public void addItem(Item item) {
        HashMap<String, String> data = new HashMap<>();
        // TODO: Add to hash map, we want a random index that firestore will generate


    }
}
