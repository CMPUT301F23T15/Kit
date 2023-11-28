package com.example.kit;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.databinding.CarouselImageBinding;

public class CarouselImageViewHolder extends RecyclerView.ViewHolder {
    private final CarouselImageBinding binding;
    private final boolean editMode;
    private OnDeleteImageListener deleteImageListener;
    private OnAddImageListener addImageListener;

    public CarouselImageViewHolder(CarouselImageBinding binding, boolean editMode) {
        super(binding.getRoot());
        this.editMode = editMode;
        this.binding = binding;
        this.binding.deleteButton.setOnClickListener(v -> {
            deleteImageListener.onDeleteImageClick(getBindingAdapterPosition());
        });
        this.binding.carouselImagePlaceholder.setOnClickListener(v -> addImageListener.onAddImageClick());
    }

    public void bind(CarouselImage image) {
        // If image is null, it is the add image placeholder
        if (image == null) {
            binding.carouselImage.setVisibility(View.GONE);
            binding.carouselImagePlaceholder.setVisibility(View.VISIBLE);
            binding.deleteButton.setVisibility(View.GONE);
            return;

        // Otherwise display image as normal
        } else {
            binding.carouselImage.setImageBitmap(image.getImage());
            binding.carouselImage.setVisibility(View.VISIBLE);
            binding.carouselImagePlaceholder.setVisibility(View.GONE);
        }

        // Show the delete button if we are in delete mode
        if (editMode) binding.deleteButton.setVisibility(View.VISIBLE);
        else binding.deleteButton.setVisibility(View.GONE);
    }

    /* BEGIN LISTENER METHODS */
    public void setOnDeleteImageListener(OnDeleteImageListener deleteImageListener) {
        this.deleteImageListener = deleteImageListener;
    }

    public void setOnAddImageListener(OnAddImageListener addImageListener) {
        this.addImageListener = addImageListener;
    }

    public interface OnDeleteImageListener {
        void onDeleteImageClick(int position);
    }
    /* END LISTENER METHODS */

    public interface OnAddImageListener {
        void onAddImageClick();
    }
}
