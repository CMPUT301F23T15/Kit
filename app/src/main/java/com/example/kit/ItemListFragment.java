package com.example.kit;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kit.databinding.ItemListBinding;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashSet;

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
        binding.cameraButton.setOnClickListener(v -> onCameraClick());
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
            binding.cameraButton.setVisibility(View.GONE);
            binding.addTagsButton.setVisibility(View.VISIBLE);
            binding.deleteItemButton.setVisibility(View.VISIBLE);
        } else {            // Show add button
            binding.addItemButton.setVisibility(View.VISIBLE);
            binding.cameraButton.setVisibility(View.VISIBLE);
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
    private String cameraPermission = android.Manifest.permission.CAMERA;

    // Used to check user permissions for use of the camera
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.i("Scanner Start", "Permissions granted by user!");
                    startCamera();
                } else {
                    Log.i("Scanner Start", "Permissions denied by user!");
                }
            });

    /**
     * When the camera icon is clicked in the list view, request use of the camera
     * as a scanner
     */
    private void onCameraClick(){
        requestScanner();
    }

    /**
     * Request the scanner, and check permissions of the app from the user
     */
    private void requestScanner(){
        if(isPermissionGranted(cameraPermission)){
            Log.i("Scanner Start", "Permissions granted!");
            startCamera();
        } else {
            Log.i("Scanner Start", "Permissions denied!");
            requestCameraPermission();
        }

    }

    /**
     *
     * @param cameraPermission Camera permission status string from manifest
     * @return Returns true if permission is granted, otherwise false
     */
    private boolean isPermissionGranted(String cameraPermission) {
        return ContextCompat.checkSelfPermission(requireContext(), cameraPermission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests Camera permission from the user, launches permission activity
     */
    private void requestCameraPermission(){
        requestPermissionLauncher.launch(cameraPermission);
    }

    // Used to extract barcode data from the scanner activity
    private final ActivityResultLauncher<Intent> scannerActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.v("Scan Return", "Returned from scanning!");
                    Intent data = result.getData();

                    Bundle bundle = new Bundle();
                    bundle.putString("Barcode",data.getStringExtra("Barcode"));
                    navController.navigate(R.id.newItemAction, bundle);
                }
            });

    /**
     * Starts camera for the scanner activity
     */
    private void startCamera() {
        Log.i("Scanner Start", "Launching Scanner!");
        Intent intent = new Intent(requireContext(), ScannerActivity.class);
        scannerActivityResult.launch(intent);
    }

}
