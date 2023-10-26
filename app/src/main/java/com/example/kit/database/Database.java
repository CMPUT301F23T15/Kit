package com.example.kit.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class Database {
    private static FirebaseFirestore db;
    static CollectionReference fetchCollection(String collectionName){
        return db.collection(collectionName);
    };


}
