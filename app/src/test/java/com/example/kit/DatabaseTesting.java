package com.example.kit;

import com.example.kit.data.Item;
import com.example.kit.database.ItemDatabase;
import com.google.firebase.firestore.CollectionReference;

import org.junit.Test;

public class DatabaseTesting {
    ItemDatabase itemDB = new ItemDatabase();
    @Test
    public void DatabaseTestAdd(){
        Item item = new Item();
        itemDB.addItem(item);
    }


}
