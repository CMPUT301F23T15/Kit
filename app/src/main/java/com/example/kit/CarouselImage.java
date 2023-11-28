package com.example.kit;

import android.graphics.Bitmap;

/**
 * Simple dataclass for representation in a {@link CarouselImageViewHolder}. Stores a bitmap image
 */
public class CarouselImage {
    private final Bitmap image;

    /**
     * Construct a CarouselImage object from a Bitmap
     * @param image The Bitmap to store in this object
     */
    public CarouselImage(Bitmap image) {
        this.image = image;
    }

    /** Return the Bitmap image
     * @return The Bitmap image
     */
    public Bitmap getImage() {
        return image;
    }

}
