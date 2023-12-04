package com.example.kit.data.source;

import com.example.kit.data.Item;

import java.util.HashMap;

public class BarcodeDataSource{
    private HashMap<String, Item> cache;

    public BarcodeDataSource() {
        cache = new HashMap<>();
        Item limaBeans = new Item();
        limaBeans.setName("Lima Beans");
        limaBeans.setMake("Compliments");
        limaBeans.setDescription("398mL Lima Beans");
        limaBeans.setValue("3.50");
        cache.put("055742358506", limaBeans);
    }
    public Item getItemByBarcode(String barcode){
        return cache.get(barcode);
    }
}
