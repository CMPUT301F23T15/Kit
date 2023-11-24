package com.example.kit.data.source;

import com.example.kit.data.Filter;
import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.data.Tag;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Intermediate subclass of {@link DataSource} for ItemDataSources that need access to the Tag
 * data sources to retrieve tag objects from the names. Necessary because there was a race condition
 * when the tag datasource was not set explicitly after the construction of both types of
 * data sources.
 */
public abstract class AbstractItemDataSource extends DataSource<Item, ItemSet> implements FilterableDataSource<Item, ItemSet> {
    protected DataSource<Tag, ArrayList<Tag>> tagDataSource;
    protected HashMap<String, Item> itemCache;

    /**
     * Sets the TagDataSource to be used by the ItemDataSource
     * @param tagDataSource
     */
    protected void setTagDataSource(DataSource<Tag, ArrayList<Tag>> tagDataSource) {
        this.tagDataSource = tagDataSource;
    }

    // TODO: IMPLEMENT ITEM FILTERING HERE @HASSAN
    @Override
    public ItemSet getFilteredDataSet(Filter filter) {
        return null;
    }
}
