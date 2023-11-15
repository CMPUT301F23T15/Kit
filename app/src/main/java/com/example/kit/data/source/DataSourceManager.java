package com.example.kit.data.source;

import android.util.Log;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.data.Tag;

import java.util.ArrayList;

import io.grpc.android.BuildConfig;

public class DataSourceManager {

    private final AbstractItemDataSource itemDataSource;
    private final DataSource<Tag, ArrayList<Tag>> tagDataSource;

    public DataSource<Item, ItemSet> getItemDataSource() {
        return itemDataSource;
    }

    public DataSource<Tag, ArrayList<Tag>> getTagDataSource() {
        return tagDataSource;
    }

    // Singleton Instantiation
    private static final DataSourceManager instance = new DataSourceManager();

    private DataSourceManager() {
        if (BuildConfig.DEBUG) {
            Log.i("Database", "Using testing DataSources");
            tagDataSource = new TestTagDataSource();
            itemDataSource = new TestItemDataSource();
        } else {
            Log.i("Database", "Using production DataSources");
            tagDataSource = new TagDataSource();
            itemDataSource = new ItemDataSource();
        }

        itemDataSource.tagDataSource = tagDataSource;
    }

    public static synchronized DataSourceManager getInstance() {
        return instance;
    }
}
