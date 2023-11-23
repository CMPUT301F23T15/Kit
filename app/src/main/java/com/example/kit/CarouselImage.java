package com.example.kit;

import androidx.annotation.DrawableRes;

public class CarouselImage {
    @DrawableRes
    private final int drawableRes;

    public CarouselImage(@DrawableRes int drawableRes) {
        this.drawableRes = drawableRes;
    }

    @DrawableRes
    public int getDrawableRes() {
        return drawableRes;
    }
}
