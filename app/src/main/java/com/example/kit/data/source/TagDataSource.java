package com.example.kit.data.source;

import android.graphics.Color;
import android.util.Log;

import com.example.kit.data.Item;
import com.example.kit.data.Tag;
import com.example.kit.data.FirestoreManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class TagDataSource extends DataSource<Tag, ArrayList<Tag>> {

    private final CollectionReference tagCollection;
    private final HashMap<String, Tag> tagCache;

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
    }

    @Override
    public void addData(Tag tag) {
        tagCollection.document(tag.getName()).set(packTag(tag));
    }

    @Override
    public void deleteDataByID(String id) {
        tagCollection.document(id).delete();
    }

    @Override
    public Tag getDataByID(String id) {
        return tagCache.get(id);
    }

    @Override
    public ArrayList<Tag> getDataSet() {
        return new ArrayList<>(tagCache.values());
    }

    private HashMap<String, Object> packTag(Tag tag) {
        HashMap<String, Object> packedTag = new HashMap<>();
        packedTag.put("red", tag.getColor().red());
        packedTag.put("green", tag.getColor().green());
        packedTag.put("blue", tag.getColor().blue());
        packedTag.put("alpha", tag.getColor().alpha());

        return packedTag;
    }

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
            nullPointerException.printStackTrace();
            tag.setColor(Color.valueOf(0.0f, 0.0f, 0.0f, 1.0f));
        }

        return tag;
    }

}
