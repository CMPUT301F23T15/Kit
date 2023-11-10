package com.example.kit.data;

import android.graphics.Color;

import androidx.annotation.Nullable;

    /**
     * A tag consists of a name, and a color. This data type is
     * used to represent a tag
     */
public class Tag {
    private String name;
    private Color color;

    // TODO: Implement for use in ArrayList<Tag>.contains()
    @Override
    public boolean equals(@Nullable Object obj) {
        return false;
    }
}
