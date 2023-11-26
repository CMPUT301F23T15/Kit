package com.example.kit;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.kit.data.Item;
import com.example.kit.data.Tag;
import com.example.kit.databinding.ItemListRowBinding;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A RecyclerView ViewHolder for an {@link Item} to be displayed.
 * Shows the Name, Value, Acquisition Date, and {@link  Tag}s of the item.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {
    private final ItemListRowBinding binding;

    /**
     * Create new ViewHolder from a binding.
     * @param binding The binding with desired layout for the ViewHolder.
     */
    public ItemViewHolder(@NonNull ItemListRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * Displays an {@link Item} within the ViewHolder, binding the data.
     * @param item The {@link Item} to be displayed
     */
    @SuppressLint("SetTextI18n")
    public void displayItem(@NonNull Item item){
        // If the item has no name for some reason, display an em
        if (item.getName() == null) {
            binding.itemNameRow.setText("ERROR: ITEM MISSING NAME");
        } else {
            binding.itemNameRow.setText(item.getName());
        }

        // Ensure the date exists before trying to display it
        DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.CANADA);
        if (item.getAcquisitionDate() != null) {
            binding.itemDateRow.setText(dateFormat.format(item.getAcquisitionDate().toDate()));
        }

        // Ensure the value exists before trying to display it
        if (item.getValue() != null) {
            String formattedValue = NumberFormat.getCurrencyInstance().format(item.valueToBigDecimal());
            binding.itemValueRow.setText(formattedValue);
        }
        ArrayList<Tag> tags = item.getTags();

        binding.itemTagGroupRow.enableAddChip(true);
        binding.itemTagGroupRow.clearTags();
        for (Tag tag : tags) {
            binding.itemTagGroupRow.addTag(tag);
        }
    }

    /**
     * This sets up listeners for individual UI elements for each item,
     * this requires a {@link com.example.kit.SelectListener}
     * @param listener The {@link SelectListener} listening to clicks on this ViewHolder.
     * @param holder The holder itself
     * @param position The position of the holder within the adapter.
     */
    public void setupListeners(SelectListener listener, ItemViewHolder holder, int position) {
        // Click listener for the entire item
        binding.itemCardView.setOnClickListener(onClick -> {
            ItemAdapter adapter = (ItemAdapter) getBindingAdapter();
            if (adapter == null) {
                Log.e("RecyclerView", "Adapter invalid for click on ViewHolder: " + holder + "Position: " + position);
                return;
            }

            listener.onItemClick(adapter.getItem(position).findID());
        });

        // Long Click listener for the entire item
        binding.itemCardView.setOnLongClickListener(onLongClick -> {
            listener.onItemLongClick();
            return true;
        });

        // Click listener for the add tag chip
        binding.itemTagGroupRow.getChildAt(0).setOnClickListener(onAddTagClick -> {
            ItemAdapter adapter = (ItemAdapter) getBindingAdapter();
            if (adapter == null) {
                Log.e("RecyclerView", "Adapter invalid for click on ViewHolder: " + holder + "Position: " + position);
                return;
            }
            listener.onAddTagClick(adapter.getItem(position).findID());
        });
    }

    /**
     * Shows the multiselect checkbox.
     */
    public void showCheckbox() {
        binding.checkBox.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the multiselect checkbox, also unchecks it.
     */
    public void hideCheckbox() {
        binding.checkBox.setChecked(false);
        binding.checkBox.setVisibility(View.GONE);
    }

    /**
     * Exposes the status of the checkbox for multiselection.
     * @return Status of the selection checkbox
     */
    public boolean isChecked() {
        return binding.checkBox.isChecked();
    }
}