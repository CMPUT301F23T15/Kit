package com.example.kit;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.data.Item;
import com.example.kit.database.ItemViewHolder;
import com.example.kit.databinding.ItemListBinding;


public class ItemListFragment extends Fragment implements SelectListener{

    private ItemListBinding binding;
    private ItemListController controller;
    private NavController navController;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = ItemListController.getInstance();
        controller.setListener(this);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ItemListBinding.inflate(inflater, container, false);
        initializeItemList();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        controller.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        controller.onStop();
    }

    private void initializeItemList() {
        binding.itemList.setAdapter(controller.getAdapter());
        binding.itemList.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    public void initializeAddItemButton() {
//        itemListBinding.addItemButton.setOnClickListener(onClick -> {

//        });
    }
    @Override
    public void onItemClick(Item item) {
        Log.v("On Item Click", "Item Clicked Success | Item: " + item.getName());
        navController.navigate(R.id.action_display_item_from_list);
    }

    @Override
    public void onItemLongClick() {
        int numItems = binding.itemList.getAdapter().getItemCount();
        for (int i = 0; i < numItems; i++) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) binding.itemList.findViewHolderForAdapterPosition(i);
            itemViewHolder.getBinding().itemNameRow.setVisibility(View.GONE);
        }
    }

    public void setItemChecked(View view){
        controller.getAdapter().adapterSetChecked(view);
    }
}
