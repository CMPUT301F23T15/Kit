package com.example.kit.data.source;

public interface DataSource<T> {

    void addData(T newData);

    void deleteDataByID(String id);

    T getDataByID(String id);

}
