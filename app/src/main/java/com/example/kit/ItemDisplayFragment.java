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
import com.example.kit.database.FirestoreManager;
import com.example.kit.databinding.ItemDisplayBinding;
import com.google.android.material.chip.Chip;
import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemDisplayFragment extends Fragment {

    private ItemDisplayBinding binding;
    private NavController navController;

    private ItemListController itemListController;
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
        navController = NavHostFragment.findNavController(this);

        // Retrieve the item from the bundle
        Item item = (Item) getArguments().getSerializable("item");

        if (item != null) {
            // Use View Binding to populate UI elements with item data
            binding.itemNameDisplay.setText(item.getName());
            binding.itemValueDisplay.setText(item.getValue());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            String formattedDate = dateFormat.format(item.getAcquisitionDate().toDate());
            binding.itemDateDisplay.setText(formattedDate);
            binding.itemDescriptionDisplay.setText(item.getDescription());
            binding.itemCommentDisplay.setText(item.getComment());
            binding.itemMakeDisplay.setText(item.getMake());
            binding.itemModelDisplay.setText(item.getModel());
            binding.itemSerialNumberDisplay.setText(item.getSerialNumber());

            binding.itemDisplayTagGroup.removeAllViews();
            for (String tag : item.getTags()) {
                Chip chip = new Chip(requireContext());
                chip.setText(tag);
                binding.itemDisplayTagGroup.addView(chip);
            }
        }


        initializeConfirmButton();
        return binding.getRoot();

    }



    private void initializeConfirmButton() {
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirestoreManager.getInstance().getCollection("Items").add(buildItem());
                navController.navigate(ItemDisplayFragmentDirections.itemCreatedAction());
            }
        });
    }

    private Item buildItem() {
        Item newItem = new Item();

        newItem.setName(binding.itemNameDisplay.getText().toString());
        newItem.setValue(binding.itemValueDisplay.getText().toString());
        // Absolutely terrible garbage, TODO: Improve data input handling. Currently only takes XX/XX/XXXX dates
        try {
            Date date = DateFormat.getDateInstance(DateFormat.SHORT).parse(binding.itemDateDisplay.getText().toString());
            newItem.setAcquisitionDate(new Timestamp(date));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        newItem.setDescription(binding.itemDescriptionDisplay.getText().toString());
        newItem.setComment(binding.itemCommentDisplay.getText().toString());
        newItem.setMake(binding.itemMakeDisplay.getText().toString());
        newItem.setModel(binding.itemModelDisplay.getText().toString());
        newItem.setSerialNumber(binding.itemSerialNumberDisplay.getText().toString());

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
