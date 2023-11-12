package com.example.kit.data.source;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.data.Tag;

import java.util.ArrayList;

import io.grpc.android.BuildConfig;

public class DataSourceManager {

    private final DataSource<Item, ItemSet> itemDataSource;
    private final DataSource<Tag, ArrayList<Tag>> tagDataSource;

    public DataSource<Item, ItemSet> getItemDataSource() {
        return itemDataSource;
    }

    public DataSource<Tag, ArrayList<Tag>> getTagDataSource() {
        return tagDataSource;
    }

    // Singleton Instantiation
    private static DataSourceManager instance;

    private DataSourceManager() {
        if (BuildConfig.DEBUG) {
            itemDataSource = new TestItemDataSource();
            tagDataSource = new TestTagDataSource();
        } else {
            itemDataSource = new ItemDataSource();
            tagDataSource = new TagDataSource();
        }
    }

    public static synchronized DataSourceManager getInstance() {
        if (instance == null) {
            instance = new DataSourceManager();
        }
        return instance;
    }
}
