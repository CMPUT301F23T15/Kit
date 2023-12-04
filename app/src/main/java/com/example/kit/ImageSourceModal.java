package com.example.kit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kit.databinding.ImageSourceChoiceBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ImageSourceModal extends BottomSheetDialogFragment {
    private ImageSourceChoiceBinding binding;
    private View.OnClickListener galleryListener;
    private View.OnClickListener cameraListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ImageSourceChoiceBinding.inflate(inflater, container, false);
        binding.galleryButton.setOnClickListener(v -> {
            galleryListener.onClick(v);
            dismiss();
        });
        binding.cameraButton.setOnClickListener(v -> {
            cameraListener.onClick(v);
            dismiss();
        });
        return binding.getRoot();
    }

    public void setGalleryButtonListener(View.OnClickListener listener) {
        galleryListener = listener;
    }

    public void setCameraButtonListener(View.OnClickListener listener) {
        cameraListener = listener;
    }
}
