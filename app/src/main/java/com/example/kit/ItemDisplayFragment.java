package com.example.kit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.kit.data.Item;
import com.example.kit.databinding.ItemDisplayBinding;
import com.google.android.material.chip.Chip;

public class ItemDisplayFragment extends Fragment {

    private ItemDisplayBinding binding;
    private NavController navController;
    private boolean newItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        if (getArguments() == null || getArguments().isEmpty()) {
            newItem = true;
        } else {
            newItem = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ItemDisplayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private Item buildItem() {
        Item newItem = new Item();

        newItem.setName(binding.itemNameDisplay.getText().toString());
        newItem.setValue(binding.itemValueDisplay.getText().toString());
        newItem.setDescription(binding.itemDescriptionDisplay.getText().toString());
        newItem.setComment(binding.itemCommentDisplay.getText().toString());
        newItem.setMake(binding.itemMakeDisplay.getText().toString());
        newItem.setModel(binding.itemModelDisplay.getText().toString());
//        newItem.setSerialNumber(itemDisplayBinding.); TODO: Add this to XML, Jesse Forgot

        // Tags are sort of 1 indexed because the first tag is the add new tag button
        int numTags = binding.itemDisplayTagGroup.getChildCount();
        for (int i = 1; i < numTags; i++) {
            Chip chip = (Chip) binding.itemDisplayTagGroup.getChildAt(i);
            if (!chip.getText().toString().isEmpty()) {
                newItem.addTag(chip.getText().toString());
            }
        }

        return newItem;
    }
}
