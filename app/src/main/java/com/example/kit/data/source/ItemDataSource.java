package com.example.kit.data.source;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.database.FirestoreManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ItemDataSource extends DataSource<Item, ItemSet> {

    private final CollectionReference itemCollection;

    public ItemDataSource() {
        this.itemCollection = FirestoreManager.getInstance().getCollection("Items");
        itemCollection.addSnapshotListener((value, error) -> onDataChanged());
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

    @Override
    public ItemSet getDataCollection() {
        ItemSet itemSet = new ItemSet();
        itemCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Item item = doc.toObject(Item.class);
                item.attachID(doc.getId());
                itemSet.addItem(item);
            }
        });
        return itemSet;
    }
}
