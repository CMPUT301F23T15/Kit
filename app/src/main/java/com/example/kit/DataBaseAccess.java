package com.example.kit;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DataBaseAccess {
    private static FirebaseFirestore db;
    static CollectionReference fetchCollection(String collectionName){
        return db.collection(collectionName);
    };


}
