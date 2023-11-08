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
import com.example.kit.database.ItemFirestoreAdapter;
import com.example.kit.database.ItemViewHolder;
import com.example.kit.databinding.ItemListBinding;

import java.util.ArrayList;
public class ItemListFragment extends Fragment implements SelectListener , AddTagFragment.OnTagAddedListener{

    private ItemListBinding binding;
    private ItemListController controller;
    private NavController navController;

    private boolean selectionModeState = false;
    private ItemFirestoreAdapter firestoreAdapter;
    private Item selectedItem;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        controller = ItemListController.getInstance();
        controller.setListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ItemListBinding.inflate(inflater, container, false);
        initializeItemList();
        initializeUIInteractions();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        controller.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        controller.onStop();
    }

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
    }
    @Override
    public void onItemClick(Item item) {
        Log.v("On Item Click", "Item Clicked Success | Item: " + item.getName());
        selectedItem = item;
        navController.navigate(R.id.action_display_item_from_list);
    }

    @Override
    public void onItemLongClick() {
        setSelectionModeState(selectionModeState);
    }

    private void setSelectionModeState(boolean state) {
        int numItems = controller.getAdapter().getItemCount();
        if(state){
            binding.addItemButton.setVisibility(View.GONE);
            binding.deleteItemButton.setVisibility(View.VISIBLE);
            for (int i = 0; i < numItems; i++) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) binding.itemList.findViewHolderForAdapterPosition(i);
                itemViewHolder.getBinding().checkBox.setVisibility(View.VISIBLE);
            }
        } else {
            binding.addItemButton.setVisibility(View.VISIBLE);
            binding.deleteItemButton.setVisibility(View.GONE);
            for (int i = 0; i < numItems; i++) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) binding.itemList.findViewHolderForAdapterPosition(i);
                itemViewHolder.getBinding().checkBox.setVisibility(View.GONE);
            }
        }
        selectionModeState = !state;
    }

    @Override
    public void onAddTagClick() {
        Log.v("Tag Adding", "Tag add click!");
        AddTagFragment dialogFragment = new AddTagFragment();
        dialogFragment.setItem(selectedItem);
        dialogFragment.show(getChildFragmentManager(), "tag_input_dialog");
    }

    @Override
    public void onTagAdded(Item item, String tagName) {
        // Replace "your_item_id_here" with the actual item ID that you want to update
        String itemId = item.getId();

        // Call the method to update Firestore with the new tag
        if (itemId != null && !itemId.isEmpty()) {
            firestoreAdapter.addTagToItem(itemId, tagName);
        } else {
            // to handle the case where the item ID is not available
        }
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
        // setSelectionModeState(false);
    }
}
