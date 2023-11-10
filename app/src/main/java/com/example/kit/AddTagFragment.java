package com.example.kit;

import com.example.kit.data.Item;
import com.example.kit.database.ItemFirestoreAdapter;
import com.example.kit.databinding.AddTagBinding;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AddTagFragment extends DialogFragment {
    private AddTagBinding binding;
    private OnTagAddedListener onTagAddedListener;
    private Item item;

    public interface OnTagAddedListener {
        void onTagAdded(Item item, String tagName);
    }

    public void setOnTagAddedListener(OnTagAddedListener listener) {
        this.onTagAddedListener = listener;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate binding and set view of dialog to the root of binding
        binding = AddTagBinding.inflate(getLayoutInflater());
        builder.setView(binding.getRoot());

        // Set positive and negative buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle tag input and store in strings
                // Retrieve tag name from the dialog's views
                String tagSearch = binding.TagSearch.getText().toString();
                String tagName = binding.TagName.getText().toString();

                if (!tagName.isEmpty() && onTagAddedListener != null)  {
                    onTagAddedListener.onTagAdded(item, tagName);
                    Log.v("Tag fragment", "Tag reached fragment");
                }
                else {
                    Log.e("Tag Adding", "Error: tagName is empty or onTagAddedListener is null");
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
