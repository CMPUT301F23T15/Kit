package com.example.kit.database;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.data.Item;
import com.example.kit.databinding.ItemListRowBinding;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    private ItemListRowBinding binding;
    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public ItemViewHolder(ItemListRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void displayItem(Item item){

    }

}
