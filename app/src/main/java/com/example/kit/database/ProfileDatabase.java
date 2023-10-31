package com.example.kit.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.auth.User;

public class ProfileDatabase extends Database{

    private CollectionReference db;
    public ProfileDatabase() {
        super();
        this.db = fetchCollection();
    }

    public void addUser(User user){

    }


    public CollectionReference fetchCollection() {
        return getDB().collection("Profiles");
    }

}
