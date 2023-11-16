package com.example.kit.data.source;

import android.util.Log;

import com.example.kit.data.Tag;

import java.util.ArrayList;

import io.grpc.android.BuildConfig;

/**
 * Singleton class that manages references to data sources used throughout the app. Depending on the
 * environment, uses testing data sources or Firestore datasources.
 */
public class DataSourceManager {

    private final AbstractItemDataSource itemDataSource;
    private final DataSource<Tag, ArrayList<Tag>> tagDataSource;

    /**
     * Accessor for the ItemDataSource based on the current environment.
     * @return Reference to the {@link AbstractItemDataSource}
     */
    public AbstractItemDataSource getItemDataSource() {
        return itemDataSource;
    }

    /**
     * Accessor for the TagDataSource based on the current environment.
     * @return Reference to the {@link DataSource} for Tags
     */
    public DataSource<Tag, ArrayList<Tag>> getTagDataSource() {
        return tagDataSource;
    }

    // Singleton Instantiation
    private static DataSourceManager instance;

    /**
     * Constructor that creates the datasources based on the environment. Testing if in debug,
     * Firestore otherwise.
     */
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

        itemDataSource.setTagDataSource(tagDataSource);
    }

    /**
     * Provides access to the instance of the DataSourceManager.
     * @return Instance of DataSourceManager.
     */
    public static synchronized DataSourceManager getInstance() {
        if (instance == null) {
            instance = new DataSourceManager();
        }
        return instance;
    }
}
