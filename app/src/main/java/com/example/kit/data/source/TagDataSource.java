package com.example.kit.data.source;

import android.graphics.Color;
import android.util.Log;

import com.example.kit.data.Tag;
import com.example.kit.data.FirestoreManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An interface for a database of {@link Tag}s, providing storage, retrieval, and deletion
 * functionality.
 */
public class TagDataSource extends AbstractTagDataSource {

    private final CollectionReference tagCollection;
    private final HashMap<String, Tag> tagCache;

    /**
     * Constructor that establishes connection to the {@link FirestoreManager} for the Tag Collection.
     * Creates a cache for the state of the database that is updated whenever the database changes.
     */
    public TagDataSource() {
        tagCollection = FirestoreManager.getInstance().getCollection("Tags");
        tagCache = new HashMap<>();
        tagCollection.addSnapshotListener((tagSnapshots, error) -> {
            if (tagSnapshots == null) {
                Log.e("Database", "SnapshotListener null query result");
                return;
            }

            tagCache.clear();
            for (QueryDocumentSnapshot tagSnapshot: tagSnapshots) {
                Tag tag = buildTag(tagSnapshot);
                tagCache.put(tag.getName(), tag);
            }
            onDataChanged();
        });
        cullUnusedTags();
    }

    /**
     * Adds a {@link Tag} to the database.
     * @param tag The Tag to be added.
     */
    @Override
    public void addData(Tag tag) {
        tagCollection.document(tag.getName()).set(packTag(tag));
    }

    /**
     * Deletes a {@link Tag} from the database given its name (ID).
     * @param name Name (ID) of the tag to be deleted.
     */
    @Override
    public void deleteDataByID(String name) {
        tagCollection.document(name).delete()
                .addOnSuccessListener(listener -> Log.d("Database", name + " Tag document successfully deleted."))
                .addOnFailureListener(exception -> Log.w("Database", exception));
    }

    /**
     * Access a {@link Tag} given its name (ID), access from the cache.
     * @param name Name (ID) of the tag to be retrieved.
     * @return The tag object of the provided name.
     */
    @Override
    public Tag getDataByID(String name) {
        return tagCache.get(name);
    }

    /**
     * Access the entire set of {@link Tag}s from the cache.
     * @return List of all known Tag objects.
     */
    @Override
    public ArrayList<Tag> getDataSet() {
        return new ArrayList<>(tagCache.values());
    }

    /**
     * Helper method to package a {@link Tag} in a form able to be stored in Firestore.
     * @param tag Tag to be packaged.
     * @return A map of the RGBA values of the color from the provided tag.
     */
    private HashMap<String, Object> packTag(Tag tag) {
        HashMap<String, Object> packedTag = new HashMap<>();
        packedTag.put("red", tag.getColor().red());
        packedTag.put("green", tag.getColor().green());
        packedTag.put("blue", tag.getColor().blue());
        packedTag.put("alpha", tag.getColor().alpha());

        return packedTag;
    }

    /**
     * Assembles a {@link Tag} from a given document snapshot that represents a tag.
     * If the color is invalid, sets the color of the tag to white and logs the error.
     * @param tagSnapshot The DocumentSnapshot for the tag.
     * @return The assembled tag with the color and name stored in the snapshot.
     */
    private Tag buildTag(DocumentSnapshot tagSnapshot) {
        Tag tag = new Tag(tagSnapshot.getId());

        try {
            Double redDouble = tagSnapshot.getDouble("red");
            float red = redDouble != null ? redDouble.floatValue() : 0.0f;

            Double greenDouble = tagSnapshot.getDouble("green");
            float green = greenDouble != null ? greenDouble.floatValue() : 0.0f;

            Double blueDouble = tagSnapshot.getDouble("blue");
            float blue = blueDouble != null ? blueDouble.floatValue() : 0.0f;

            Double alphaDouble = tagSnapshot.getDouble("alpha");
            float alpha = alphaDouble != null ? alphaDouble.floatValue() : 0.0f;

            tag.setColor(Color.valueOf(red, green, blue, alpha));

        } catch (NullPointerException nullPointerException) {
            Log.e("Database", "Unable to reassemble tag color with name: " + tagSnapshot.getId());
            nullPointerException.printStackTrace();
            tag.setColor(Color.valueOf(0.0f, 0.0f, 0.0f, 1.0f));
        }

        return tag;
    }
}
