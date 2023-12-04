package com.example.kit.data;

import android.graphics.Color;

import androidx.annotation.NonNull;

/**
 * A tag consists of a name, and a color. This data type is
 * used to represent a tag.
 */
public class Tag {
    private String name;
    private Color color;

    /**
     * Minimum constructor for the tag, setting its name with a default color.
     * @param name Name of the tag.
     */
    public Tag(String name) {
        this.name = name;
        color = Color.valueOf(Color.WHITE);
    }

    /**
     * Complete constructor for a tag, settings the name and color.
     * @param name  Name of the tag.
     * @param color Color of the tag.
     */
    public Tag(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Returns the name of this tag.
     * @return The name of this tag.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for this tag.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the color of this tag.
     * @return The color of this tag.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets a new color for this tag.
     * @param color Color value to be set.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
