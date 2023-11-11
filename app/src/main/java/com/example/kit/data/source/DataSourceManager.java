package com.example.kit.data.source;

import com.example.kit.data.Item;
import com.example.kit.data.Tag;

import io.grpc.android.BuildConfig;

public class DataSourceManager {

    private static DataSourceManager instance;
    private final DataSource<Item> itemDataSource;
    private final DataSource<Tag> tagDataSource;

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

    public DataSource<Item> getItemDataSource() {
        return itemDataSource;
    }

    public DataSource<Tag> getTagDataSource() {
        return tagDataSource;
    }
}
