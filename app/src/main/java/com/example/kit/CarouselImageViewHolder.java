package com.example.kit;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.databinding.CarouselImageBinding;

public class CarouselImageViewHolder extends RecyclerView.ViewHolder {
    private final CarouselImageBinding binding;

    public CarouselImageViewHolder(CarouselImageBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(CarouselImage image, boolean deletable) {
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
        if (deletable) binding.deleteButton.setVisibility(View.VISIBLE);
        else binding.deleteButton.setVisibility(View.GONE);
    }
}
