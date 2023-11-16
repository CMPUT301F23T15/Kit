package com.example.kit;

import com.example.kit.command.CommandManager;
import com.example.kit.command.DeleteItemCommand;
import com.example.kit.command.MacroCommand;
import com.example.kit.data.ItemSet;
import com.example.kit.data.source.DataChangedCallback;
import com.example.kit.data.source.DataSourceManager;

import java.math.BigDecimal;
import java.util.HashSet;

/**
 * Controller that handles data management and manipulation for a {@link ItemListFragment}.
 */
public class ItemListController implements DataChangedCallback {
    private ItemAdapter adapter;
    private ItemSet itemSet;
    private ItemSetValueChangedCallback callback;
    private final DataSourceManager dataSourceManager = DataSourceManager.getInstance();

    /**
     * Initializes the {@link ItemSet} and attaches the controller as a {@link DataChangedCallback}
     * for the ItemDataSource.
     */
    public ItemListController() {
        itemSet = new ItemSet();
        // Add the controller as a callback when the ItemDataSource has changes in data
        dataSourceManager.getItemDataSource().setCallback(this);
    }

    /**
     * Registers the adapter from the ListFragment
     * @param adapter Adapter for the RecyclerView on the ItemListFragment.
     */
    public void setAdapter(ItemAdapter adapter) {
        this.adapter = adapter;
        updateAdapterData();
    }

    /**
     * Registers a callback to be called when the data set changes. Calls the callback method for
     * the initial value.
     * @param callback The {@link ItemSetValueChangedCallback} to be registered.
     */
    public void setCallback(ItemSetValueChangedCallback callback) {
        this.callback = callback;
        onDataChanged();
    }

    /**
     * Deletes items from the data source based on the positions of items within the adapter.
     * @param positions Checked item positions to be deleted.
     */
    public void deleteCheckedItems(HashSet<Integer> positions) {
        MacroCommand deleteItemsMacro = new MacroCommand();

        // Create a new deletion command for each item that is to be deleted.
        for (int position : positions) {
            deleteItemsMacro.addCommand(new DeleteItemCommand(itemSet.getItem(position)));
        }

        // Delete all items in the command
        CommandManager.getInstance().executeCommand(deleteItemsMacro);
    }

    /**
     * Call back method for the {@link DataChangedCallback} called whenever the data from the
     * {@link com.example.kit.data.source.DataSource} changes. Updates the {@link ItemSet} and
     * adapter. Relays the call to the {@link ItemSetValueChangedCallback}
     * as the data changed likely means the total value has changed too.
     */
    @Override
    public void onDataChanged() {
        // Get new data and update the adapter with the new dataset
        itemSet = dataSourceManager.getItemDataSource().getDataSet();
        updateAdapterData();
        // Update the Total Valuation in the fragment
        callback.onItemSetValueChanged(itemSet.getItemSetValue());
    }

    /**
     * Helper method to update the {@link ItemSet} for the adapter.
     */
    private void updateAdapterData() {
        adapter.setItemSet(itemSet);
        // TODO: Find performant way to use more specific data-changed notifiers.
        adapter.notifyDataSetChanged();
    }

    /**
     * Interface to register a callback when the value of the {@link ItemSet} changes.
     */
    public interface ItemSetValueChangedCallback { void onItemSetValueChanged(BigDecimal value); }
}