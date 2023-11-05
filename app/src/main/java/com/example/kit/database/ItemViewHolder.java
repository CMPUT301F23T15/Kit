package com.example.kit.database;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.R;
import com.example.kit.data.Item;
import com.example.kit.databinding.ItemListRowBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    private final ItemListRowBinding binding;

    public ItemViewHolder(ItemListRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    // TODO: Expand
    public void displayItem(Item item){
        // Fill in item row values

        DateFormat df = DateFormat.getDateTimeInstance();
        binding.itemNameRow.setText(item.getName());
        binding.itemDateRow.setText(df.format(item.getAcquisitionDate().toDate()));
        binding.itemValueRow.setText(item.getValue());

        ArrayList<String> tags = item.getTags();

        Chip[] chips = {
                binding.tagChip1,
                binding.tagChip2,
                binding.tagChip3,
                binding.tagChip4,
                binding.tagChip5,
                binding.tagChip6,
                binding.tagChip7,
                binding.tagChip8,
                binding.tagChip9,
                binding.tagChip10,
                binding.tagChip11,
                binding.tagChip12,
                binding.tagChip13,
                binding.tagChip14,
                binding.tagChip15,
                binding.tagChip16,
                binding.tagChip17,
                binding.tagChip18,
                binding.tagChip19,
                binding.tagChip20
        };

        // Populate chips with tags
        int num_chips = binding.itemTagGroupRow.getChildCount();
        for(int i = 0; i < num_chips; i++) {
            Chip chip = (Chip) binding.itemTagGroupRow.getChildAt(i);

            // Hide the unused chips
            if(i >= tags.size()) {
                chip.setText("");
                chip.setVisibility(View.GONE);
                continue;
            }

            chip.setText(tags.get(i));
            chip.setVisibility(View.VISIBLE);
        }
    }
}
