package com.example.kit.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kit.data.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class ItemDatabase extends Database {
    private CollectionReference db;

    public ItemDatabase() {
        super();
        this.db = fetchCollection();
    }

    public void addItem(Item item) {
        // Todo: Implement tag data here, I don't have the data type on this branch
        HashMap<String, String> data = new HashMap<>();
        data.put("Name", item.getName());
        data.put("Date", item.getAquisitionDate().toString());
        data.put("Value", item.getValue().toString());
        data.put("Make", item.getMake());
        data.put("Model", item.getModel());
        data.put("SerialNumber", item.getSerialNumber());
        data.put("Description", item.getDescription());
        data.put("Comment", item.getComments());
        db.document().set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Added item to collection!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "Adding item failure!");
                    }
                });
    }

    public void deleteItem(String uid){
        db.document(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Removed item from collection!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "Removing item failure!");
                    }
                });
    }
    @Override
    public CollectionReference fetchCollection() {
        return getDB().collection("Items");
    }

    private void setListener(EventListener<QuerySnapshot> listener){
        db.addSnapshotListener(listener);

    }
}
