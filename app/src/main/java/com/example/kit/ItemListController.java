package com.example.kit;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.database.FirestoreManager;
import com.example.kit.database.ItemFirestoreAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class ItemListController {

    private static final ItemListController controller = new ItemListController();
    private final CollectionReference itemCollection;
    private final ItemFirestoreAdapter adapter;
    private ItemSet itemSet;

    private ItemListController() {
        itemCollection = FirestoreManager.getInstance().getItemCollection();

        // Default to getting the entire Items collection.
        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(itemCollection, Item.class)
                .build();

        itemCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                itemSet.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    itemSet.addItem(document.toObject(Item.class));
                }
            }
            else {
                // do stuff
            }
        });

        adapter = new ItemFirestoreAdapter(options);
    }

    public static ItemListController getInstance() {
        return controller;
    }

    public ItemFirestoreAdapter getAdapter() {
        return adapter;
    }

    public void updateFilter(

}
