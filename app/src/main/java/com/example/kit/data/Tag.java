package com.example.kit.data;

import android.graphics.Color;

import androidx.annotation.Nullable;

    /**
     * A tag consists of a name, and a color. This data type is
     * used to represent a tag
     */
public class Tag implements Identifiable {
    private String id;
    private String name;
    // Colorlong is what is stored on the database,
    private long colorLong;


    /**
     * Empty constructor for Firestore object storage
     */
    public Tag() {};

    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getColorLong() {
        return colorLong;
    }

    public void setColorLong(long colorLong) {
        this.colorLong = colorLong;
    }

    public Color tagColor() {
        return Color.valueOf(colorLong);
    }

    public void changeColor(Color color) {
        this.colorLong = color.pack();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        // If compared to another Tag, check their ID's to see if they are the same tag
        if (obj instanceof Tag) {
            Tag otherTag = (Tag) obj;
            return this.findID().equals(otherTag.findID());
        }
        return false;
    }

    @Override
    public void attachID(String id) {
        this.id = id;
    }

    @Override
    public String findID() {
        return id;
    }
}
