package com.example.kit.data.source;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.kit.data.Item;
import com.example.kit.database.FirestoreManager;
import com.google.firebase.firestore.CollectionReference;


public class ItemDataSource implements DataSource<Item> {

    private final CollectionReference itemCollection;

    public ItemDataSource() {
        this.itemCollection = FirestoreManager.getInstance().getCollection("Items");
    }

    @Override
    public void addData(Item newItem) {
        // Add new item and generate ID if it doesn't have one
        if (newItem.findId() == null || newItem.findId().isEmpty()) {
            itemCollection.add(newItem)
                    // Log if fails for some reason
                    .addOnFailureListener(exception -> Log.w("Database", "Failed to add new item."));
        } else {
            // Item already has ID, update the existing document
            itemCollection.document(newItem.findId()).set(newItem)
                    // Log if fails for some reason
                    .addOnFailureListener(exception -> Log.w("Database", "Failed to set item with ID: " + newItem.findId()));
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
        // Must use final object reference to receive item from listener
        final Item[] items = new Item[1];

        itemCollection.document(id).get()
                // On success, load item into the single array
                .addOnSuccessListener(documentSnapshot -> {
                    items[0] = documentSnapshot.toObject(Item.class);
                    Log.d("Database", "Successfully retrieved item with ID:" + id );
                })
                .addOnFailureListener(exception -> Log.w("Database", exception));

        return items[0];
    }
}
