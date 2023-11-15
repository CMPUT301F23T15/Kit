package com.example.kit.data.source;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.data.Tag;

import java.util.ArrayList;

public abstract class AbstractItemDataSource extends DataSource<Item, ItemSet> {
    protected DataSource<Tag, ArrayList<Tag>> tagDataSource;

    protected void setTagDataSource(DataSource<Tag, ArrayList<Tag>> tagDataSource) {
        this.tagDataSource = tagDataSource;
    }
}
