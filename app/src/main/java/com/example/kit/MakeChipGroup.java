package com.example.kit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class MakeChipGroup extends ChipGroup {
    private final Context context;
    private final ArrayList<String> makes;

    public MakeChipGroup(Context context) {
        super(context);
        this.context = context;
        makes = new ArrayList<>();
    }

    public MakeChipGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        makes = new ArrayList<>();
    }

    public MakeChipGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        makes = new ArrayList<>();
    }

    public void addMake(String make) {
        makes.add(make);
        Chip chip = new Chip(new ContextThemeWrapper(context, R.style.tag_chip));
        ChipDrawable drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.tag_chip);
        chip.setChipDrawable(drawable);
        chip.setEnsureMinTouchTargetSize(false);
        chip.setText(make);
        chip.setPadding(0,0,0,0);
        chip.setChipMinHeightResource(R.dimen.tag_height);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            String chipMake = ((Chip) v).getText().toString();
            removeMake(chipMake);
        });

        addView(chip);
    }

    public void removeMake(String make) {
        makes.remove(make);
    }

    public ArrayList<String> getMakes() {
        return makes;
    }

    public void clear() {
        removeAllViews();
        makes.clear();
    }
}
