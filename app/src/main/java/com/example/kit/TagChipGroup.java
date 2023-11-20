package com.example.kit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;

import com.example.kit.data.Tag;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class TagChipGroup extends ChipGroup {
    private final ArrayList<Tag> tags;
    private Context context;
    private boolean hasAddTagChip = false;

    public TagChipGroup(Context context) {
        super(context);
        this.context = context;

        tags = new ArrayList<>();
    }

    public TagChipGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;

        tags = new ArrayList<>();
    }

    public TagChipGroup(Context context, ArrayList<Tag> tags) {
        super(context);
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

    public void enableAddChip() {
        hasAddTagChip = true;
        Chip addChip = new Chip(new ContextThemeWrapper(context, R.style.tag_add_chip));
        ChipDrawable drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.tag_add_chip);
//        drawable.setIconEndPadding(0f);
//        drawable.setIconStartPadding(0f);
        addChip.setIconEndPadding(0f);
        addChip.setIconStartPadding(0f);
        addChip.setEnsureMinTouchTargetSize(false);
        addChip.setChipDrawable(drawable);
        addChip.setChipMinHeightResource(R.dimen.tag_height);
        addChip.setPadding(0,0,0,0);
        addView(addChip, 0);
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }
    public void clear() {
        tags.clear();
        int count = getChildCount();
        int index;
        if (hasAddTagChip) {
            index = 1;
        } else {
            index = 0;
        }

        while (index < count) {
            removeView(getChildAt(index));
            index++;
        }
    }
}
