package com.example.kit;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.kit.databinding.CarouselImageBinding;
import com.example.kit.databinding.ItemListRowBinding;

public class CarouselImageAdapter extends ListAdapter<CarouselImage, CarouselImageViewHolder> {

    private static final DiffUtil.ItemCallback<CarouselImage> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CarouselImage>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull CarouselImage oldImage, @NonNull CarouselImage newImage) {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    return oldImage == newImage;
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull CarouselImage oldImage, @NonNull CarouselImage newImage) {
                    return false;
                }
            };

    public CarouselImageAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public CarouselImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CarouselImageBinding binding = CarouselImageBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new CarouselImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselImageViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
