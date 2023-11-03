package com.example.kit.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreManager {

    private static final FirestoreManager firestoreManager = new FirestoreManager();

    private FirebaseFirestore db;

    private FirestoreManager() {
        db = FirebaseFirestore.getInstance();
    }

    public static FirestoreManager getInstance() {
        return firestoreManager;
    }

    public CollectionReference getItemCollection() {
        return db.collection("Items");
    }
}
