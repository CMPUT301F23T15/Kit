package com.example.kit;

import android.os.Bundle;
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

import com.example.kit.databinding.ItemListBinding;

/**
 * A Fragment that displays a RecyclerView that contains a list of {@link com.example.kit.data.Item},
 * Displays the total value of the items currently displayed.
 */
public class ItemListFragment extends Fragment {

    private ItemListBinding binding;
    private ItemListController controller;
    private NavController navController;

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
        initializeAddItemButton();
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

    /**
     * Initialize the floating action button that navigates to the new item fragment
     */
    public void initializeAddItemButton() {
        binding.addItemButton.setOnClickListener(onClick -> {
            navController.navigate(ItemListFragmentDirections.newItemAction());
        });
    }
}
