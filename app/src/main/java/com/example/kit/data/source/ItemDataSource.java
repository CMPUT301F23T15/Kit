package com.example.kit.data.source;

import android.util.Log;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.data.FirestoreManager;
import com.example.kit.data.Tag;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ItemDataSource extends AbstractItemDataSource {

    private final CollectionReference itemCollection;
    private final HashMap<String, Item> itemCache;

    public ItemDataSource() {
        itemCollection = FirestoreManager.getInstance().getCollection("Items");
        itemCache = new HashMap<>();
        itemCollection.addSnapshotListener((itemSnapshots, error) -> {
            if (itemSnapshots == null) {
                Log.e("Database", "SnapshotListener null query result");
                return;
            }

            itemCache.clear();
            for (QueryDocumentSnapshot itemSnapshot: itemSnapshots) {
//                Item item = itemSnapshot.toObject(Item.class);
//                item.attachID(itemSnapshot.getId());
                Item item = buildItem(itemSnapshot);
                itemCache.put(item.findID(), item);
            }
            onDataChanged();
        });
    }

    @Override
    public void addData(Item newItem) {
        // Add new item and generate ID if it doesn't have one
        if (newItem.findID() == null || newItem.findID().isEmpty()) {
            itemCollection.add(packItem(newItem))
                    // Log if fails for some reason
                    .addOnFailureListener(exception -> Log.w("Database", "Failed to add new item."));
        } else {
            // Item already has ID, update the existing document
            itemCollection.document(newItem.findID()).set(packItem(newItem))
                    // Log if fails for some reason
                    .addOnFailureListener(exception -> Log.w("Database", "Failed to set item with ID: " + newItem.findID()));
        }
    }

    @Override
    public void deleteDataByID(String id) {
        itemCollection.document(id).delete()
                .addOnSuccessListener(listener -> Log.d("Database", "Document successfully deleted."))
                .addOnFailureListener(exception -> Log.w("Database", exception));
    }

    @Override
    public Item getDataByID(String id) {
        return itemCache.get(id);
    }

    @Override
    public ItemSet getDataSet() {
        ItemSet itemSet = new ItemSet();
        for (Item item : itemCache.values()) {
            itemSet.addItem(item);
        }
        return itemSet;
    }

    private HashMap<String, Object> packItem(Item item) {
        HashMap<String, Object> packedItem = new HashMap<>();
        packedItem.put("name", item.getName());
        packedItem.put("acquisitionDate", item.getAcquisitionDate());
        packedItem.put("comment", item.getComment());
        packedItem.put("description", item.getDescription());
        packedItem.put("value", item.getValue());
        packedItem.put("make", item.getMake());
        packedItem.put("model", item.getModel());
        packedItem.put("serialNumber", item.getSerialNumber());

        List<String> tagNames = new ArrayList<>();
        for (Tag tag : item.getTags()) {
            tagNames.add(tag.getName());
        }

        packedItem.put("tags", tagNames);

        return packedItem;
    }

    private Item buildItem(DocumentSnapshot itemSnapshot) {
        Item item = new Item();
        item.attachID(itemSnapshot.getId());
        item.setName((String) itemSnapshot.get("name"));
        item.setAcquisitionDate((Timestamp) itemSnapshot.get("acquisitionDate"));
        item.setValue((String) itemSnapshot.get("value"));
        item.setDescription((String) itemSnapshot.get("description"));
        item.setComment((String) itemSnapshot.get("comment"));
        item.setMake((String) itemSnapshot.get("make"));
        item.setModel((String) itemSnapshot.get("model"));
        item.setSerialNumber((String) itemSnapshot.get("serialNumber"));

        // Add Tag objects from the Tag DataSource
        List<String> tagNames = getTagList(itemSnapshot);
        for (String tagName : tagNames) {
            item.addTag(tagDataSource.getDataByID(tagName));
        }

        return item;
    }

    @SuppressWarnings("unchecked")
    private static List<String> getTagList(DocumentSnapshot itemSnapshot) {
        Object fieldValue = itemSnapshot.get("tags");
        if (fieldValue instanceof List<?>) {
            List<?> list = (List<?>) fieldValue;
            if (!list.isEmpty() && list.get(0) instanceof String) {
                return (List<String>) fieldValue;
            }
        }
        return Collections.emptyList();
    }
}
