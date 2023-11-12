package com.example.kit;


import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.command.CommandManager;
import com.example.kit.command.DeleteItemCommand;
import com.example.kit.command.MacroCommand;
import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.data.source.DataChangedCallback;
import com.example.kit.data.source.DataSourceManager;
import com.example.kit.data.source.ItemDataSource;
import com.example.kit.database.ItemFirestoreAdapter;
import com.example.kit.database.ItemViewHolder;

import java.math.BigDecimal;
import java.util.HashSet;

/**
 * A controller class for the {@link ItemListFragment}, managing the {@link ItemFirestoreAdapter}
 * with queries and filtering.
 */
public class ItemListController implements DataChangedCallback {
    private ItemAdapter adapter;
    private ItemSet itemSet;
    private ItemSetValueChangedCallback callback;
    private final DataSourceManager dataSourceManager = DataSourceManager.getInstance();

    public ItemListController() {
        itemSet = new ItemSet();
        // Add the controller as a callback when the ItemDataSource has changes in data
        dataSourceManager.getItemDataSource().setCallback(this);
    }

    /**
     * Provides a reference to the adapter
     *
     * @return Instance of the adapter
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setAdapter(ItemAdapter adapter) {
        this.adapter = adapter;
        this.adapter.setItemSet(itemSet);
        this.adapter.notifyDataSetChanged();
    }

    /**
     * Sets a SelectListener to the Firestore adapter for handling item selection.
     *
     * @param listener The SelectListener to set on the adapter.
     */
    public void setListener(SelectListener listener) {
        adapter.setListener(listener);
    }

    public void deleteCheckedItems(HashSet<Integer> positions) {
        MacroCommand deleteItemsMacro = new MacroCommand();

        for (int position : positions) {
            deleteItemsMacro.addCommand(new DeleteItemCommand(itemSet.getItem(position)));
        }

        CommandManager.getInstance().executeCommand(deleteItemsMacro);
    }

    public void setCallback(ItemSetValueChangedCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onDataChanged() {
        itemSet = dataSourceManager.getItemDataSource().getDataCollection();
        adapter.setItemSet(itemSet);
        adapter.notifyDataSetChanged();
        callback.onItemSetValueChanged(itemSet.getItemSetValue());
    }

    public interface ItemSetValueChangedCallback {
        void onItemSetValueChanged(BigDecimal value);

    }
}