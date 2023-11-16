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
import com.example.kit.data.Tag;
import com.example.kit.data.source.DataSourceManager;
import com.example.kit.databinding.ItemDisplayBinding;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Fragment that displays an {@link Item} for viewing purposes.
 */
public class ItemDisplayFragment extends Fragment {

    private ItemDisplayBinding binding;
    private NavController navController;
    private String itemID;

    /**
     * Standard fragment lifecycle
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
    }

    /**
     * Standard fragment lifecycle, initializes binding, NavController, and UI elements
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
        binding = ItemDisplayBinding.inflate(inflater, container, false);
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
        binding.floatingActionButton.setOnClickListener(onClick -> navController.popBackStack());
        binding.floatingActionButton2.setOnClickListener(onClick -> {
            Bundle itemId = new Bundle();
            itemId.putString("id", itemID);
            navController.navigate(R.id.editDisplayItemAction, itemId);
        });
    }

    /**
     * Loads an item's details into the UI components if editing an existing item. Retrieves the item from the fragment's arguments.
     */
    private void loadItem() {
        // Retrieve the item from the bundle
        if (getArguments() == null) {
            Log.e("Navigation", "No arguments found for the transition to fragment.");
            return;
        }
        String id = getArguments().getString("id");
        this.itemID = id;
        Item item = DataSourceManager.getInstance().getItemDataSource().getDataByID(id);

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
            binding.itemDisplayTagGroup.addView(chip);
        }
    }
}