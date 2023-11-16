package com.example.kit.data;

import java.math.BigDecimal;
import java.util.ArrayList;

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
}
