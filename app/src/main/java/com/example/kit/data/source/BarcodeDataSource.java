package com.example.kit.data.source;

import com.example.kit.data.Item;

import java.util.HashMap;

public class BarcodeDataSource{
    private final HashMap<String, Item> cache;

    public BarcodeDataSource() {
        cache = new HashMap<>();
        Item limaBeans = new Item();
        limaBeans.setName("Lima Beans");
        limaBeans.setMake("Compliments");
        limaBeans.setDescription("398mL Lima Beans");
        limaBeans.setValue("3.50");
        cache.put("055742358506", limaBeans);
        Item kraftDinner = new Item();
        kraftDinner.setName("Kraft Dinner");
        kraftDinner.setMake("Kraft");
        kraftDinner.setDescription("340g");
        kraftDinner.setValue("5");
        cache.put("04050032766000",kraftDinner);
    }

    public Item getItemByBarcode(String barcode){
        return cache.get(barcode);
    }

    public boolean isItemInCache(String barcode){
        Item item = cache.get(barcode);
        if(item == null){
            return false;
        } else {
            return true;
        }
    }
}
