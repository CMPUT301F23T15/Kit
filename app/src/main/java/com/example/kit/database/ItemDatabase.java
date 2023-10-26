package com.example.kit.database;

import android.nfc.Tag;

import com.example.kit.data.Item;
import com.google.firebase.firestore.CollectionReference;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ItemDatabase {
    private CollectionReference db;

    public ItemDatabase(CollectionReference db, <Item> itemList) {
        this.db = db;
    }

    public void addItem(){
        // HashMap<String, String> data = new HashMap<>();

    }


}
