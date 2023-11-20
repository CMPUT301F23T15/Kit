package com.example.kit;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.databinding.ItemListRowBinding;

/**
 * An implementation of a RecyclerView Adapter for an {@link ItemViewHolder} for {@link Item}s
 * within an {@link ItemSet}
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private ItemSet itemSet;
    private SelectListener listener;
    private boolean showCheckboxes = false;

    /**
     * Inflates a new ViewHolder.
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that has its {@link ItemListRowBinding}.
     */
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListRowBinding binding = ItemListRowBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ItemViewHolder(binding);
    }

    /**
     * Calls methods on the {@link ItemViewHolder} to display the {@link Item} at the position
     * in the adapters {@link ItemSet}.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's {@link ItemSet}.
     */
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.displayItem(itemSet.getItem(position));
        holder.setupListeners(listener, holder, position);
        if (showCheckboxes) {
            holder.showCheckbox();
        } else {
            holder.hideCheckbox();
        }
    }

    /**
     * Exposes the number of items in the {@link ItemSet}
     * @return The size of the {@link ItemSet}
     */
    @Override
    public int getItemCount() {
        return itemSet.getItemCount();
    }

    /**
     * Access to {@link Item}s within the {@link ItemSet} at the given position.
     * @param position Position of the item to be returned from the set.
     * @return The Item at the given position.
     */
    public Item getItem(int position) {
        return itemSet.getItem(position);
    }

    /**
     * Setter for the {@link ItemSet} to be represented by this adapter.
     * @param itemSet The ItemSet to be set.
     */
    public void setItemSet(ItemSet itemSet) {
        this.itemSet = itemSet;
    }

    /**
     * Registration method for the {@link SelectListener} that will listen to clicks on the
     * ViewHolders bound by this adapter.
     * @param listener The SelectListener.
     */
    public void setListener(SelectListener listener) {
        this.listener = listener;
    }

    public void showCheckBoxes(boolean show){
        this.showCheckboxes = show;
    }
}
