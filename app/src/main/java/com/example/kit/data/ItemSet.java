package com.example.kit.data;

import java.math.BigDecimal;
import java.util.ArrayList;



public class ItemSet {
    private ArrayList<Item> items;

    public ItemSet() {
        items = new ArrayList<Item>();
    }

    /**
     * Returns the {@link Item}s within the ItemSet.
     * @return
     *  Returns the Items within the ItemSet as an ArrayList.
     */
    public ArrayList<Item> getItems() {
        return items;
    }


    public Item getItem(int position) {
        return items.get(position);
    }

    /**
     * Calculate the total value of the {@link Item}s within this ItemSet.
     * @return
     *  Returns a BigDecimal value of the items within the ItemSet.
     */
    public BigDecimal getItemSetValue() {
        BigDecimal totalValue = new BigDecimal(0);
        for (Item item : items) {
            totalValue = totalValue.add(item.getValueBigDecimal());
        }
        return totalValue;
    }

    public void addItem(Item item){items.add(item);}

    public void clear() {
        items.clear();
    }
}
