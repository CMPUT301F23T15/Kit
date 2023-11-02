package com.example.kit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.data.Tag;
import com.example.kit.database.ItemDatabase;
import com.example.kit.databinding.ItemListBinding;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigDecimal;
import java.util.Date;


public class ItemListFragment extends Fragment {

    private ItemListBinding itemListBinding;

    private ItemAdapter itemAdapter;
    private ItemSet itemSet;
    private ItemDatabase itemDatabase = new ItemDatabase();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventListener eventListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    itemSet.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        Item item = new Item(
                                doc.getString("Name"),
                                doc.getString("Date"),
                                doc.getString("Value"),
                                doc.getString("Make"),
                                doc.getString("Model"),
                                doc.getString("SerialNumber"),
                                doc.getString("Description"),
                                doc.getString("Comment"),
                        );

                    }
                        // Create item to add
                        Log.d("Firestore", String.format("Item fetched"));
                    }
                    itemAdapter.notifyDataSetChanged();
            }
        };
        itemDatabase.setListener(eventListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemListBinding = ItemListBinding.inflate(inflater, container, false);
        View view = itemListBinding.getRoot();
        return view;
    }

    public void initAddItemButton() {
//        itemListBinding.addItemButton.setOnClickListener(onClick -> {

//        });
    }
}
