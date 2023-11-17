package com.example.kit;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kit.data.Filter;
import com.example.kit.databinding.FilterSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashSet;

public class FilterSheetFragment extends BottomSheetDialogFragment {

    private FilterSheetBinding binding;
    private FilterSheetController controller;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.controller = new FilterSheetController();
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FilterSheetBinding.inflate(inflater, container, false);
        setupControllerInterface();
        return binding.getRoot();
    }

    private void setupControllerInterface() {
        binding.searchBar.addTextChangedListener(controller.createSearchWatcher());
        binding.valueLow.addTextChangedListener(controller.createLowerValueWatcher());
        binding.valueHigh.addTextChangedListener(controller.createUpperValueWatcher());
        binding.dateStart.addTextChangedListener(controller.createLowerDateWatcher());
        binding.dateEnd.addTextChangedListener(controller.createUpperValueWatcher());
        // need to somehow pass the values from the Chips inside the tag chip group and the make
        // chip group to to the controller as well.
        controller.updateMakes(new HashSet<>());
        controller.updateTags(new HashSet<>());
    }
    

    public void setCallback(FilterSheetController.FilterUpdateCallback callback) {
        controller.setCallback(callback);
    }
}
