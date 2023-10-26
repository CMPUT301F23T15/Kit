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

import com.example.kit.data.Item;
import com.example.kit.databinding.ItemDisplayBinding;

public class ItemDisplayFragment extends Fragment {

    ItemDisplayBinding itemDisplayBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemDisplayBinding = ItemDisplayBinding.inflate(inflater, container, false);
        return itemDisplayBinding.getRoot();
    }
}
