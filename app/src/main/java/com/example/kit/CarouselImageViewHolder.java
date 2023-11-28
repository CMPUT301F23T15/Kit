package com.example.kit;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.databinding.CarouselImageBinding;

/**
 * A ViewHolder for a {@link CarouselImage} within RecyclerView with a {@link CarouselImageAdapter}
 * Two modes, displaying an image or a button with callback to add a new image to the Adapter.
 */
public class CarouselImageViewHolder extends RecyclerView.ViewHolder {
    private final CarouselImageBinding binding;
    private final boolean editMode;
    private OnDeleteImageListener deleteImageListener;
    private OnAddImageListener addImageListener;

    /**
     * Construct a new ViewHolder from the binding, setting the editMode property of the ViewHolder
     * @param binding The binding for the ViewHolder
     * @param editMode A flag for displaying the delete buttons or not.
     */
    public CarouselImageViewHolder(CarouselImageBinding binding, boolean editMode) {
        super(binding.getRoot());
        this.editMode = editMode;
        this.binding = binding;
        this.binding.deleteButton.setOnClickListener(v -> {
            deleteImageListener.onDeleteImageClick(getBindingAdapterPosition());
        });
        this.binding.carouselImagePlaceholder.setOnClickListener(v -> addImageListener.onAddImageClick());
    }

    /**
     * Bind the {@link CarouselImage} to this ViewHolder, unless it is null, then it displays an add
     * image button
     * @param image The image to display, or null for add image button
     */
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

    /**
     * Sets the Listener for when the delete button is pressed on this ViewHolder
     * @param deleteImageListener The delete image listener
     */
    public void setOnDeleteImageListener(OnDeleteImageListener deleteImageListener) {
        this.deleteImageListener = deleteImageListener;
    }

    /**
     * Sets the Listener for when the add image button is pressed
     * @param addImageListener The add image listener
     */
    public void setOnAddImageListener(OnAddImageListener addImageListener) {
        this.addImageListener = addImageListener;
    }
    /* END LISTENER METHODS */

    /**
     * An interface for callbacks when the delete button is pressed
     */
    public interface OnDeleteImageListener {
        void onDeleteImageClick(int position);
    }


    /**
     * An interface for callbacks when the add image button is pressed
     */
    public interface OnAddImageListener {
        void onAddImageClick();
    }
}
