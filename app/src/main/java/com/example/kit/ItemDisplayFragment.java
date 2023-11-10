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

/**
 * ItemDisplayFragment is a Fragment subclass used to display details of an {@link Item} object.
 * It supports creating a new item or editing an existing one, integrating with Firestore for data persistence.
 */
public class ItemDisplayFragment extends Fragment {

    private ItemDisplayBinding binding;
    private NavController navController;
    private boolean newItem;
    private String itemID;

    /**
     * Called to do initial creation of the fragment. Sets up the navigation controller and determines if a new item is being created.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */

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

    /**
     * Called to have the fragment instantiate its user interface view. Initializes the binding and navController.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ItemDisplayBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);
        initializeConfirmButton();
        return binding.getRoot();

    }
    /**
     * Called when the fragment becomes visible. Handles loading of the item details if editing an existing item.
     */

    @Override
    public void onStart() {
        super.onStart();
        loadItem();
    }

    /**
     * Initializes the floating action button to handle the creation or update of an item in the Firestore database.
     */
    private void initializeConfirmButton() {
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newItem) {
                    FirestoreManager.getInstance().getCollection("Items").add(buildItem());
                    navController.navigate(ItemDisplayFragmentDirections.itemCreatedAction());
                } else if (!(itemID == null || itemID.isEmpty())) {
                    FirestoreManager.getInstance().getCollection("Items").document(itemID).set(buildItem());
                    navController.navigate(ItemDisplayFragmentDirections.itemCreatedAction());
                }
            }
        });
    }

    /**
     * Loads an item's details into the UI components if editing an existing item. Retrieves the item from the fragment's arguments.
     */
    private void loadItem() {
        // Retrieve the item from the bundle
        Item item = (Item) getArguments().getSerializable("item");
        if (item != null) {
            this.itemID = item.findId();
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
                Chip chip = new Chip(getContext());
                chip.setText(tag);
                binding.itemDisplayTagGroup.addView(chip);
            }
        }
    }

    /**
     * Constructs an {@link Item} object from the data input in the UI fields.
     * This method includes date parsing and exception handling for invalid date formats.
     *
     * @return {@link Item} The newly constructed or updated item.
     * @throws RuntimeException If there is a problem parsing the acquisition date.
     */
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
