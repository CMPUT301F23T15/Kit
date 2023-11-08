package com.example.kit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kit.databinding.ItemListBinding;


public class ItemListFragment extends Fragment {

    private ItemListBinding binding;
    private ItemListController controller;

    /**
     * Standard lifecycle method for a fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = ItemListController.getInstance();
        controller.setNavController(NavHostFragment.findNavController(this));
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

    public void initializeAddItemButton() {
//        itemListBinding.addItemButton.setOnClickListener(onClick -> {

//        });
    }
}
