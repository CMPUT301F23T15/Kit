package com.example.kit.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.example.kit.R;
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
        Chip chip = new Chip(new ContextThemeWrapper(context, R.style.tag_chip));
        ChipDrawable drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.tag_chip);
        chip.setChipDrawable(drawable);
        chip.setEnsureMinTouchTargetSize(false);
        chip.setText(make);
        chip.setPadding(0,0,0,0);
        chip.setChipMinHeightResource(R.dimen.tag_height);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(this::removeMake);

        addView(chip);
        makes.add(make);
    }

    private void removeMake(View v) {
        String chipMake = ((Chip) v).getText().toString();
        makes.remove(chipMake);
        removeView(v);
    }

    public ArrayList<String> getMakes() {
        return makes;
    }

    public void clear() {
        removeAllViews();
        makes.clear();
    }

    public boolean isEmpty() {
        return makes.isEmpty();
    }
}
