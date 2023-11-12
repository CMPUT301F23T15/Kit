package com.example.kit;


import android.annotation.SuppressLint;

import com.example.kit.command.CommandManager;
import com.example.kit.command.DeleteItemCommand;
import com.example.kit.command.MacroCommand;
import com.example.kit.data.ItemSet;
import com.example.kit.data.source.DataChangedCallback;
import com.example.kit.data.source.DataSourceManager;

import java.math.BigDecimal;
import java.util.HashSet;

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

    public void setAdapter(ItemAdapter adapter) {
        this.adapter = adapter;
        updateAdapterData(this.adapter);
    }

    public void setCallback(ItemSetValueChangedCallback callback) {
        this.callback = callback;
        callback.onItemSetValueChanged(itemSet.getItemSetValue());
    }

    public void deleteCheckedItems(HashSet<Integer> positions) {
        MacroCommand deleteItemsMacro = new MacroCommand();

        // Create a new deletion command for each item that is to be deleted.
        for (int position : positions) {
            deleteItemsMacro.addCommand(new DeleteItemCommand(itemSet.getItem(position)));
        }

        // Delete all items in the command
        CommandManager.getInstance().executeCommand(deleteItemsMacro);
    }

    @Override
    public void onDataChanged() {
        // Get new data and update the adapter with the new dataset
        itemSet = dataSourceManager.getItemDataSource().getDataSet();
        updateAdapterData(adapter);
        // Update the Total Valuation in the fragment
        callback.onItemSetValueChanged(itemSet.getItemSetValue());
    }

    private void updateAdapterData(ItemAdapter adapter) {
        adapter.setItemSet(itemSet);
        // TODO: Find performant way to use more specific data-changed notifiers.
        adapter.notifyDataSetChanged();
    }

    public interface ItemSetValueChangedCallback {
        void onItemSetValueChanged(BigDecimal value);

    }
}