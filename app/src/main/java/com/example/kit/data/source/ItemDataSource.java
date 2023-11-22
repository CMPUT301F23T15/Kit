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

/**
 * An interface for a database of {@link Item}s, providing storage, retrieval, and deletion
 * functionality.
 */
public class ItemDataSource extends AbstractItemDataSource {

    private final CollectionReference itemCollection;
    private final HashMap<String, Item> itemCache;

    /**
     * Constructor that establishes connection to the {@link FirestoreManager} for the Tag Collection.
     * Creates a cache for the state of the database that is updated whenever the database changes.
     */
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
                Item item = buildItem(itemSnapshot);
                itemCache.put(item.findID(), item);
            }
            onDataChanged();
        });
    }

    /**
     * Adds the given {@link Item} to the database if it does not exist, but an Item exists with the
     * same ID as the given Item, that document is updated instead.
     * @param newItem The item to be added or updated in the database.
     */
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

    /**
     * Deletes an {@link Item} from the database given its  ID.
     * @param id The ID of the item to be deleted.
     */
    @Override
    public void deleteDataByID(String id) {
        itemCollection.document(id).delete()
                .addOnSuccessListener(listener -> Log.d("Database", "Item document successfully deleted."))
                .addOnFailureListener(exception -> Log.w("Database", exception));
    }

    /**
     * Access an {@link Item} from the database cache given its ID.
     * @param id The ID of the Item to be retrieved.
     * @return The Item associated with the given ID.
     */
    @Override
    public Item getDataByID(String id) {
        return itemCache.get(id);
    }

    /**
     * Access the entire {@link ItemSet} of {@link Item}s from the cache.
     * @return ItemSet of all known Item objects.
     */
    @Override
    public ItemSet getDataSet() {
        ItemSet itemSet = new ItemSet();
        for (Item item : itemCache.values()) {
            itemSet.addItem(item);
        }
        return itemSet;
    }

    /**
     * Helper method to package an {@link Item} to a Map able to be stored in the Firestore database.
     * Stores {@link Tag}s by their name (ID) in an array.
     * @param item The Item to be packaged.
     * @return A Map that represents the Item.
     */
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

    /**
     * Assembles an {@link Item} from a document snapshot that represents an Item.
     * @param itemSnapshot DocumentSnapshot representing an Item in the database.
     * @return An Item that represents the data from the database.
     */
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
            Tag tag = tagDataSource.getDataByID(tagName);
            if (tag == null) {
                Log.w("Database", "Tag: " + tagName + " not found in the database.");
                continue;
            }

            item.addTag(tag);
        }

        return item;
    }

    /**
     * Helper method that extracts a list of tag names from the itemSnapshot, with type checking
     * to ensure the list is a string list.
     * @implNote Suppresses unchecked cast warning, because it does check the type.
     * @param itemSnapshot DocumentSnapshot representing an item.
     * @return List of tag names if it exists, otherwise an empty list.
     */
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