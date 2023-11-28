package com.example.kit;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.databinding.CarouselImageBinding;

import java.util.ArrayList;

public class CarouselImageAdapter extends RecyclerView.Adapter<CarouselImageViewHolder>
        implements CarouselImageViewHolder.OnDeleteImageListener {

    private final ArrayList<CarouselImage> images;
    private CarouselImageViewHolder.OnAddImageListener addImageListener;
    private final boolean editMode;

    public CarouselImageAdapter(boolean editMode) {
        images = new ArrayList<>();
        this.editMode = editMode;

        // If we are in editMode, add a null object to the images list that will represent the add
        // images button. The ViewHolder responds to a null object to display the button instead
        // of displaying an image.
        if (editMode) images.add(null);
    }

    /* BEGIN INHERITED METHODS */
    @NonNull
    @Override
    public CarouselImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CarouselImageBinding binding =
                CarouselImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        CarouselImageViewHolder holder = new CarouselImageViewHolder(binding, editMode);
        holder.setOnDeleteImageListener(this);
        holder.setOnAddImageListener(addImageListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselImageViewHolder holder, int position) {
        holder.bind(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
    /* END INHERITED METHODS */

    /* BEGIN DATA METHODS */
    public void addImage(CarouselImage image) {
        // Always add images before the null
        if (editMode) {
            if (images.size() < 2) {
                // With less than 2 images, we must add the newest image directly at index 0
                images.add(0, image);
                notifyItemInserted(0);
            } else {
                // If we have 2 or more images, we want to add the newest image in the second to last
                // index behind the "null" image used to represent the add image button.
                images.add(images.size()-2, image);

                // Notify the adapter of the change, with an offset of -3 due to the above insertion
                // changing the size of the array
                notifyItemInserted(images.size()-3);
            }
        } else {
            // No add item button, freely add images at the end of the list
            images.add(image);
            notifyItemInserted(images.size()-2);
        }
    }

    public ArrayList<CarouselImage> getImages() {
        // Return all images except the null placeholder if we are in editmode
        if (editMode) return new ArrayList<> (images.subList(0, images.size()-2));
        // Otherwise return all images freely
        return images;
    }
    /* END DATA METHODS */


    /* BEGIN LISTENER METHODS */
    @Override
    public void onDeleteImageClick(int position) {
        images.remove(position);
        notifyItemRemoved(position);
    }

    public void setAddImageListener(CarouselImageViewHolder.OnAddImageListener addListener) {
        this.addImageListener = addListener;
    }
    /* END LISTENER METHODS */
}
