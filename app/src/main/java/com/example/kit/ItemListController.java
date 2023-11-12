package com.example.kit;


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
import com.example.kit.database.ItemFirestoreAdapter;
import com.example.kit.database.ItemViewHolder;

import java.math.BigDecimal;

/**
 * A controller class for the {@link ItemListFragment}, managing the {@link ItemFirestoreAdapter}
 * with queries and filtering.
 */
public class ItemListController implements DataChangedCallback {
    private ItemAdapter adapter;
    private ItemSet itemSet;
    private ItemSetValueChangedCallback callback;

    private ItemListController() {
        itemSet = new ItemSet();
        // Add the controller as a callback when the ItemDataSource has changes in data
        DataSourceManager.getInstance().getItemDataSource().setCallback(this);
    }

    /**
     * Provides a reference to the adapter
     *
     * @return Instance of the adapter
     */
    public void setAdapter(ItemAdapter adapter) {
        this.adapter = adapter;
        this.adapter.setItemSet(itemSet);
    }

    /**
     * Sets a SelectListener to the Firestore adapter for handling item selection.
     *
     * @param listener The SelectListener to set on the adapter.
     */
    public void setListener(SelectListener listener) {
        adapter.setListener(listener);
    }

    public void deleteCheckedItems() {
        int numItems = itemSet.getItemsCount();
        MacroCommand deleteItemsMacro = new MacroCommand();

        for (int i = 0; i < numItems; i++){
            deleteItemsMacro.addCommand(new DeleteItemCommand(itemSet.getItem(i)));
        }

        CommandManager.getInstance().executeCommand(deleteItemsMacro);
    }

    public void setCallback(ItemSetValueChangedCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onDataChanged() {
        itemSet = DataSourceManager.getInstance().getItemDataSource().getDataCollection();
        callback.onItemSetValueChanged(itemSet.getItemSetValue());
    }

    public interface ItemSetValueChangedCallback {
        void onItemSetValueChanged(BigDecimal value);

    }
}