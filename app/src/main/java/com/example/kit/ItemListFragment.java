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
     * Initializes UI elements that require interaction, such as buttons
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
     * Implements action based on an item being clicked in the list
     * @param item
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
     * Implements action on an item being long clicked in the list
     */
    @Override
    public void onItemLongClick() {
        changeMode();
    }

    /**
     * This changes the mode in the list, from either viewing items to
     * selecting items for deletion
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
     * This implements acitons when the add tag button is clicked on each item
     */
    @Override
    public void onAddTagClick() {
        Log.v("Tag Adding", "Tag add click!");
    }

    /**
     * This implements the action taken when an item is delted
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
        changeMode();
    }

    /**
     * This updates the total value of every item in the lsit
     * @param value
     */
    public void updateTotalItemValue(BigDecimal value) {
        String formattedValue = NumberFormat.getCurrencyInstance().format(value);
        binding.itemSetTotalValue.setText(formattedValue);
    }
}
