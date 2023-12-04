package com.example.kit.data.source;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.data.Tag;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Intermediate subclass of {@link DataSource} for TagDataSources that need access to the Item
 * data sources to search for tags on each item.
 */
public abstract class AbstractTagDataSource extends DataSource<Tag, ArrayList<Tag>> {
    protected DataSource<Item, ItemSet> itemDataSource;

    /**
     * Sets the ItemDataSource to be used by the ItemDataSource
     * @param itemDataSource TagDataSource reference.
     */
    protected void setItemDataSource(DataSource<Item, ItemSet> itemDataSource) {
        this.itemDataSource = itemDataSource;
    }

    /**
     * Removes any tags that are not present on any of the Items from the ItemSource.
     */
    protected void cullUnusedTags() {
        HashSet<Tag> usedTags = new HashSet<>();
        HashSet<Tag> allTags = new HashSet<>(getDataSet());

        // Add all tags from each item to the usedTags set
        ItemSet items = itemDataSource.getDataSet();
        int itemCount = items.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            usedTags.addAll(items.getItem(i).getTags());
        }

        // Remove the used tags from the allTags set, leaving only the unused tags
        allTags.removeAll(usedTags);

        // Remove the remaining tags from the datasource
        for (Tag tag : allTags) {
            deleteDataByID(tag.getName());
        }
    }
}
