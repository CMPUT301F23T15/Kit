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
import com.example.kit.database.ItemViewHolder;
import com.example.kit.databinding.ItemListBinding;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
/**
 * A Fragment that displays a RecyclerView that contains a list of {@link com.example.kit.data.Item},
 * Displays the total value of the items currently displayed.
 */
public class ItemListFragment extends Fragment implements SelectListener {

    private ItemListBinding binding;
    private ItemListController controller;
    private NavController navController;
    private boolean modeFlag = false;

    /**
     * Standard lifecycle method for a fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        controller = ItemListController.getInstance();
        controller.setListener(this);
        controller.setFragment(this);
        getLifecycle().addObserver(controller);
    }

    /**
     * Standard lifecycle method for a fragment
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     *  Root of the viewbinding
     */
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
        binding.itemList.setAdapter(controller.getAdapter());
        binding.itemList.setLayoutManager(new LinearLayoutManager(this.getContext()));

    }
    /**
     * Initializes UI interactions, setting up listeners for add and delete buttons.
     */
    //TODO: Implement add and profile buttons here
    public void initializeUIInteractions(){
        binding.deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDelete();
            }
        });

        binding.addItemButton.setOnClickListener(onClick -> {
            navController.navigate(ItemListFragmentDirections.newItemAction());
        });
    }

    /**
     * Handles item click events. Opens item details if not in delete mode.
     *
     * @param item The item that was clicked.
     */
    @Override
    public void onItemClick(Item item) {
        if(!modeFlag) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", item);
            navController.navigate(R.id.displayListItemAction, bundle);
        }
    }

    /**
     * Handles long click events on items to change the UI mode for item deletion.
     */
    @Override
    public void onItemLongClick() {
        changeMode();
    }

    /**
     * Toggles the UI mode between normal and deletion mode, showing or hiding checkboxes and buttons accordingly.
     */
    private void changeMode() {
        modeFlag = !modeFlag;
        int numItems = controller.getAdapter().getItemCount();
        if(modeFlag){
            binding.addItemButton.setVisibility(View.GONE);
            binding.deleteItemButton.setVisibility(View.VISIBLE);
        } else {
            binding.addItemButton.setVisibility(View.VISIBLE);
            binding.deleteItemButton.setVisibility(View.GONE);
        }

        for (int i = 0; i < numItems; i++) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) binding.itemList.findViewHolderForAdapterPosition(i);
            if(modeFlag) {
                itemViewHolder.getBinding().checkBox.setVisibility(View.VISIBLE);
            } else {
                itemViewHolder.getBinding().checkBox.setChecked(false);
                itemViewHolder.getBinding().checkBox.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Handles the event for adding a tag to an item.
     */
    @Override
    public void onAddTagClick() {
        Log.v("Tag Adding", "Tag add click!");
    }

    /**
     * Handles the deletion of selected items. Collects all items marked for deletion and requests their removal.
     */
    public void onDelete(){
        int numItems = binding.itemList.getAdapter().getItemCount();
        ArrayList<Item> deleteItems = new ArrayList<>();
        for (int i = 0; i < numItems; i++) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) binding.itemList.findViewHolderForAdapterPosition(i);
            if (itemViewHolder.getBinding().checkBox.isChecked()){
                deleteItems.add(controller.getItem(i));
                itemViewHolder.getBinding().checkBox.setChecked(false);
            }
        }
        controller.deleteItems(deleteItems);
        // TODO: Fix this, I think it is referencing an outdated value when looping causing a crash, it is intended to reapply the regular mode
        changeMode();
    }

    /**
     * Updates the displayed total value of all items in the set.
     *
     * @param value The total value to display.
     */
    public void updateTotalItemValue(BigDecimal value) {
        String formattedValue = NumberFormat.getCurrencyInstance().format(value);
        binding.itemSetTotalValue.setText(formattedValue);
    }
}
