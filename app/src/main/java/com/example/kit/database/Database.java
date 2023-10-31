package com.example.kit.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class Database {
    // Todo: Discuss in meeting, implement an add function here, and have parameteres be a bundle and collection
    private static FirebaseFirestore db;
    public Database() {
        db= FirebaseFirestore.getInstance();
    }
    public static FirebaseFirestore getDB() {
        return db;
    }
    abstract public CollectionReference fetchCollection();
}