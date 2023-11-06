package com.example.kit.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreManager {

    private static final FirestoreManager firestoreManager = new FirestoreManager();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirestoreManager() {
    }

    public static FirestoreManager getInstance() {
        return firestoreManager;
    }

    public CollectionReference getCollection(String collection) {
        return db.collection(collection);
    }
}
