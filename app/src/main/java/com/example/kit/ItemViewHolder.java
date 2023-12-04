package com.example.kit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.example.kit.command.CommandManager;
import com.example.kit.command.RemoveTagFromItemCommand;
import com.example.kit.data.Item;
import com.example.kit.data.Tag;
import com.example.kit.databinding.ItemListRowBinding;
import com.example.kit.util.FormatUtils;
import com.example.kit.views.TagChipGroup;
import com.example.kit.util.ImageUtils;

import java.util.ArrayList;

/**
 * A RecyclerView ViewHolder for an {@link Item} to be displayed.
 * Shows the Name, Value, Acquisition Date, and {@link  Tag}s of the item.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder implements TagChipGroup.OnTagChipCloseListener {
    private final ItemListRowBinding binding;
    private Context context;
    private String itemID;
    private static final int TRANSITION_TIME = 125;

    /**
     * Create new ViewHolder from a binding.
     * @param binding The binding with desired layout for the ViewHolder.
     */
    public ItemViewHolder(Context context, @NonNull ItemListRowBinding binding) {
        super(binding.getRoot());
        this.context = context;
        this.binding = binding;
        binding.itemThumbnailRow.setClipToOutline(true);
    }

    /**
     * Displays an {@link Item} within the ViewHolder, binding the data.
     * @param item The {@link Item} to be displayed
     */
    @SuppressLint("SetTextI18n")
    public void displayItem(@NonNull Item item) {
        this.itemID = item.findID();

        // If the item has no name for some reason, display an em
        if (item.getName() == null) {
            binding.itemNameRow.setText("ERROR: ITEM MISSING NAME");
        } else {
            binding.itemNameRow.setText(item.getName());
        }

        // Ensure the date exists before trying to display it
        if (item.getAcquisitionDate() != null) {
            binding.itemDateRow.setText(FormatUtils.formatDateStringLong(item.getAcquisitionDate()));
        }

        // Ensure the value exists before trying to display it
        if (item.getValue() != null) {
            binding.itemValueRow.setText(FormatUtils.formatValue(item.valueToBigDecimal(), true));
        }

        // Display Tags as chips, including a default add tag chip
        ArrayList<Tag> tags = item.getTags();
        binding.itemTagGroupRow.enableAddChip(true);
        binding.itemTagGroupRow.clearTags();
        for (Tag tag : tags) {
            binding.itemTagGroupRow.addTag(tag);
        }

        binding.itemTagGroupRow.setChipCloseListener(this);

        // If there is images associated with the item, display the first image as the thumbnail
        if (item.getBase64Images().size() > 0) {
            String thumbnailBase64 = item.getBase64Images().get(0);
            Bitmap thumbnailBitmap;
            thumbnailBitmap = ImageUtils.convertBase64ToBitmap(thumbnailBase64);
            // Crop the image to a square
            if (thumbnailBitmap != null) {
                Bitmap croppedThumbnail = ImageUtils.cropBitmapToThumbnail(thumbnailBitmap, itemView.getContext());
                binding.itemThumbnailRow.setImageBitmap(croppedThumbnail);
            }
        } else {
            binding.itemThumbnailRow.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.baseline_hide_image_24));
        }
    }

    /**
     * This sets up listeners for individual UI elements for each item,
     * this requires a {@link com.example.kit.SelectListener}
     * @param listener The {@link SelectListener} listening to clicks on this ViewHolder.
     * @param holder The holder itself
     * @param position The position of the holder within the adapter.
     */
    public void setupListeners(SelectListener listener, ItemViewHolder holder, int position) {
        // Click listener for the entire item
        binding.itemCardView.setOnClickListener(onClick -> {
            ItemAdapter adapter = (ItemAdapter) getBindingAdapter();
            if (adapter == null) {
                Log.e("RecyclerView", "Adapter invalid for click on ViewHolder: " + holder + "Position: " + position);
                return;
            }

            listener.onItemClick(adapter.getItem(position).findID());
        });

        // Long Click listener for the entire item
        binding.itemCardView.setOnLongClickListener(onLongClick -> {
            listener.onItemLongClick();
            return true;
        });

        // Click listener for the add tag chip
        binding.itemTagGroupRow.getChildAt(0).setOnClickListener(onAddTagClick -> {
            ItemAdapter adapter = (ItemAdapter) getBindingAdapter();
            if (adapter == null) {
                Log.e("RecyclerView", "Adapter invalid for click on ViewHolder: " + holder + "Position: " + position);
                return;
            }
            listener.onAddTagClick(adapter.getItem(position).findID());
        });
    }

    /**
     * Shows the multiselect checkbox.
     */
    public void showCheckbox() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(binding.getRoot());
        constraintSet.connect(binding.itemCardView.getId(), ConstraintSet.END, binding.checkBox.getId(), ConstraintSet.START);
        constraintSet.applyTo(binding.getRoot());

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(TRANSITION_TIME);
        TransitionManager.beginDelayedTransition(binding.rowConstraintLayout, changeBounds);
        binding.checkBox.setVisibility(View.VISIBLE);

        binding.itemTagGroupRow.setInEditMode(true);
    }

    /**
     * Hides the multiselect checkbox, also unchecks it.
     */
    public void hideCheckbox() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(binding.getRoot());
        constraintSet.connect(binding.itemCardView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.applyTo(binding.getRoot());

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(TRANSITION_TIME);
        TransitionManager.beginDelayedTransition(binding.rowConstraintLayout, changeBounds);
        binding.checkBox.setChecked(false);
        binding.checkBox.setVisibility(View.GONE);

        binding.itemTagGroupRow.setInEditMode(false);
    }

    /**
     * Exposes the status of the checkbox for multiselection.
     * @return Status of the selection checkbox
     */
    public boolean isChecked() {
        return binding.checkBox.isChecked();
    }

    @Override
    public void onTagChipClosed(Tag tag) {
        RemoveTagFromItemCommand command = new RemoveTagFromItemCommand(tag, itemID);
        CommandManager.getInstance().executeCommand(command);
    }
}