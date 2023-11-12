package com.example.kit.data.source;

import com.example.kit.data.Tag;
import com.example.kit.database.FirestoreManager;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

public class TagDataSource extends DataSource<Tag, ArrayList<Tag>> {

    private final CollectionReference tagCollection;

    public TagDataSource() {
        tagCollection = FirestoreManager.getInstance().getCollection("Tags");
    }

    @Override
    public void addData(Tag newData) {

    }

    @Override
    public void deleteDataByID(String id) {

    }

    @Override
    public Tag getDataByID(String id) {
        return null;
    }

    @Override
    public ArrayList<Tag> getDataCollection() {
        return null;
    }
}
