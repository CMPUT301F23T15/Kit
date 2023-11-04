package com.example.kit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.databinding.ItemListRowBinding;
import com.google.android.material.chip.ChipGroup;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private ItemSet itemSet;
    private Context context;

    public ItemAdapter(Context context, ItemSet itemSet) {
        this.context = context;
        this.itemSet = itemSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.displayItem(itemSet.getItem(position));
    }

    @Override
    public int getItemCount() {
        return itemSet.getItemsCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Refactor to reduce redundancy
            ImageView itemThumbnail = itemView.findViewById(R.id.itemThumbnail_row);
            TextView itemName = itemView.findViewById(R.id.itemName_row);
            TextView itemDate = itemView.findViewById(R.id.itemDate_row);
            ChipGroup itemTagGroup = itemView.findViewById(R.id.itemTagGroup_row);

            // Awaiting completion of chips before adding variables
        }

        public void displayItem(Item item) {

        }
    }

}
