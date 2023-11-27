package com.example.kit;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.databinding.CarouselImageBinding;

import java.util.ArrayList;

public class CarouselImageAdapter extends RecyclerView.Adapter<CarouselImageViewHolder> {

    private final ArrayList<CarouselImage> images;

    public CarouselImageAdapter() {
        images = new ArrayList<>();
    }

    public void addImage(CarouselImage image) {
        images.add(image);
    }

    @NonNull
    @Override
    public CarouselImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CarouselImageBinding binding =
                CarouselImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CarouselImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselImageViewHolder holder, int position) {
        holder.bind(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
