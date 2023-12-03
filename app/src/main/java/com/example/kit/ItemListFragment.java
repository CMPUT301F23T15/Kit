package com.example.kit;


import android.content.Intent;
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

import com.example.kit.databinding.ItemListBinding;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import java.util.Map;

/**
 * A Fragment that displays a RecyclerView that contains a list of {@link com.example.kit.data.Item},
 * Displays the total value of the items currently displayed. Controlled by {@link ItemListController}
 */
public class ItemListFragment extends Fragment implements SelectListener, ItemListController.ItemSetValueChangedCallback {

    private ItemListBinding binding;
    private NavController navController;
    private ItemListController controller;
    private ItemAdapter adapter;
    private boolean inMultiSelectMode = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get NavController for screen navigation
        navController = NavHostFragment.findNavController(this);
        // Create controller for the Item List
        controller = new ItemListController();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ItemListBinding.inflate(inflater, container, false);
        initializeItemList();
        initializeUIInteractions();

        // Add self as callback for updates when the dataset changes
        controller.setCallback(this);
        return binding.getRoot();
    }

    /**
     * Initialize the RecyclerView of the fragment, setting the Adapter and registering this Fragment
     * as a listener for clicks.
     */
    private void initializeItemList() {
        adapter = new ItemAdapter();

        // Bind the fragment as a listener for Clicks, Long Clicks, and the add TagButton clicks
        adapter.setListener(this);

        // Make controller aware of the adapter
        controller.setAdapter(adapter);

        // Initialize RecyclerView with adapter and layout manager
        binding.itemList.setAdapter(adapter);
        binding.itemList.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
  
    /**
     * Initializes UI interactions, setting up listeners for add and delete buttons.
     */
    //TODO: Implement add and profile buttons here
    public void initializeUIInteractions(){
        binding.addItemButton.setOnClickListener(onClick -> {
            navController.navigate(R.id.newItemAction);
        });
        binding.deleteItemButton.setOnClickListener(onClick -> onDelete());
        binding.addItemButton.setOnClickListener(onClick -> navController.navigate(ItemListFragmentDirections.newItemAction()));
        Intent login = new Intent(getActivity(), ProfileActivity.class);
        binding.profileButton.setOnClickListener(onClick -> startActivity(login));
        binding.addTagsButton.setOnClickListener(onClick -> onAddTagMultipleItems());
        binding.cameraButton.setOnClickListener(onClick -> onTakePhoto());
    }

    /**
     * Handles item click events. Opens item details if not in multiselect mode.
     * @param id The ID for the Item that was clicked.
     */
    @Override
    public void onItemClick(String id) {
        // Disable transition in delete mode
        if (inMultiSelectMode) return;

        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        navController.navigate(R.id.displayListItemAction, bundle);

        // Safe args doesn't work for me?
//        ItemListFragmentDirections.DisplayListItemAction action =
//                ItemListFragmentDirections.displayListItemAction(id);
//        navController.navigate(action);
    }

    /**
     * Handles long click events on items to change the UI mode for item deletion.
     */
    @Override
    public void onItemLongClick() {
        toggleMultiSelectMode();
    }

    /**
     * Toggles the UI mode between normal and multiselect mode, showing or hiding checkboxes and buttons accordingly.
     */
    private void toggleMultiSelectMode() {
        inMultiSelectMode = !inMultiSelectMode;

        if (inMultiSelectMode) { // Show delete button
            binding.addItemButton.setVisibility(View.GONE);
            binding.addTagsButton.setVisibility(View.VISIBLE);
            binding.deleteItemButton.setVisibility(View.VISIBLE);
            binding.cameraButton.setVisibility(View.VISIBLE);
        } else {            // Show add button
            binding.addItemButton.setVisibility(View.VISIBLE);
            binding.addTagsButton.setVisibility(View.GONE);
            binding.deleteItemButton.setVisibility(View.GONE);
        }

        toggleViewHolderCheckBoxes();
    }

    /**
     * Toggles the state of the checkboxes used for multi select items in the RecyclerView
     */
    private void toggleViewHolderCheckBoxes() {
        int numItems = adapter.getItemCount();
        for (int i = 0; i < numItems; i++) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) binding.itemList.findViewHolderForAdapterPosition(i);

            if (itemViewHolder == null) {
                Log.e("RecyclerView", "ItemViewHolder at position " + i + " was null.");
                continue;
            }

            if(inMultiSelectMode) {
                itemViewHolder.showCheckbox();
            } else {
                itemViewHolder.hideCheckbox();
            }
        }
    }

    /**
     * Handles the deletion of selected items. Collects all items marked for deletion and requests their removal.
     */
    public void onDelete(){
        controller.deleteCheckedItems(checkedItems());
        toggleMultiSelectMode();
        // TODO: Show SnackBar with option to undo
    }

    /**
     * Helper method to return the positions of the items within the RecyclerView that are checked.
     * @return HashSet of the checked item positions.
     */
    private HashSet<Integer> checkedItems() {
        HashSet<Integer> checkedPositions = new HashSet<>();
        int numItems = adapter.getItemCount();
        for (int i = 0; i < numItems; i++) {
            ItemViewHolder viewHolder = (ItemViewHolder) binding.itemList.findViewHolderForAdapterPosition(i);
            if (viewHolder == null) {
                Log.e("RecyclerView", "ItemViewHolder at position " + i + " was null.");
                continue;
            }

            if (viewHolder.isChecked()) {
                checkedPositions.add(i);
            }
        }
        return checkedPositions;
    }

    /**
     * Opens the {@link AddTagFragment} dialog to add tags to the item clicked on
     */
    @Override
    public void onAddTagClick(String itemID) {
        HashSet<String> itemIDs = new HashSet<>();
        itemIDs.add(itemID);

        Log.i("Tag Dialog", "Adding tags to a single item.");
        AddTagFragment dialogFragment = new AddTagFragment(itemIDs);
        dialogFragment.show(getChildFragmentManager(), "tag_input_dialog");
    }

    /**
     * Opens the {@link AddTagFragment} dialog to add tags to all the checked items.
     */
    private void onAddTagMultipleItems() {
        Log.i("Tag Dialog", "Adding tags to checked items.");
        AddTagFragment dialogFragment =
                new AddTagFragment(controller.getItemIDsAtPositions(checkedItems()));
        dialogFragment.show(getChildFragmentManager(), "tag_input_dialog");
        toggleMultiSelectMode();
    }

    /**
     * Updates the displayed total value of all items in the set.
     * Called by {@link ItemListController} when the dataset changes.
     * @param value The total value to display.
     */
    @Override
    public void onItemSetValueChanged(BigDecimal value) {
        String formattedValue = NumberFormat.getCurrencyInstance().format(value);
        binding.itemSetTotalValue.setText(formattedValue);
    }

    private void onTakePhoto() {
        Log.d("takephoto", "reached");
        checkAndRequestPermissions();
    }
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            // Add other required permissions as needed
    };
    private void checkAndRequestPermissions() {
        // Check if permissions are already granted

        boolean allPermissionsGranted = true;
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                Log.d("cr1", "reached");
                //break;
                startCamera();
            }
        }

        // If any permission is not granted, request permissions
        if (!allPermissionsGranted) {
            String[] permissionsToRequest = Arrays.stream(REQUIRED_PERMISSIONS)
                    .filter(permission -> ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED)
                    .toArray(String[]::new);
            activityResultLauncher.launch(permissionsToRequest);
        } else {
            // All permissions are already granted, proceed with your logic
            Log.d("cr2", "reached");
            startCamera();
        }
    }

    private ActivityResultLauncher<String[]> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                // Handle Permission granted/rejected
                boolean permissionGranted = true;
                for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                    if (Arrays.asList(REQUIRED_PERMISSIONS).contains(entry.getKey()) && !entry.getValue()) {
                        permissionGranted = false;
                    }
                }
                if (!permissionGranted) {
                    //to be done
                } else {
                    onTakePhoto();
                }
            });

    private void startCamera() {
        Intent i = new Intent(requireContext(), CameraActivity.class);
        startActivity(i);
    }


}
