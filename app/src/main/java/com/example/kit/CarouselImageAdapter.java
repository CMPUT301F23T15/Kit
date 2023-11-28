package com.example.kit;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.databinding.CarouselImageBinding;

import java.util.ArrayList;

public class CarouselImageAdapter extends RecyclerView.Adapter<CarouselImageViewHolder> {

    private final ArrayList<CarouselImage> images;
    private boolean editMode = false;

    public CarouselImageAdapter(boolean editMode) {
        images = new ArrayList<>();
        if (editMode) {
            images.add(null);
        }
        this.editMode = editMode;
    }

    public void addImage(CarouselImage image) {
        // Always add images before the null
        if (editMode) {
            if (images.size() < 2) {
                images.add(0, image);
            } else {
                images.add(images.size()-2, image);
            }
        } else {
            images.add(image);
        }
    }

    public ArrayList<CarouselImage> getImages() {
        return images;
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
        holder.bind(images.get(position), editMode);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
