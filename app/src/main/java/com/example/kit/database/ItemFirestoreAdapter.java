package com.example.kit.database;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.kit.data.Item;
import com.example.kit.databinding.ItemListRowBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * RecyclerView Adapter
 */
public class ItemFirestoreAdapter extends FirestoreRecyclerAdapter<Item, ItemViewHolder> {

    /**
     * Constructor that initializes a query for the Adapter from the Firestore database.
     * @param options
     *  The {@link FirestoreRecyclerOptions} containing the query for the adapter.
     */
    public ItemFirestoreAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
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
}
