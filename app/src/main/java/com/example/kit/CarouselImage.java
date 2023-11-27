package com.example.kit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.Objects;

public class CarouselImage {
    private final Bitmap image;

    public CarouselImage(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        this.image = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public CarouselImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

}
