package com.example.kit.database;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.data.Item;
import com.example.kit.databinding.ItemListRowBinding;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    private final ItemListRowBinding binding;

    public ItemViewHolder(ItemListRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    // TODO: Expand
    public void displayItem(Item item){
        binding.itemNameRow.setText(item.getName());
    }
}
