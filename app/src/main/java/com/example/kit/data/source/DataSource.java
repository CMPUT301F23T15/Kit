package com.example.kit.data.source;

/**
 * Datasource class providing addition, deletion, and access to a datatype and a collection of data.
 * @param <T> Singular datatype class
 * @param <C> Collection type for the Singular datatype.
 */
public abstract class DataSource<T, C> {

    private DataChangedCallback callback;

    /**
     * Adds the given data object to the data source.
     * @param newData Data object to be added.
     */
    public abstract void addData(T newData);

    /**
     * Deletes the data object associated with the given ID from the data source.
     * @param id The ID of the data object to be deleted.
     */
    public abstract void deleteDataByID(String id);

    /**
     * Access one object of the data given its ID.
     * @param id The ID of the data object.
     * @return The data object associated with the given ID.
     */
    public abstract T getDataByID(String id);

    /**
     * Access the entire Collection of Data from the data source.
     * @return Collection of all data entries.
     */
    public abstract C getDataSet();

    /**
     * Registers a {@link DataChangedCallback} listener.
     * @param callback The listener to be registered.
     */
    public void setCallback(DataChangedCallback callback) {
        this.callback = callback;
    }

    /**
     * Calls the callback function on the listener.
     */
    protected void onDataChanged() {
        if (callback != null) {
            callback.onDataChanged();
        }
    }
}
