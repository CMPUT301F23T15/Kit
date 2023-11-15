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

import com.example.kit.command.AddItemCommand;
import com.example.kit.command.CommandManager;
import com.example.kit.data.Item;
import com.example.kit.data.Tag;
import com.example.kit.data.source.DataSource;
import com.example.kit.data.source.DataSourceManager;
import com.example.kit.databinding.ItemDisplayBinding;
import com.example.kit.databinding.ItemEditBinding;
import com.google.android.material.chip.Chip;
import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * ItemDisplayFragment is a Fragment subclass used to display details of an {@link Item} object.
 * It supports creating a new item or editing an existing one, integrating with Firestore for data persistence.
 */
public class ItemEditFragment extends Fragment {

    private ItemEditBinding binding;
    private NavController navController;
    private String itemID;

    /**
     * Standard fragment lifecycle, stores a reference to the NavController.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
    }

    /**
     * Standard Fragment lifecycle function, inflates the binding and initializes buttons.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The root view of the binding.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ItemEditBinding.inflate(inflater, container, false);
        initializeConfirmButton();
        return binding.getRoot();
    }

    /**
     * Loads the inputted item on start
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
        binding.floatingActionButton.setOnClickListener(onClick -> {
            // Add item to the database and navigate back to the list
            CommandManager.getInstance().executeCommand(new AddItemCommand(buildItem()));
            navController.navigate(ItemEditFragmentDirections.itemCreatedAction());
        });
    }

    /**
     * Loads an item's details into the UI components if editing an existing item. Retrieves the item from the fragment's arguments.
     */
    private void loadItem() {
        // Retrieve the item from the bundle
        if (getArguments() == null) {
            // bad stuff
            return;
        }
        String id = getArguments().getString("id");
        Item item = DataSourceManager.getInstance().getItemDataSource().getDataByID(id);
        if (item != null) {
            this.itemID = item.findID();
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
            for (Tag tag : item.getTags()) {
                Chip chip = new Chip(getContext());
                chip.setText(tag.getName());
                chip.setBackgroundColor(tag.getColor().toArgb());
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
        DataSource<Tag, ArrayList<Tag>> tagDataSource = DataSourceManager.getInstance().getTagDataSource();
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
                newItem.addTag(tagDataSource.getDataByID(chip.getText().toString()));
            }
        }

        if (itemID != null && !itemID.isEmpty()) {
            newItem.attachID(itemID);
        }

        return newItem;
    }
}
