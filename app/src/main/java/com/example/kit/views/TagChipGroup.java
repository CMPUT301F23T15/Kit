package com.example.kit.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.example.kit.R;
import com.example.kit.data.Tag;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

/**
 * A Custom Implementation of {@link ChipGroup} that displays {@link Tag}s directly, and has an
 * optional Chip that allows adding new tags to this group
 */
public class TagChipGroup extends ChipGroup {
    private final Context context;
    private OnTagChipCloseListener listener;

    private boolean addChipEnabled = false;
    private Chip addChip;

    private final ArrayList<Tag> tags;
    private final ArrayList<Chip> chips;

    /**
     * Calls super, then creates the Add Tag Chip
     * @param context Context that this Group is being created in
     */
    public TagChipGroup(Context context) {
        super(context);
        this.context = context;
        createAddTagChip();

        tags = new ArrayList<>();
        chips = new ArrayList<>();
    }

    /**
     * Calls super, then creates the Add Tag Chip
     * @param context Context that this Group is being created in
     * @param attributeSet Attribute set for super
     */
    public TagChipGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        createAddTagChip();

        tags = new ArrayList<>();
        chips = new ArrayList<>();
    }

    public void setInEditMode(boolean inEditMode) {
        for (Chip chip : chips) {
            chip.setCloseIconVisible(inEditMode);
        }
    }

    /**
     * Enables or disables the visibility of the Add Tag Chip.
     * @param enable Displays the Add Tag chip if true.
     */
    public void enableAddChip(boolean enable) {
        addChipEnabled = enable;
        if (addChipEnabled) {
            addChip.setVisibility(View.VISIBLE);
        } else {
            addChip.setVisibility(View.GONE);
        }
    }

    /**
     * Create the Add Tag Chip and set it to hidden by default
     */
    private void createAddTagChip() {
        addChip = new Chip(new ContextThemeWrapper(context, R.style.tag_add_chip));
        ChipDrawable drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.tag_add_chip);
        addChip.setChipDrawable(drawable);
        addChip.setEnsureMinTouchTargetSize(false);
        addChip.setText("");
        addChip.setPadding(0,0,0,0);
        addChip.setChipMinHeightResource(R.dimen.tag_height);
        addChip.setIconEndPadding(0f);
        addChip.setIconStartPadding(0f);
        addView(addChip, 0);

        if (!addChipEnabled) addChip.setVisibility(View.GONE);
    }

    /**
     * Add a {@link Tag} to this Group, keep track of the Tag and the created chip
     * Displays the Tag's name and sets the color of the chip to the color of the tag.
     * @param tag The Tag to add.
     */
    public void addTag(Tag tag) {
        tags.add(tag);
        Chip chip = new Chip(new ContextThemeWrapper(context, R.style.tag_chip));
        ChipDrawable drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.tag_chip);
        drawable.setChipBackgroundColor(ColorStateList.valueOf(tag.getColor().toArgb()));
        chip.setChipDrawable(drawable);
        chip.setEnsureMinTouchTargetSize(false);
        chip.setText(tag.getName());
        chip.setPadding(0,0,0,0);
        chip.setChipMinHeightResource(R.dimen.tag_height);
        chip.setOnCloseIconClickListener(v -> {
            if (listener == null) return;
            removeChip(v);
        });
        addView(chip);
        chips.add(chip);
    }

    private void removeChip(View v) {
        Chip chip = (Chip) v;
        String tagName = chip.getText().toString();
        for (Tag tag : tags) {
            if (tagName.equals(tag.getName())) {
                tags.remove(tag);
                listener.onTagChipClosed(tag);
                break;
            }
        }
        chips.remove(chip);
        removeView(chip);
    }

    public boolean isEmpty() {
        return chips.isEmpty();
    }

    /**
     * Clear the list of {@link Tag}s displayed by this group, and remove the Chip views.
     */
    public void clearTags() {
        tags.clear();

        for (View chip : chips) {
            removeView(chip);
        }

        chips.clear();
    }

    /**
     * Gets the {@link Tag}s displayed by this Group
     * @return The tags
     */
    public ArrayList<Tag> getTags() {
        return tags;
    }



    public void setChipCloseListener(OnTagChipCloseListener listener) {
        this.listener = listener;
    }

    public interface OnTagChipCloseListener {
        void onTagChipClosed(Tag tag);
    }
}

