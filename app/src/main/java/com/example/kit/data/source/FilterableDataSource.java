package com.example.kit.data.source;

import com.example.kit.data.Filter;

public interface FilterableDataSource<T, C> {

    C getFilteredDataSet(Filter filter);
}
