package com.example.kit;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class CarouselImageAdapter extends ListAdapter<CarouselImage, CarouselImageViewHolder> {
    @LayoutRes private final int itemLayoutRes = R.layout.carousel_image;

    private static final DiffUtil.ItemCallback<CarouselImage> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CarouselImage>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull CarouselImage oldImage, @NonNull CarouselImage newImage) {
                    return oldImage.equals(newImage);
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull CarouselImage oldImage, @NonNull CarouselImage newImage) {
                    return oldImage.equals(newImage);
                }
            };

    public CarouselImageAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public CarouselImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CarouselImageViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(itemLayoutRes, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselImageViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
