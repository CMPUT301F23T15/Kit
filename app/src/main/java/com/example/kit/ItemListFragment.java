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

import com.example.kit.data.Item;
import com.example.kit.databinding.ItemListBinding;


import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashSet;

/**
 * A Fragment that displays a RecyclerView that contains a list of {@link com.example.kit.data.Item},
 * Displays the total value of the items currently displayed.
 */
public class ItemListFragment extends Fragment implements SelectListener, ItemListController.ItemSetValueChangedCallback, AddTagFragment.OnTagAddedListener {

    private ItemListBinding binding;
    private NavController navController;
    private ItemListController controller;
    private ItemAdapter adapter;
    private boolean inDeleteMode = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);

        adapter = new ItemAdapter();

        controller = new ItemListController();
        controller.setCallback(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ItemListBinding.inflate(inflater, container, false);
        initializeItemList();
        initializeUIInteractions();
        return binding.getRoot();
    }

    /**
     * Initialize the RecyclerView of the fragment
     */
    private void initializeItemList() {
        controller.setAdapter(adapter);
        controller.setListener(this);
        binding.itemList.setAdapter(adapter);
        binding.itemList.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
  
    /**
     * Initializes UI interactions, setting up listeners for add and delete buttons.
     */
    //TODO: Implement add and profile buttons here
    public void initializeUIInteractions(){
        binding.deleteItemButton.setOnClickListener(onClick -> onDelete());
        binding.addItemButton.setOnClickListener(onClick -> navController.navigate(ItemListFragmentDirections.newItemAction()));
    }

    /**
     * Handles item click events. Opens item details if not in delete mode.
     *
     * @param Ttem The item that was clicked.
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

    /**
     * Toggles the UI mode between normal and deletion mode, showing or hiding checkboxes and buttons accordingly.
     */
    private void toggleDeleteMode() {
        inDeleteMode = !inDeleteMode;

        if (inDeleteMode) {
            binding.addItemButton.setVisibility(View.GONE);
            binding.deleteItemButton.setVisibility(View.VISIBLE);
        } else {
            binding.addItemButton.setVisibility(View.VISIBLE);
            binding.deleteItemButton.setVisibility(View.GONE);
        }

        toggleViewHolderCheckBoxes();
    }

    private void toggleViewHolderCheckBoxes() {
        int numItems = adapter.getItemCount();
        for (int i = 0; i < numItems; i++) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) binding.itemList.findViewHolderForAdapterPosition(i);

            if (itemViewHolder == null) {
                throw new NullPointerException("ItemViewHolder at position " + i + " was null!");
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
        HashSet<Integer> checkedPositions = new HashSet<>();
        int numItems = adapter.getItemCount();
        for (int i = 0; i < numItems; i++) {
            ItemViewHolder viewHolder = (ItemViewHolder) binding.itemList.findViewHolderForAdapterPosition(i);
            if (viewHolder.isChecked()) {
                checkedPositions.add(i);
            }
        }

        controller.deleteCheckedItems(checkedPositions);
        toggleDeleteMode();
        // TODO: Show snackbar with option to undo
    }

    /**
     * Handles the event for adding a tag to an item.
     */
    @Override
    public void onAddTagClick(Item item) {
        Log.v("Tag Adding", "Tag add click!");
        AddTagFragment dialogFragment = new AddTagFragment();
        dialogFragment.setOnTagAddedListener(this);
        dialogFragment.setItem(item);
        dialogFragment.show(getChildFragmentManager(), "tag_input_dialog");
    }

    @Override
    public void onTagAdded(Item item, String tagName) {
        Log.v("Tag adding", "Tag reached onTag");
        String itemID = item.findID();
        // Call the method to update Firestore with the new tag
        if (!itemID.isEmpty()) {
//            adapter.addTagToItem(itemID, tagName);
            Log.v("Tag adding", "Tag going to adapter");
        } else {
            Log.v("Tag adding", "TagID null");
        }
    }

    /**
     * Updates the displayed total value of all items in the set.
     *
     * @param value The total value to display.
     */
    @Override
    public void onItemSetValueChanged(BigDecimal value) {
        String formattedValue = NumberFormat.getCurrencyInstance().format(value);
        binding.itemSetTotalValue.setText(formattedValue);
    }
}
