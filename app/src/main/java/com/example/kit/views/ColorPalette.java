package com.example.kit.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.kit.R;
import com.example.kit.databinding.ColorPaletteBinding;

/**
 * A Constraint Layout that contains a grid of 18 "Color Splotches," and provides a callback with
 * the color of whichever splotch was clicked.
 */
public class ColorPalette extends ConstraintLayout {
    private final Context context;
    private ColorPaletteBinding binding;
    private OnColorSplotchClickListener listener;

    /**
     * Standard constructor, initializes the Palette
     */
    public ColorPalette(Context context) {
        super(context);
        this.context = context;
        initialize();
    }

    /**
     * Standard constructor, initializes the Palette
     */
    public ColorPalette(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initialize();
    }

    /**
     * Standard constructor, initializes the Palette
     */
    public ColorPalette(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initialize();
    }

    /**
     * Initialize the Palette, inflates the view and adds the click listeners
     */
    private void initialize() {
        binding = ColorPaletteBinding.inflate(LayoutInflater.from(context), this, true);
        setColorSplotchClickListeners();
    }

    /**
     * For each ColorSplotch, add a click listener that calls the callback with its color. Defaults
     * to white if there is an error
     */
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

    /**
     * Register the {@link OnColorSplotchClickListener} for this Palette
     * @param listener The callback listener
     */
    public void setColorSplotchClickListener(OnColorSplotchClickListener listener) {
        this.listener = listener;
    }

    /**
     * Interface for listening to ColorSplotch clicks
     */
    public interface OnColorSplotchClickListener {
        void onColorSplotchClick(int colorInt);
    }
}
