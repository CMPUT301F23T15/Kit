package com.example.kit.database;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.data.Item;
import com.example.kit.databinding.ItemListRowBinding;

import java.text.DateFormat;

import java.util.Date;

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
    }
}
