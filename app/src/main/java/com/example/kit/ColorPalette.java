package com.example.kit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.kit.databinding.ColorPaletteBinding;

public class ColorPalette extends ConstraintLayout {
    private OnColorSplotchClickListener listener;
    private final Context context;
    private ColorPaletteBinding binding;

    public ColorPalette(Context context) {
        super(context);
        this.context = context;
        initialize();
    }

    public ColorPalette(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initialize();
    }

    public ColorPalette(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initialize();
    }

    private void initialize() {
        binding = ColorPaletteBinding.inflate(LayoutInflater.from(context), this, true);
        setColorSplotchClickListeners();
    }

    private void setColorSplotchClickListeners() {
        int childCount = binding.getRoot().getChildCount();
        for (int i = 0; i < childCount; i++) {
            View colorSplotch = binding.getRoot().getChildAt(i);
            colorSplotch.setOnClickListener(splotch -> {
                ColorStateList colorStates = splotch.getBackgroundTintList();
                if (colorStates != null) {
                    listener.onColorSplotchClick(colorStates.getDefaultColor());
                } else {
                    listener.onColorSplotchClick(R.color.white);
                }
            });
        }
    }

    public void setColorSplotchClickListener(OnColorSplotchClickListener listener) {
        this.listener = listener;
    }

    public interface OnColorSplotchClickListener {
        void onColorSplotchClick(int colorInt);
    }
}
