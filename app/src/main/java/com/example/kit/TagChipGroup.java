package com.example.kit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.example.kit.data.Tag;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class TagChipGroup extends ChipGroup {
    private boolean addChipEnabled = false;
    private final Chip addChip;
    private final ArrayList<Tag> tags;
    private Context context;

    public TagChipGroup(Context context) {
        super(context);
        this.context = context;
        addChip = new Chip(new ContextThemeWrapper(context, R.style.tag_add_chip));
        if (!addChipEnabled) addChip.setVisibility(View.GONE);

        tags = new ArrayList<>();
    }

    public TagChipGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        addChip = new Chip(new ContextThemeWrapper(context, R.style.tag_add_chip));
        if (!addChipEnabled) addChip.setVisibility(View.GONE);

        tags = new ArrayList<>();
    }

    public TagChipGroup(Context context, ArrayList<Tag> tags) {
        super(context);
        addChip = new Chip(new ContextThemeWrapper(context, R.style.tag_add_chip));
        if (!addChipEnabled) addChip.setVisibility(View.GONE);
        this.tags = tags;
        for (Tag tag : this.tags) {
            addTag(tag);
        }
    }

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
        addView(chip);
    }

    public void enableAddChip(boolean enable) {
        addChipEnabled = enable;
        if (addChipEnabled) {
            addChip.setVisibility(View.VISIBLE);
        } else {
            addChip.setVisibility(View.GONE);
        }
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }
}
