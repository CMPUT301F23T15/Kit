package com.example.kit;


import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.database.FirestoreManager;
import com.example.kit.database.ItemFirestoreAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigDecimal;
import java.util.ArrayList;
/**
 * A controller class for the {@link ItemListFragment}, managing the {@link ItemFirestoreAdapter}
 * with queries and filtering.
 */
public class ItemListController implements DefaultLifecycleObserver {

    private static final ItemListController controller = new ItemListController();
    private final CollectionReference itemCollection;
    private final ItemFirestoreAdapter adapter;
    private final ItemSet itemSet;
    private ItemListFragment fragment;

    /**
     * Private Singleton constructor
     */
    private ItemListController() {
        itemCollection = FirestoreManager.getInstance().getCollection("Items");
        itemSet = new ItemSet();

        // Default to getting the entire Items collection.
        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(itemCollection, Item.class)
                .build();

        adapter = new ItemFirestoreAdapter(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                fragment.updateTotalItemValue(itemSet.getItemSetValue());
            }
        };

        itemCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Database", "Item Collection Errored", error);
                    return;
                }

                Log.i("Database", "Snapshot Listener called");

                itemSet.clear();
                for (QueryDocumentSnapshot document : value) {
                    itemSet.addItem(document.toObject(Item.class), document.getId());
                }
            }
        });
    }

    /**
     * Provides the instance of the ItemListController Singleton
     *
     * @return Instance of ItemListController
     */
    public static ItemListController getInstance() {
        return controller;
    }

    public void setFragment(ItemListFragment fragment) {
        this.fragment = fragment;
    }

    /**
     * Provides a reference to the adapter
     *
     * @return Instance of the adapter
     */
    public ItemFirestoreAdapter getAdapter() {
        return adapter;
    }

    /**
     * Update the query on the Adapter according to the filter parameter
     */
    public void updateFilter(/* Filter filter */) {
        // Build query

        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                //        .setQuery()
                .build();

        adapter.updateOptions(options);
    }

    /**
     * Sets the listener for each individual items UI elements
     * @param listener
     */
    public void setListener(SelectListener listener) {
        adapter.setListener(listener);
    }

    public void deleteItem(@NonNull Item item) {
        itemCollection.document(item.findId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Document deleted successfully: " + item.findId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "Document deletion failed: " + item.findId());
                    }
                });
    }

    /**
     * Deletes list of items from the database, and therefore the item set
     * See {@link #deleteItem(Item) deleteItem}
     * @param items
     */
    public void deleteItems(ArrayList<Item> items) {
        for (int i = 0; i < items.size(); i++) {
            deleteItem(items.get(i));
        }
    }

    /**
     * Return an item based on position
     * @param position
     * @return
     */
    public Item getItem(int position) {
        return itemSet.getItem(position);
    }


    /**
     * Starts the adapter listening when the ItemListFragment starts
     * @param owner
     */
    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        adapter.startListening();
    }

    /**
     * Stops the adapter from listening when the ItemListFragment stops
     * @param owner
     */
    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        adapter.stopListening();
    }
}