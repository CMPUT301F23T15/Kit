package com.example.kit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kit.databinding.ItemListBinding;


public class ItemListFragment extends Fragment {

    private ItemListBinding binding;
    private ItemListController controller;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = ItemListController.getInstance();
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
}
