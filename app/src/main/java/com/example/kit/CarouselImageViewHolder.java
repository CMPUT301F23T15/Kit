package com.example.kit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.kit.databinding.CarouselImageBinding;

public class CarouselImageViewHolder extends RecyclerView.ViewHolder {
    private CarouselImageBinding binding;

    public CarouselImageViewHolder(CarouselImageBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(CarouselImage image) {
        binding.carouselImage.setImageBitmap(image.getImage());

//        Glide.with(binding.getRoot().getContext())
//                .asBitmap()
//                .load(image.getImage())
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        binding.carouselImage.setImageBitmap(resource);
//                        Log.i("image", "reasource ready");
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                    }
//                });
    }
}
