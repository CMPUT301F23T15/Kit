package com.example.kit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.kit.data.ItemSet;
import com.example.kit.databinding.ItemListBinding;


public class ItemListFragment extends Fragment {

    private NavController navController;
    private ItemListBinding itemListBinding;
    private ItemSet itemSet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        NavHostFragment navHostFragment = (NavHostFragment) getParentFragmentManager().findFragmentById(R.id.nav_host_fragment);
//        if (navHostFragment == null) throw new NullPointerException();
//        navController = navHostFragment.getNavController();

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public void initAddItemButton() {
        itemListBinding.addItemButton.setOnClickListener(click -> {
//            navController.navigate(R.id.action_itemListFragment_to_itemDisplayFragment);
        });
    }
}
