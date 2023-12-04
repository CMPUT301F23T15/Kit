package com.example.kit.data;

import com.example.kit.util.FormatUtils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Collection representing a collection of {@link Item}, that can calculate the total value
 * of the items in the set.
 */
public class ItemSet {
    private final ArrayList<Item> items;

    /**
     * Basic constructor, initializes an empty set of Items
     */
    public ItemSet() {
        items = new ArrayList<>();
    }

    /**
     * Gets an {@link Item} at a given position in the ItemSet
     * @param position Position of the item.
     * @return {@link Item} at the given position.
     */
    public Item getItem(int position) {
        return items.get(position);
    }

    /**
     * Add a {@link Item} to the ItemSet via an item and the ID
     * @param item The Item to be added
     */
    public void addItem(Item item){
        items.add(item);
    }

    /**
     * Removes an {@link Item} from the ItemSet
     * @param item {@link Item} to removed
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * Returns the number of {@link Item}s in the ItemSet
     * @return The number of items in the set
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * Clear the ItemSet
     */
    public void clear() {
        items.clear();
    }

    /**
     * Calculate the total value of the {@link Item}s within this ItemSet.
     * @return Returns a BigDecimal value of the items within the ItemSet.
     */
    public BigDecimal getItemSetValue() {
        BigDecimal totalValue = new BigDecimal(0);
        for (Item item : items) {
            totalValue = totalValue.add(item.valueToBigDecimal());
        }
        return totalValue;
    }
    /**
     * Filters the current set of {@link Item}s based on a keyword.
     * Both the item name and description are checked against the keyword..
     *
     * @param keyword The keyword to filter the items by.
     * @return An {@link ItemSet} containing items that match the keyword.
     */
    public ItemSet filterByKeyword(String keyword) {
        ItemSet filteredSet = new ItemSet();
        String trimmedKeyword = keyword.trim();

        filteredSet.items.addAll(
                this.items.stream()
                        .filter(item -> containsIgnoreCase(item.getName(), trimmedKeyword) ||
                                containsIgnoreCase(item.getDescription(), trimmedKeyword))
                        .collect(Collectors.toList())
        );
        return filteredSet;
    }

    private boolean containsIgnoreCase(String source, String toFind) {
        return source != null && toFind != null &&
                source.trim().toLowerCase().contains(toFind.toLowerCase());
    }

    /**
     * Filters the current set of {@link Item}s based on a specified date range.
     * Items with an acquisition date within the specified range are included in the result.
     *
     * @param lowerDateString The lower bound of the date range in string format.
     * @param upperDateString The upper bound of the date range in string format.
     * @return An {@link ItemSet} containing items within the specified date range.
     */
    public ItemSet filterByDateRange(String lowerDateString, String upperDateString) {
        ItemSet filteredSet = new ItemSet();

        Date lowerDate = FormatUtils.parseDateString(lowerDateString);
        Date upperDate = FormatUtils.parseDateString(upperDateString);

        filteredSet.items.addAll(
                this.items.stream()
                        .filter(item -> {
                            Date acquisitionDate = item.getAcquisitionDate().toDate();
                            return (lowerDate == null || acquisitionDate.equals(lowerDate) || acquisitionDate.after(lowerDate)) &&
                                    (upperDate == null || acquisitionDate.equals(upperDate) || acquisitionDate.before(upperDate));
                        })
                        .collect(Collectors.toList())
        );

        return filteredSet;
    }
    /**
     * Filters the current set of {@link Item}s based on a specified price range.
     * Items with a value within the specified range are included in the result.
     *
     * @param lowerPriceString The lower bound of the price range as a string.
     * @param upperPriceString The upper bound of the price range as a string.
     * @return An {@link ItemSet} containing items within the specified price range.
     */
    public ItemSet filterByPriceRange(String lowerPriceString, String upperPriceString) {
        ItemSet filteredSet = new ItemSet();
        BigDecimal lowerPrice = new BigDecimal(lowerPriceString.isEmpty() ? "0" : lowerPriceString);
        BigDecimal upperPrice = new BigDecimal(upperPriceString.isEmpty() ? String.valueOf(BigDecimal.valueOf(Long.MAX_VALUE)) : upperPriceString);

        filteredSet.items.addAll(
                this.items.stream()
                        .filter(item -> {
                            BigDecimal itemValue = item.valueToBigDecimal();
                            return (itemValue.compareTo(lowerPrice) >= 0) &&
                                    (itemValue.compareTo(upperPrice) <= 0);
                        })
                        .collect(Collectors.toList())
        );

        return filteredSet;
    }
    /**
     * Filters the current set of {@link Item}s based on a set of tags.
     * Only items containing all specified tags are included in the result.
     *
     * @param tags An ArrayList of {@link Tag} objects to filter the items by.
     * @return An {@link ItemSet} containing items that match all the provided tags.
     */
    public ItemSet filterByTags(ArrayList<Tag> tags) {
        if (tags == null || tags.isEmpty()) return this;
        ItemSet filteredSet = new ItemSet();
        filteredSet.items.addAll(
                this.items.stream()
                        .filter(item -> item.getTags().containsAll(tags)).collect(Collectors.toList()));

        return filteredSet;
    }
    /**
     * Filters the current set of {@link Item}s based on a list of makes.
     * Only items whose make matches one of the specified makes are included in the result.
     *
     * @param makes An ArrayList of strings representing the makes to filter the items by.
     * @return An {@link ItemSet} containing items that match any of the provided makes.
     */
    public ItemSet filterByMakes(ArrayList<String> makes) {
        if (makes == null || makes.isEmpty()) return this;
        ItemSet filteredSet = new ItemSet();
        filteredSet.items.addAll(
                this.items.stream()
                        .filter(item -> makes.contains(item.getMake())).collect(Collectors.toList()));

        return filteredSet;
    }

    public void sortItems(Comparator<Item> comparator) {
        if (comparator != null) {
            Collections.sort(items, comparator);
        }
    }
    /**
     * Retrieves the list of {@link Item}s in the ItemSet.
     *
     * @return An ArrayList of {@link Item}s currently in the ItemSet.
     */
    public ArrayList<Item> getItems() {
        return items;
    }


}
