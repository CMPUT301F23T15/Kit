package com.example.kit.data.source;

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
                Tag tag = new Tag(tagSnapshot.getId(), new HashMap<>(tagSnapshot.getData()));
                tagCache.put(tag.getName(), tag);
            }
            onDataChanged();
        });
    }

    @Override
    public void addData(Tag tag) {
        tagCollection.document(tag.getName()).set(tag.pack());
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
}
