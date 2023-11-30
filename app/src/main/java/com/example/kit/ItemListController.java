package com.example.kit;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.kit.command.AddTagCommand;
import com.example.kit.command.CommandManager;
import com.example.kit.command.DeleteItemCommand;
import com.example.kit.command.MacroCommand;
import com.example.kit.data.Filter;
import com.example.kit.data.ItemSet;
import com.example.kit.data.Tag;
import com.example.kit.data.source.AbstractItemDataSource;
import com.example.kit.data.source.AbstractTagDataSource;
import com.example.kit.data.source.DataChangedCallback;
import com.example.kit.data.source.DataSourceManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Controller that handles data management and manipulation for a {@link ItemListFragment}.
 */
public class ItemListController implements DataChangedCallback {
    private ItemAdapter itemAdapter;
    private ItemSet itemSet;
    private ItemSetValueChangedCallback callback;
    private final AbstractTagDataSource tagDataSource = DataSourceManager.getInstance().getTagDataSource();
    private final AbstractItemDataSource itemDataSource = DataSourceManager.getInstance().getItemDataSource();
    private ArrayAdapter<String> tagAdapter;
    private ArrayList<String> tagNames;
    private ArrayAdapter<String> makeAdapter;
    private final ArrayList<String> makes;
    private String currentKeyword = "";
    private String currentDateStart = "";
    private String currentDateEnd = "";
    private String currentPriceLow = "";
    private String currentPriceHigh = "";


    /**
     * Initializes the {@link ItemSet} and attaches the controller as a {@link DataChangedCallback}
     * for the ItemDataSource.
     */
    public ItemListController() {
        itemSet = new ItemSet();
        tagNames = new ArrayList<>();
        makes = new ArrayList<>();
        // Add the controller as a callback when the ItemDataSource has changes in data
        itemDataSource.setCallback(this);
        tagDataSource.setCallback(this);
    }

    /**
     * Registers the adapter from the ListFragment
     * @param adapter Adapter for the RecyclerView on the ItemListFragment.
     */
    public void setItemAdapter(ItemAdapter adapter) {
        this.itemAdapter = adapter;
        updateItemAdapterData();
    }

    /**
     * Helper method to update the {@link ItemSet} for the adapter.
     */
    private void updateItemAdapterData() {
        itemAdapter.setItemSet(itemSet);
        // TODO: Find performant way to use more specific data-changed notifiers.
        itemAdapter.notifyDataSetChanged();
    }

    public void updateDataFilter(Filter filter) {

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
     * Fetches the IDs of the items at the specified positions in the adapter
     * @param positions Positions of items in the adapter
     * @return The corrisponding Item IDs
     */
    public HashSet<String> getItemIDsAtPositions(HashSet<Integer> positions) {
        HashSet<String> itemIDs = new HashSet<>();
        for (int pos : positions) {
            itemIDs.add(itemSet.getItem(pos).findID());
        }
        return itemIDs;
    }

    /**
     * Callback method for the {@link DataChangedCallback} called whenever the data from the
     * {@link com.example.kit.data.source.DataSource} changes. Updates the {@link ItemSet} and
     * adapter. Relays the call to the {@link ItemSetValueChangedCallback}
     * as the data changed likely means the total value has changed too.
     */
    @Override
    public void onDataChanged() {
        // Get new data and update the adapter with the new dataset
        itemSet = itemDataSource.getDataSet();
        updateItemAdapterData();
        // Update the Total Valuation in the fragment
        callback.onItemSetValueChanged(itemSet.getItemSetValue());

        ArrayList<Tag> dbTags = tagDataSource.getDataSet();
        tagNames.clear();
        for (Tag tag : dbTags) {
            tagNames.add(tag.getName());
        }

        ItemSet dbItems = itemDataSource.getDataSet();
        makes.clear();
        for (int i = 0; i < dbItems.getItemCount(); i++) {
            String make = dbItems.getItem(i).getMake();
            if (make == null || make.isEmpty()) continue;

            if (!makes.contains(make)) makes.add(make);
        }
    }

    public ArrayAdapter<String> createTagAdapter(Context context) {
        ArrayList<Tag> dbTags = tagDataSource.getDataSet();
        tagNames.clear();
        for (Tag tag : dbTags) {
            tagNames.add(tag.getName());
        }
        tagAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, tagNames);
        return tagAdapter;
    }

    public void addTagToAdapter(Tag tag) {
        if(!tagNames.contains(tag.getName())) {
            tagNames.add(tag.getName());
        }
    }

    public Tag tagClickedAtPosition(int position) {
        String tagNameAtPosition = tagNames.remove(position);
        Tag tagAtPosition = tagDataSource.getDataByID(tagNameAtPosition);
        tagAdapter.notifyDataSetChanged();
        return tagAtPosition;
    }

    public Tag tagFilterEntered(String tagName) {
        Tag tag = tagDataSource.getDataByID(tagName);
        // Tag not found, don't add new tags from the filter screen
        if (tag == null) return null;

        // Remove the existing tag from the options in the dropdown
        tagNames.remove(tag.getName());
        tagAdapter.notifyDataSetChanged();

        return tag;
    }

    public ArrayAdapter<String> createMakeAdapter(Context context) {
        ItemSet dbItems = itemDataSource.getDataSet();
        makes.clear();
        for (int i = 0; i < dbItems.getItemCount(); i++) {
            String make = dbItems.getItem(i).getMake();
            if (make == null || make.isEmpty()) continue;

            if (!makes.contains(make)) makes.add(make);
        }
        makeAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, makes);
        return makeAdapter;
    }

    public void addMakeToAdapter(String make) {
        if (!makes.contains(make)) {
            makes.add(make);
        }
    }

    public String makeClickedAtPosition(int position) {
        String makeAtPosition = makes.remove(position);
        makeAdapter.notifyDataSetChanged();
        return makeAtPosition;
    }

    /**
     * Interface to register a callback when the value of the {@link ItemSet} changes.
     */
    public interface ItemSetValueChangedCallback { void onItemSetValueChanged(BigDecimal value); }

    public void updateKeywordFilter(String keyword) {
        this.currentKeyword = keyword;
        applyFilters();
    }

    public void updateDateRangeFilter(String dateStart, String dateEnd) {
        this.currentDateStart = dateStart;
        this.currentDateEnd = dateEnd;
        applyFilters();
    }

    public void updatePriceRangeFilter(String priceLow, String priceHigh) {
        this.currentPriceLow = priceLow;
        this.currentPriceHigh = priceHigh;
        applyFilters();
    }

    // Applies all filters to the dataset
    private void applyFilters() {
        ItemSet filteredSet = itemSet
                .filterByKeyword(currentKeyword)
                .filterByDateRange(currentDateStart, currentDateEnd)
                .filterByPriceRange(currentPriceLow, currentPriceHigh);
        itemAdapter.setItemSet(filteredSet);
        itemAdapter.notifyDataSetChanged();
    }

}