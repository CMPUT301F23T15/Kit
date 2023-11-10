package com.example.kit.database;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.kit.SelectListener;

import com.example.kit.data.Item;
import com.example.kit.databinding.ItemListRowBinding;
import com.google.android.material.chip.Chip;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * ViewHolder for {@link Item} that binds the data to a view for {@link ItemFirestoreAdapter}
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {
    private final ItemListRowBinding binding;

    /**
     * Create new ViewHolder from a binding.
     * @param binding
     *  The binding with desired layout for the ViewHolder.
     */
    public ItemViewHolder(@NonNull ItemListRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    // TODO: Expand

    /**
     * Displays an {@link Item} within the ViewHolder, binding the data.
     * @param item
     *  The {@link Item} to be displayed
     */
    public void displayItem(@NonNull Item item){
        // Fill in item row values
        DateFormat df = DateFormat.getDateTimeInstance();
        binding.itemNameRow.setText(item.getName());
        binding.itemDateRow.setText(df.format(item.getAcquisitionDate().toDate()));
        String formattedValue = NumberFormat.getCurrencyInstance().format(item.valueToBigDecimal());
        binding.itemValueRow.setText(formattedValue);
        ArrayList<String> tags = item.getTags();

        // Populate chips with tags
        // Leave first "+" chip blank as button to add new tag
        int num_chips = binding.itemTagGroupRow.getChildCount();
        for(int i = 1; i < num_chips; i++) {
            Chip chip = (Chip) binding.itemTagGroupRow.getChildAt(i);

            // Hide the unused chips
            if(i > tags.size()) {
                chip.setText("");
                chip.setVisibility(View.GONE);
                continue;
            }

            chip.setText(tags.get(i-1));
            chip.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets up click and long-click listeners for the item view .
     *
     * @param listener The SelectListener that will handle item click and long-click actions.
     * @param holder The ItemViewHolder which holds the view that the listeners are being attached to.
     * @param model The {@link Item} model associated with the current ViewHolder.
     */
    public void setupListeners(SelectListener listener, ItemViewHolder holder, Item model){
        binding.itemCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemFirestoreAdapter adapter = (ItemFirestoreAdapter) holder.getBindingAdapter();
                String ID = adapter.getSnapshots().getSnapshot(holder.getBindingAdapterPosition()).getId();
                model.attachID(ID);
                listener.onItemClick(model);
            }
        });
        binding.itemCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemLongClick();
                return true;
            }
        });
        binding.addTagChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemFirestoreAdapter adapter = (ItemFirestoreAdapter) holder.getBindingAdapter();
                String ID = adapter.getSnapshots().getSnapshot(holder.getBindingAdapterPosition()).getId();
                model.attachID(ID);
                listener.onAddTagClick(model);
            }
        });
    }

    /**
     * Returns the ItemListRowBinding associated with this ViewHolder.
     *
     * @return The binding instance containing the layout for the item row.
     */
    public ItemListRowBinding getBinding() {
        return binding;
    }
}