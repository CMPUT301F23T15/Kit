package com.example.kit.database;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.ItemListController;
import com.example.kit.R;
import com.example.kit.data.Item;
import com.example.kit.databinding.ItemListRowBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

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
        binding.itemValueRow.setText(item.getValue());

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
}
