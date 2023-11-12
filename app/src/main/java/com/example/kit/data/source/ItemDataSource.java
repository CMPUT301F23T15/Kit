package com.example.kit.data.source;

import android.util.Log;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.data.FirestoreManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;


public class ItemDataSource extends DataSource<Item, ItemSet> {

    private final CollectionReference itemCollection;
    private final HashMap<String, Item> itemCache;

    public ItemDataSource() {
        this.itemCollection = FirestoreManager.getInstance().getCollection("Items");
        itemCache = new HashMap<>();
        itemCollection.addSnapshotListener((documentSnapshots, error) -> {
            if (documentSnapshots == null) {
                Log.e("Database", "SnapshotListener null query result");
                return;
            }

            itemCache.clear();
            for (QueryDocumentSnapshot documentSnapshot: documentSnapshots) {
                Item item = documentSnapshot.toObject(Item.class);
                item.attachID(documentSnapshot.getId());
                itemCache.put(item.findID(), item);
            }
            onDataChanged();
        });
    }

    @Override
    public void addData(Item newItem) {
        // Add new item and generate ID if it doesn't have one
        if (newItem.findID() == null || newItem.findID().isEmpty()) {
            itemCollection.add(newItem)
                    // Log if fails for some reason
                    .addOnFailureListener(exception -> Log.w("Database", "Failed to add new item."));
        } else {
            // Item already has ID, update the existing document
            itemCollection.document(newItem.findID()).set(newItem)
                    // Log if fails for some reason
                    .addOnFailureListener(exception -> Log.w("Database", "Failed to set item with ID: " + newItem.findID()));
        }
    }

    @Override
    public void deleteDataByID(String id) {
        itemCollection.document(id).delete()
                .addOnSuccessListener(listener -> Log.d("Database", "Document successfully deleted."))
                .addOnFailureListener(exception -> Log.w("Database", exception));
    }

    @Override
    public Item getDataByID(String id) {
        return itemCache.get(id);
    }

    @Override
    public ItemSet getDataSet() {
        ItemSet itemSet = new ItemSet();
        for (Item item : itemCache.values()) {
            itemSet.addItem(item);
        }
        return itemSet;
    }
}
