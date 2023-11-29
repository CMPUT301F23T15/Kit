package com.example.kit;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.databinding.CarouselImageBinding;

import java.util.ArrayList;

/**
 * An implementation of a RecyclerView Adapter to manage displaying {@link CarouselImage}s for an
 * {@link com.example.kit.data.Item}. Relays a {@link com.example.kit.CarouselImageViewHolder.OnAddImageListener}
 * to its ViewHolders. Can toggle between an Edit or Display mode, which toggles delete and add buttons
 * within the list.
 */
public class CarouselImageAdapter extends RecyclerView.Adapter<CarouselImageViewHolder>
        implements CarouselImageViewHolder.OnDeleteImageListener {

    private final ArrayList<CarouselImage> images;
    private CarouselImageViewHolder.OnAddImageListener addImageListener;
    private final boolean editMode;

    /**
     * Construct an Adapter in one of the two modes
     * @param editMode Flag to display the add and delete buttons
     */
    public CarouselImageAdapter(boolean editMode) {
        images = new ArrayList<>();
        this.editMode = editMode;

        // If we are in editMode, add a null object to the images list that will represent the add
        // images button. The ViewHolder responds to a null object to display the button instead
        // of displaying an image.
        if (editMode) images.add(null);
    }

    /* BEGIN INHERITED METHODS */
    /**
     * Create a new ViewHolder, relaying the editMode and setting the listeners for the ViewHolder.
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder
     */
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

    /**
     * Binds a {@link CarouselImage} to the ViewHolder
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull CarouselImageViewHolder holder, int position) {
        holder.bind(images.get(position));
    }

    /**
     * Return the amount of images in the list, including the null image representing the add image
     * button
     * @return The size of the internal image list
     */
    @Override
    public int getItemCount() {
        return images.size();
    }
    /* END INHERITED METHODS */

    /* BEGIN DATA METHODS */

    /**
     * Add an image to this list, behind the add item button if in edit mode, otherwise at the end.
     * @param image CarouselImage to be added
     */
    public void addImage(CarouselImage image) {
        // Always add images before the null
        if (editMode) {
            if (images.size() < 2) {
                // With less than 2 images, we must add the newest image directly at index 0
                images.add(0, image);
            } else {
                // If we have 2 or more images, we want to add the newest image in the second to last
                // index behind the "null" image used to represent the add image button.
                images.add(images.size()-1, image);
            }
        } else {
            // No add item button, freely add images at the end of the list
            images.add(image);
//            notifyItemInserted(images.size()-1);
        }
        notifyDataSetChanged();
    }

    /**
     * Returns all images in the list
     * @return All defined images in the list
     */
    public ArrayList<CarouselImage> getImages() {
        // Return all images except the null placeholder if we are in editmode
        if (editMode) return new ArrayList<> (images.subList(0, images.size()-1));
        // Otherwise return all images freely
        return images;
    }
    /* END DATA METHODS */


    /* BEGIN LISTENER METHODS */
    /**
     * Callback method for the {@link com.example.kit.CarouselImageViewHolder.OnDeleteImageListener}
     * which removes the selected image from the list and notifies the adapter of the changes
     * @param position Position of the image to delete
     */
    @Override
    public void onDeleteImageClick(int position) {
        images.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Sets the {@link com.example.kit.CarouselImageViewHolder.OnAddImageListener} that will be
     * relayed to the ViewHolders
     * @param addListener The Listener to relay
     */
    public void setAddImageListener(CarouselImageViewHolder.OnAddImageListener addListener) {
        this.addImageListener = addListener;
    }
    /* END LISTENER METHODS */
}
