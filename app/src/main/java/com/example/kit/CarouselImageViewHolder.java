package com.example.kit;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.databinding.CarouselImageBinding;

public class CarouselImageViewHolder extends RecyclerView.ViewHolder {
    private final CarouselImageBinding binding;

    public CarouselImageViewHolder(CarouselImageBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(CarouselImage image) {
        binding.carouselImage.setImageDrawable(image.getDrawableRes());
        // Use Glide
    }
}
