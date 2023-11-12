package com.example.kit.data.source;

public abstract class DataSource<T, C> {

    private DataChangedCallback callback;

    public abstract void addData(T newData);

    public abstract void deleteDataByID(String id);

    public abstract T getDataByID(String id);

    public abstract C getDataSet();

    public void setCallback(DataChangedCallback callback) {
        this.callback = callback;
    }

    protected void onDataChanged() {
        if (callback != null) {
            callback.onDataChanged();
        }
    }
}
