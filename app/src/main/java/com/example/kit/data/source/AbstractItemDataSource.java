package com.example.kit.data.source;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.data.Tag;

import java.util.ArrayList;

/**
 * Intermediate subclass of {@link DataSource} for ItemDataSources that need access to the Tag
 * data sources to retrieve tag objects from the names. Necessary because there was a race condition
 * when the tag datasource was not set explicitly after the construction of both types of
 * data sources.
 */
public abstract class AbstractItemDataSource extends DataSource<Item, ItemSet> {
    protected DataSource<Tag, ArrayList<Tag>> tagDataSource;

    /**
     * Sets the TagDataSource to be used by the ItemDataSource
     * @param tagDataSource TagDataSource reference.
     */
    protected void setTagDataSource(DataSource<Tag, ArrayList<Tag>> tagDataSource) {
        this.tagDataSource = tagDataSource;
    }
}
