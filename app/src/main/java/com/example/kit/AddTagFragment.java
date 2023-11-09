package com.example.kit;

import com.example.kit.data.Item;
import com.example.kit.database.ItemFirestoreAdapter;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AddTagFragment extends DialogFragment {
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
        // Inflate a custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.add_tag, null);
        builder.setView(dialogView);

        // Set positive and negative buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle tag input and store in strings
                // Retrieve tag name from the dialog's views
                EditText searchTag = dialogView.findViewById(R.id.TagSearch);
                String tagSearch = searchTag.getText().toString();
                EditText addTag = dialogView.findViewById(R.id.TagName);
                String tagName = addTag.getText().toString();

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
