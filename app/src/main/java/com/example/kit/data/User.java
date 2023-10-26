package com.example.kit.data;

import android.provider.ContactsContract;

public class User {
    private String username;
    private ContactsContract.CommonDataKinds.Email email;

    // TODO: Add authentication using Firestore

    public User(String username, ContactsContract.CommonDataKinds.Email email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ContactsContract.CommonDataKinds.Email getEmail() {
        return email;
    }

    public void setEmail(ContactsContract.CommonDataKinds.Email email) {
        this.email = email;
    }
}
