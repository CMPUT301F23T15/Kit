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

    public Tag(String name, Color color ) {
        this.name = name;
        this.color = color;
    }

    public Tag(String name, HashMap<String, Object> pack) {
        this.name = name;
        Color tagColor;

        Double red = (Double) pack.get("red");
        Double green = (Double) pack.get("green");
        Double blue = (Double) pack.get("blue");
        Double alpha = (Double) pack.get("alpha");

        if (red == null || green == null || blue == null || alpha == null) {
            Log.e("Tag Color", "Tag attempted to be constructed from a null color pack.");
            tagColor = new Color();
        } else {
            tagColor = Color.valueOf(red.floatValue(), green.floatValue(), blue.floatValue(), alpha.floatValue());
        }

        this.color = tagColor;
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

    public HashMap<String, Float> pack() {
        HashMap<String, Float> packed = new HashMap<>();
        packed.put("alpha", color.alpha());
        packed.put("red", color.red());
        packed.put("green", color.green());
        packed.put("blue", color.blue());

        return packed;
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
