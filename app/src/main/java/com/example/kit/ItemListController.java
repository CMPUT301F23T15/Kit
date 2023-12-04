package com.example.kit;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.kit.command.CommandManager;
import com.example.kit.command.DeleteItemCommand;
import com.example.kit.command.MacroCommand;
import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.data.Tag;
import com.example.kit.data.source.AbstractItemDataSource;
import com.example.kit.data.source.AbstractTagDataSource;
import com.example.kit.data.source.DataChangedCallback;
import com.example.kit.data.source.DataSourceManager;
import com.example.kit.views.MakeChipGroup;
import com.example.kit.views.TagChipGroup;
import com.example.kit.views.TriStateSortButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Controller that handles data management and manipulation for a {@link ItemListFragment}.
 */
public class ItemListController implements DataChangedCallback,
        TagChipGroup.OnTagChipCloseListener,
        MakeChipGroup.OnMakeChipCloseListener {
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
    private ArrayList<Tag> tagFilters;
    private ArrayList<String> makeFilters;



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

//        applyFilters();
    }
    /**
     * Creates and returns an adapter for tags.
     *
     * @param context The current context.
     * @return An ArrayAdapter for tags.
     */
    public ArrayAdapter<String> createTagAdapter(Context context) {
        ArrayList<Tag> dbTags = tagDataSource.getDataSet();
        tagNames.clear();
        for (Tag tag : dbTags) {
            tagNames.add(tag.getName());
        }
        tagAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, tagNames);
        return tagAdapter;
    }
    /**
     * Handles the click event on a tag at a specific position.
     *
     * @param position The position of the clicked tag.
     * @return The {@link Tag} at the specified position.
     */
    public Tag tagClickedAtPosition(int position) {
        String tagNameAtPosition = tagNames.remove(position);
        Tag tagAtPosition = tagDataSource.getDataByID(tagNameAtPosition);
        tagAdapter.notifyDataSetChanged();
        return tagAtPosition;
    }
    /**
     * Handles the event when a new tag filter is entered.
     *
     * @param tagName The name of the entered tag.
     * @return The {@link Tag} that matches the entered name.
     */
    public Tag tagFilterEntered(String tagName) {
        Tag tag = tagDataSource.getDataByID(tagName);
        // Tag not found, don't add new tags from the filter screen
        if (tag == null) return null;

        // Remove the existing tag from the options in the dropdown
        tagNames.remove(tag.getName());
        tagAdapter.notifyDataSetChanged();

        return tag;
    }
    /**
     * Creates and returns an adapter for makes.
     *
     * @param context The current context.
     * @return An ArrayAdapter for makes.
     */
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
    /**
     * Handles the click event on a make at a specific position.
     *
     * @param position The position of the clicked make.
     * @return The make at the specified position.
     */
    public String makeClickedAtPosition(int position) {
        String makeAtPosition = makes.remove(position);
        makeAdapter.notifyDataSetChanged();
        return makeAtPosition;
    }
    /**
     * Callback method when a make chip is closed.
     *
     * @param makeName The name of the make associated with the closed chip.
     */
    @Override
    public void onMakeChipClosed(String makeName) {
        if (!makes.contains(makeName)) {
            makes.add(makeName);
        }
        makeFilters.remove(makeName);
        applyFilters();
    }
    /**
     * Callback method when a tag chip is closed.
     *
     * @param tag The {@link Tag} associated with the closed chip.
     */
    @Override
    public void onTagChipClosed(Tag tag) {
        if(!tagNames.contains(tag.getName())) {
            tagNames.add(tag.getName());
        }
        tagFilters.remove(tag);
        applyFilters();
    }

    /**
     * Interface to register a callback when the value of the {@link ItemSet} changes.
     */
    public interface ItemSetValueChangedCallback { void onItemSetValueChanged(BigDecimal value); }
    /**
     * Updates the keyword filter for the item list.
     *
     * @param keyword The keyword to filter by.
     */
    public void updateKeywordFilter(String keyword) {
        this.currentKeyword = keyword;
        applyFilters();
    }
    /**
     * Updates the date range filter for the item list.
     *
     * @param dateStart The start date of the range.
     * @param dateEnd The end date of the range.
     */
    public void updateDateRangeFilter(String dateStart, String dateEnd) {
        this.currentDateStart = dateStart;
        this.currentDateEnd = dateEnd;
        applyFilters();
    }
    /**
     * Updates the price range filter for the item list.
     *
     * @param priceLow The lower bound of the price range.
     * @param priceHigh The upper bound of the price range.
     */
    public void updatePriceRangeFilter(String priceLow, String priceHigh) {
        this.currentPriceLow = priceLow;
        this.currentPriceHigh = priceHigh;
        applyFilters();
    }
    /**
     * Updates the tag filter for the item list.
     *
     * @param tagFilters The list of {@link Tag} to filter by.
     */
    public void updateTagFilter(ArrayList<Tag> tagFilters) {
        this.tagFilters = tagFilters;
        applyFilters();
    }
    /**
     * Updates the make filter for the item list.
     *
     * @param makeFilters The list of makes to filter by.
     */
    public void updateMakeFilter(ArrayList<String> makeFilters) {
        this.makeFilters = makeFilters;
        applyFilters();
    }
    /**
     * Applies the current set of filters to the item list.
     */
    private void applyFilters() {
        ItemSet filteredSet = itemSet
                .filterByKeyword(currentKeyword)
                .filterByDateRange(currentDateStart, currentDateEnd)
                .filterByPriceRange(currentPriceLow, currentPriceHigh)
                .filterByTags(tagFilters)
                .filterByMakes(makeFilters);

        itemAdapter.setItemSet(filteredSet);
        itemAdapter.notifyDataSetChanged();
        if (callback != null) {
            callback.onItemSetValueChanged(filteredSet.getItemSetValue());
        }
    }
    /**
     * Sorts the items in the list based on the specified sort state and attribute.
     *
     * @param state The sorting state.
     * @param sortAttribute The attribute to sort by.
     */
    public void sortItems(TriStateSortButton.BUTTON_STATE state, String sortAttribute) {
        Comparator<Item> comparator = null;

        switch (sortAttribute) {
            case "keyword":
                comparator = Comparator.comparing(Item::getDescription);
                break;
            case "date":
                comparator = Comparator.comparing(Item::getAcquisitionDate);
                break;
            case "price":
                comparator = Comparator.comparing(Item::valueToBigDecimal);
                break;
            case "tag":
                comparator = (item1, item2) -> {
                    if (item1.getTags().isEmpty() && item2.getTags().isEmpty()) return 0;
                    if (item1.getTags().isEmpty()) return -1;
                    if (item2.getTags().isEmpty()) return 1;

                    return lexicographicalCompare(item1.getTags(), item2.getTags());
                };

                break;
            case "make":
                comparator = Comparator.comparing(Item::getMake);
                break;
        }

        if (comparator != null) {
            if (state == TriStateSortButton.BUTTON_STATE.DESCENDING) {
                comparator = comparator.reversed();
            }

            itemSet.getItems().sort(comparator);
        }

        itemAdapter.notifyDataSetChanged();
    }
    /**
     * Compares two lists of {@link Tag} lexicographically.
     *
     * @param list1 The first list of tags.
     * @param list2 The second list of tags.
     * @return An integer representing the comparison result.
     */
    private int lexicographicalCompare(List<Tag> list1, List<Tag> list2) {
        int minLength = Math.min(list1.size(), list2.size());

        // Compare element by element
        for (int i = 0; i < minLength; i++) {
            int comparison = list2.get(i).toString().compareTo(list1.get(i).toString());
            if (comparison != 0) {
                return comparison;
            }
        }

        // If the common elements are equal, the longer list comes later
        return Integer.compare(list1.size(), list2.size());
    }
}