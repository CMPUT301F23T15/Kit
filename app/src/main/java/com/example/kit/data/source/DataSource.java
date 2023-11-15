package com.example.kit.data.source;

/**
 * Datasource class providing addition, deletion, and access to a datatype and a collection of data.
 * @param <T> Singular datatype class
 * @param <C> Collection type for the Singular datatype.
 */
public abstract class DataSource<T, C> {

    private DataChangedCallback callback;

    public abstract void addData(T newData);

    public abstract void deleteDataByID(String id);

    public abstract T getDataByID(String id);

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
