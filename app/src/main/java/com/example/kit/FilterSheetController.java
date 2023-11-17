package com.example.kit;

import android.text.Editable;
import android.text.TextWatcher;

import com.example.kit.data.Filter;

import java.util.HashSet;

public class FilterSheetController {

    private FilterUpdateCallback callback;
    private final Filter filter;

    public FilterSheetController() {
        filter = new Filter();
        onFilterChanged();
    }

    public TextWatcher createSearchWatcher() {
        return new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                // update Filter
                onFilterChanged();
            }
        };
    }

    public TextWatcher createLowerValueWatcher() {
        return new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                // update Filter
                onFilterChanged();
            }
        };
    }

    public TextWatcher createUpperValueWatcher() {
        return new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                // update Filter
                onFilterChanged();
            }
        };
    }

    public TextWatcher createLowerDateWatcher() {
        return new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                // update Filter
                onFilterChanged();
            }
        };
    }

    public TextWatcher createUpperDateWatcher() {
        return new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                // update Filter
                onFilterChanged();
            }
        };
    }

    public void updateTags(HashSet<String> tags) {
        // update Filter
        onFilterChanged();
    }

    public void updateMakes(HashSet<String> makes) {
        // update Filter
        onFilterChanged();
    }

    public void setCallback(FilterUpdateCallback callback) {
        this.callback = callback;
    }

    private void onFilterChanged() {
        if (callback != null) {
            callback.onFilterChangedCallback(filter);
        }
    }

    public interface FilterUpdateCallback {
        void onFilterChangedCallback(Filter filter);
    }

    /**
     * Abstract class implementing TextWatcher to provide a default empty implementation for the two
     * unneeded methods, beforeTextChanged and onTextChanged. All text watchers will only need the
     * afterTextChanged method. This will reduce code in this class.
     */
    private abstract static class TextWatcherAdapter implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // nothing
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
