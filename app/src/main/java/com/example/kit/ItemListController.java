package com.example.kit;


import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
import java.util.ArrayList;
/**
 * A controller class for the {@link ItemListFragment}, managing the {@link ItemFirestoreAdapter}
 * with queries and filtering.
 */
public class ItemListController {

    private static final ItemListController controller = new ItemListController();
    private final CollectionReference itemCollection;
    private final ItemFirestoreAdapter adapter;
    private final ItemSet itemSet;

    private NavController navController;

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

        adapter = new ItemFirestoreAdapter(options);
    }

    /**
     * Provides the instance of the ItemListController Singleton
     * @return
     *  Instance of ItemListController
     */
    public static ItemListController getInstance() {
        return controller;
    }

    /**
     * Life Cycle method for the {@link ItemListFragment#onStart() onStart}
     */
    public void onStart() {
        adapter.startListening();
    }

    /**
     * Life Cycle method for the {@link ItemListFragment#onStop() onStop}
     */
    public void onStop() {
        adapter.stopListening();
    }

    /**
     * Provides a reference to the adapter
     * @return
     *  Instance of the adapter
     */
    public ItemFirestoreAdapter getAdapter() {
        return adapter;
    }
    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public NavController getNavController () {
        return navController;
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

   public void setListener(SelectListener listener){
        adapter.setListener(listener);
   }

   public void deleteItem(@NonNull Item item){
       itemCollection.document(item.getId())
               .delete()
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                       Log.d("Firestore", "Document deleted successfully: " + item.getId());
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.d("Firestore", "Document deletion failed: " + item.getId());
                   }
               });
   }
   public void deleteItems(ArrayList<Item> items){
        for(int i = 0; i < items.size(); i++) {
            deleteItem(items.get(i));
        }
   }
   public Item getItem(int position){
        return itemSet.getItem(position);
   }
}
