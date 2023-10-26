package com.example.kit.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class Database {
    private static FirebaseFirestore db;

    public Database() {
        db= FirebaseFirestore.getInstance();
    }

    public static FirebaseFirestore getDB() {
        return db;
    }
    public CollectionReference fetchCollection(String collectionName){
        return db.collection(collectionName);
    };


}
