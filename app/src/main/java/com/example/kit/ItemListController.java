package com.example.kit;


import android.util.Log;

import androidx.navigation.NavController;


import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.database.FirestoreManager;
import com.example.kit.database.ItemFirestoreAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
public class ItemListController implements SelectListener{

    private static final ItemListController controller = new ItemListController();
    private final CollectionReference itemCollection;
    private final ItemFirestoreAdapter adapter;
    private final ItemSet itemSet;
    private NavController navController;

    private ItemListController() {
        itemCollection = FirestoreManager.getInstance().getCollection("Items");
        itemSet = new ItemSet();

        // Default to getting the entire Items collection.
        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(itemCollection, Item.class)
                .build();

        itemCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                itemSet.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    itemSet.addItem(document.toObject(Item.class), document.getId());
                }
            }
            else {
                // TODO: Throw error
            }
        });

        adapter = new ItemFirestoreAdapter(options, this);
    }

    public static ItemListController getInstance() {
        return controller;
    }

    public void onStart() {
        adapter.startListening();
    }

    public void onStop() {
        adapter.stopListening();
    }

    public ItemFirestoreAdapter getAdapter() {
        return adapter;
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public NavController getNavController () {
        return navController;
    }

    public void updateFilter(/* Filter filter */) {
        // Build query

        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
        //        .setQuery()
                .build();

        adapter.updateOptions(options);
    }

    @Override
    public void onItemClick(Item item) {
        Log.v("On Item Click", "Item Clicked Success | Item: " + item.getName());
             navController.navigate(R.id.action_display_item_from_list);
    }

    @Override
    public void onItemLongClick(Item item) {
        Log.v("On Item Long Click", "Start selection");
    }

}
