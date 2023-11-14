package com.example.kit.data.source;

import android.util.Log;

import com.example.kit.data.Tag;

import java.util.ArrayList;
import java.util.HashMap;

public class TestTagDataSource extends DataSource<Tag, ArrayList<Tag>> {

    private final HashMap<String, Tag> tags;

    public TestTagDataSource() {
        tags = new HashMap<>();
    }

    @Override
    public void addData(Tag tag) {
//        tags.put(tag.findID(), tag);
    }

    // Maybe throw errors if tag not found?
    @Override
    public void deleteDataByID(String id) {
        Tag removedTag = tags.remove(id);
        if (removedTag == null) {
            Log.w("Test Database", "Tag was not found with ID: " + id);
        }
    }

    // Throw error if tag not found?
    @Override
    public Tag getDataByID(String id) {
        return tags.get(id);
    }

    @Override
    public ArrayList<Tag> getDataSet() {
        return new ArrayList<>(tags.values());
    }
}
