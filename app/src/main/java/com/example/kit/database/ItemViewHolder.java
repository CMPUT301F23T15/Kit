package com.example.kit.database;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.kit.SelectListener;

import com.example.kit.data.Item;
import com.example.kit.databinding.ItemListRowBinding;
import com.google.android.material.chip.Chip;

import java.text.DateFormat;
import java.util.ArrayList;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    private final ItemListRowBinding binding;

    public ItemViewHolder(@NonNull ItemListRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    // TODO: Expand
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
    public void setupListeners(SelectListener listener, ItemViewHolder holder, Item model){
        binding.itemCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        binding.itemNameRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.setItemChecked(v);
            }
        });
        binding.addTagChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddTagClick();
            }
        });
    }

    public ItemListRowBinding getBinding() {
        return binding;
    }
}
