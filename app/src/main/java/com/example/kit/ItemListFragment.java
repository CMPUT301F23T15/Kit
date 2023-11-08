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
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.kit.data.Item;
import com.example.kit.database.ItemViewHolder;
import com.example.kit.databinding.ItemListBinding;

import java.util.ArrayList;
/**
 * A Fragment that displays a RecyclerView that contains a list of {@link com.example.kit.data.Item},
 * Displays the total value of the items currently displayed.
 */
public class ItemListFragment extends Fragment implements SelectListener{


    private ItemListBinding binding;
    private ItemListController controller;
    private NavController navController;
    private boolean selectionModeState = false;

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
        controller.setNavController(navController);
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
     * Standard lifecycle method for a fragment
     */
    @Override
    public void onStart() {
        super.onStart();
        controller.onStart();
    }

    /** Standard lifecycle method for a fragment
     *
     */
    @Override
    public void onStop() {
        super.onStop();
        controller.onStop();
    }

    /**
     * Initialize the RecyclerView of the fragment
     */
    private void initializeItemList() {
        binding.itemList.setAdapter(controller.getAdapter());
        binding.itemList.setLayoutManager(new LinearLayoutManager(this.getContext()));

    }

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
    @Override
    public void onItemClick(Item item) {
        if(!selectionModeState) {
            navController.navigate(R.id.displayListItemAction);
        }
    }

    @Override
    public void onItemLongClick() {
        setSelectionModeState(selectionModeState);
    }

    private void setSelectionModeState(boolean state) {
        int numItems = controller.getAdapter().getItemCount();
        if(state){
            binding.addItemButton.setVisibility(View.VISIBLE);
            binding.deleteItemButton.setVisibility(View.GONE);
            for (int i = 0; i < numItems; i++) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) binding.itemList.findViewHolderForAdapterPosition(i);
                itemViewHolder.getBinding().checkBox.setChecked(false);
                itemViewHolder.getBinding().checkBox.setVisibility(View.GONE);
            }
        } else {
            binding.addItemButton.setVisibility(View.GONE);
            binding.deleteItemButton.setVisibility(View.VISIBLE);
            for (int i = 0; i < numItems; i++) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) binding.itemList.findViewHolderForAdapterPosition(i);
                itemViewHolder.getBinding().checkBox.setVisibility(View.VISIBLE);
            }
        }
        selectionModeState = !state;
    }

    @Override
    public void onAddTagClick() {
        Log.v("Tag Adding", "Tag add click!");
    }
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
        setSelectionModeState(true);
    }
}
