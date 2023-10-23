package com.example.kit.data;

import java.math.BigDecimal;
import java.util.ArrayList;



public class ItemSet {
    private ArrayList<Item> items;

    /**
     * Returns the {@link Item}s within the ItemSet.
     * @return
     *  Returns the Items within the ItemSet as an ArrayList.
     */
    public ArrayList<Item> getItems() {
        return items;
    }


    // this is maybe not good? exposes internal function of the data within the class, but it would
    // be useful so idk
    // -Jesse
    public Item getItem(int position) {
        return items.get(position);
    }

    /**
     * Calculate the total value of the {@link Item}s within this ItemSet.
     * @return
     *  Returns a BigDecimal value of the items within the ItemSet.
     */
    public BigDecimal getItemSetValue() {

        return null;
    }

}
