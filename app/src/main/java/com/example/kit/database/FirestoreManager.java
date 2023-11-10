package com.example.kit.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Manages an instance of the Firestore Database, providing access to the various collections.
 */
public class FirestoreManager {

    private static final FirestoreManager firestoreManager = new FirestoreManager();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirestoreManager() {
    }

    /**
     * Provides the instance of the FirestoreManager Singleton
     * @return
     *  Instance of FirestoreManager
     */
    public static FirestoreManager getInstance() {
        return firestoreManager;
    }

    /**
     * Provides references to the requested collection from the Database
     * @param collection
     *  Name of the collection to be retrieved
     * @return
     *  CollectionReference with the provided name.
     */
    public CollectionReference getCollection(String collection) {
        return db.collection(collection);
    }
}
