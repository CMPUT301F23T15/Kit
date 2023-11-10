package com.example.kit.database;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.kit.SelectListener;
import com.example.kit.data.Item;
import com.example.kit.databinding.ItemListRowBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * RecyclerView Adapter
 */
public class ItemFirestoreAdapter extends FirestoreRecyclerAdapter<Item, ItemViewHolder> {
    private SelectListener listener;
    private FirestoreManager firestoreManager;

    /**
     * Constructor that initializes a query for the Adapter from the Firestore database.
     * @param options
     *  The {@link FirestoreRecyclerOptions} containing the query for the adapter.
     */
    public ItemFirestoreAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {

        super(options);
        firestoreManager = FirestoreManager.getInstance();
    }

    /**
     * Populates the {@link ItemViewHolder} with the {@link Item}
     * @param holder
     *  {@link ItemViewHolder} to be populated
     * @param position
     *  Position of the item in the adapter list.
     * @param model the model object containing the data that should be used to populate the view.
     */
    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Item model) {
        holder.displayItem(model);
        holder.setupListeners(listener, holder, model);
    }

    /**
     * Creates an {@link ItemViewHolder}.
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return Inflated {@link ItemViewHolder} from the layout file.
     */
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListRowBinding binding = ItemListRowBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ItemViewHolder(binding);
    }
    public void setListener(SelectListener listener) {
        this.listener = listener;
    }

    public void addTagToItem(String itemID, String newTag) {
        DocumentReference itemRef = firestoreManager.getCollection("Items").document(itemID);

        // Update the Firestore document with the new tag
        itemRef.update("tags", FieldValue.arrayUnion(newTag))
                .addOnSuccessListener(aVoid -> {
                    Log.v("Tag Adding", "Tag added!!");
                })
                .addOnFailureListener(e -> {
                    Log.v("Tag Adding", "Tag failed!!");
                });
    }

    public void searchTags(String itemID, String searchTag) {
        CollectionReference tagsCollection = firestoreManager.getCollection("Tags");
        Log.v("Tag Adding", "Tag reached Adapter!");
        tagsCollection.whereEqualTo("tagName", searchTag)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.v("Tag Adding", "Tag added!!");
                            firestoreManager.addTagToItem(itemID, searchTag);
                        }

                        if (task.getResult().isEmpty()) {
                            Log.v("Tag Adding", "Tag failed!!");
                        }
                    } else {
                        // Handle errors
                        Log.e("Firestore", "Error getting tags", task.getException());
                    }
                });
    }


}
