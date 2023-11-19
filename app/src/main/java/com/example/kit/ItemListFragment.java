package com.example.kit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kit.databinding.FilterSheetBinding;
import com.example.kit.databinding.ItemListBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashSet;

/**
 * A Fragment that displays a RecyclerView that contains a list of {@link com.example.kit.data.Item},
 * Displays the total value of the items currently displayed. Controlled by {@link ItemListController}
 */
public class ItemListFragment extends Fragment implements SelectListener, ItemListController.ItemSetValueChangedCallback {

    // Item List Fields
    private ItemListBinding binding;
    private NavController navController;
    private ItemListController controller;
    private ItemAdapter adapter;
    private boolean inDeleteMode = false;

    // Filter Sheet Fields
    private FilterSheetBinding filterBinding;
    private BottomSheetBehavior<ConstraintLayout> filterSheetBehavior;
    private final AlphaAnimation fadeInFAB;
    private final AlphaAnimation fadeOutFAB;

    {
        fadeOutFAB = new AlphaAnimation(1.0f, 0.0f);
        fadeOutFAB.setDuration(250);
        fadeOutFAB.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.addItemButton.setVisibility(View.GONE);
                binding.deleteItemButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeInFAB = new AlphaAnimation(0.0f, 1.0f);
        fadeInFAB.setDuration(250);
        fadeInFAB.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (inDeleteMode) {
                    binding.deleteItemButton.setVisibility(View.VISIBLE);
                } else {
                    binding.addItemButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

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
        filterBinding = binding.filterSheet;
        initializeItemList();
        initializeUIInteractions();
        initializeFilterSheet();

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
    private void initializeUIInteractions() {
        binding.deleteItemButton.setOnClickListener(onClick -> onDelete());
        binding.addItemButton.setOnClickListener(onClick -> navController.navigate(ItemListFragmentDirections.newItemAction()));
    }

    /**
     * Initialize the FilterSheet, including registering the {@link ItemListController} as a
     * callback for the {@link FilterSheetController.FilterUpdateCallback}.
     */
    private void initializeFilterSheet() {
        filterSheetBehavior = BottomSheetBehavior.from(filterBinding.getRoot());
        filterSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        fadeInFABs();
                        setCollapsedFilterLayout();
                        break;

                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_SETTLING:
                        fadeOutFABs();
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        setExpandedFilterLayout();
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
    }

    private void setExpandedFilterLayout() {
    }

    private void setCollapsedFilterLayout() {
    }

    /**
     * Handles item click events. Opens item details if not in delete mode.
     * @param id The ID for the Item that was clicked.
     */
    @Override
    public void onItemClick(String id) {
        // Disable transition in delete mode
        if (inDeleteMode) return;

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
        toggleDeleteMode();
    }

    private void fadeOutFABs() {
        if (inDeleteMode) {
            if (binding.deleteItemButton.getVisibility() == View.GONE) return;
            binding.deleteItemButton.startAnimation(fadeOutFAB);
        } else {
            if (binding.addItemButton.getVisibility() == View.GONE) return;
            binding.addItemButton.startAnimation(fadeOutFAB);
        }
    }

    private void fadeInFABs() {
        if (inDeleteMode) {
            binding.deleteItemButton.startAnimation(fadeInFAB);
        } else {
            binding.addItemButton.startAnimation(fadeInFAB);
        }
    }

    /**
     * Toggles the UI mode between normal and deletion mode, showing or hiding checkboxes and buttons accordingly.
     */
    private void toggleDeleteMode() {
        inDeleteMode = !inDeleteMode;

        if (inDeleteMode) { // Show delete button
            binding.addItemButton.setVisibility(View.GONE);
            binding.deleteItemButton.setVisibility(View.VISIBLE);
        } else {            // Show add button
            binding.addItemButton.setVisibility(View.VISIBLE);
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

            if(inDeleteMode) {
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
        toggleDeleteMode();
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
     * Handles the event for adding a tag to an item.
     */
    @Override
    public void onAddTagClick(String itemID) {
        HashSet<String> itemIDs = new HashSet<>();
        itemIDs.add(itemID);

        // Below lines demonstrate adding tags to multiple items but I think this is a very jank
        // way to do it, but I think it is a good idea to only use the item IDs.
//        HashSet<Integer> chkd = checkedItems();
//        for (int pos : chkd) {
//            itemIDs.add(adapter.getItem(pos).findID());
//        }

        Log.v("Tag Adding", "Tag add click!");
        AddTagFragment dialogFragment = new AddTagFragment(itemIDs);
        dialogFragment.show(getChildFragmentManager(), "tag_input_dialog");
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
}
