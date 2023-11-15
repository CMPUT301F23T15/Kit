package com.example.kit.data;

import android.graphics.Color;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.HashMap;

/**
     * A tag consists of a name, and a color. This data type is
     * used to represent a tag
     */
public class Tag {
    private String name;
    private Color color;

    public Tag(String name) {
        this.name = name;
    }

    public Tag(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

//        @Override
//    public boolean equals(@Nullable Object obj) {
//        // If compared to another Tag, check their ID's to see if they are the same tag
//        if (obj instanceof Tag) {
//            Tag otherTag = (Tag) obj;
//            return this.findID().equals(otherTag.findID());
//        }
//        return false;
//    }
}
