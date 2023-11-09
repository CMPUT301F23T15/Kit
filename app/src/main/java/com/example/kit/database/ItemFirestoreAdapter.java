package com.example.kit.database;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.kit.SelectListener;
import com.example.kit.data.Item;
import com.example.kit.databinding.ItemListRowBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;

public class ItemFirestoreAdapter extends FirestoreRecyclerAdapter<Item, ItemViewHolder> {
    private SelectListener listener;
    private FirestoreManager firestoreManager;

    public ItemFirestoreAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {

        super(options);
        firestoreManager = FirestoreManager.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Item model) {
        holder.displayItem(model);
        holder.setupListeners(listener, holder, model);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListRowBinding binding = ItemListRowBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ItemViewHolder(binding);
    }
    public void setListener(SelectListener listener) {
        this.listener = listener;
    }

    public void addTagToItem(String itemName, String newTag) {
        DocumentReference itemRef = firestoreManager.getCollection("items").document(itemName);

        // Update the Firestore document with the new tag
        itemRef.update("tags", FieldValue.arrayUnion(newTag))
                .addOnSuccessListener(aVoid -> {
                    Log.v("Tag Adding", "Tag added!!");
                })
                .addOnFailureListener(e -> {
                });
    }

}
