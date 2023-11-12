package com.example.kit;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.database.ItemViewHolder;
import com.example.kit.databinding.ItemListRowBinding;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private ItemSet itemSet;
    private SelectListener listener;

    public void setItemSet(ItemSet itemSet) {
        this.itemSet = itemSet;
    }

    public void setListener(SelectListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListRowBinding binding = ItemListRowBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.displayItem(itemSet.getItem(position));
        holder.setupListeners(listener, holder, position);
    }

    @Override
    public int getItemCount() {
        return itemSet.getItemCount();
    }

    public Item getItem(int position) {
        return itemSet.getItem(position);
    }
}
