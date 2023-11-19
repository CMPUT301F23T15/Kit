package com.example.kit;

import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;


import com.example.kit.data.Item;
import com.example.kit.data.Tag;
import com.example.kit.databinding.ItemListRowBinding;
import com.google.android.material.chip.Chip;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * A RecyclerView ViewHolder for an {@link Item} to be displayed.
 * Shows the Name, Value, Acquisition Date, and {@link  Tag}s of the item.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {
    private final ItemListRowBinding binding;
    private static final int TRANSITION_TIME = 125;

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
    public void displayItem(@NonNull Item item){
        // Fill in item row values
        DateFormat df = DateFormat.getDateTimeInstance();
        binding.itemNameRow.setText(item.getName());
        binding.itemDateRow.setText(df.format(item.getAcquisitionDate().toDate()));
        String formattedValue = NumberFormat.getCurrencyInstance().format(item.valueToBigDecimal());
        binding.itemValueRow.setText(formattedValue);
        ArrayList<Tag> tags = item.getTags();

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

            chip.setText(tags.get(i-1).getName());
            chip.setBackgroundColor(tags.get(i-1).getColor().toArgb());
            chip.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This sets up listeners for individual UI elements for each item,
     * this requires a {@link com.example.kit.SelectListener}
     * @param listener The {@link SelectListener} listening to clicks on this ViewHolder.
     * @param holder The holder itself
     * @param position The position of the holder within the adapter.
     */
    public void setupListeners(SelectListener listener, ItemViewHolder holder, int position){
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

        // Click listener for the tags
        binding.addTagChip.setOnClickListener(onAddTagClick -> {
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
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(binding.getRoot());
        constraintSet.connect(binding.itemCardView.getId(), ConstraintSet.END, binding.checkBox.getId(), ConstraintSet.START);
        constraintSet.applyTo(binding.getRoot());

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(TRANSITION_TIME);
        TransitionManager.beginDelayedTransition(binding.rowConstraintLayout, changeBounds);
        binding.checkBox.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the multiselect checkbox, also unchecks it.
     */
    public void hideCheckbox() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(binding.getRoot());
        constraintSet.connect(binding.itemCardView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.applyTo(binding.getRoot());

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(TRANSITION_TIME);
        TransitionManager.beginDelayedTransition(binding.rowConstraintLayout, changeBounds);
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