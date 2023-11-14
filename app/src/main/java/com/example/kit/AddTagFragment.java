package com.example.kit;

import com.example.kit.command.AddTagCommand;
import com.example.kit.command.AddTagToItemCommand;
import com.example.kit.command.CommandManager;
import com.example.kit.data.Item;
import com.example.kit.data.Tag;
import com.example.kit.databinding.AddTagBinding;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashSet;

public class AddTagFragment extends DialogFragment {
    private AddTagBinding binding;
    private OnTagAddedListener onTagAddedListener;
    private Item item;
    private HashSet<String> itemIDs;

    public interface OnTagAddedListener {
        void onTagAdded(String tagID, String itemID);
    }

    public void setOnTagAddedListener(OnTagAddedListener listener) {
        this.onTagAddedListener = listener;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void addItemIDs(HashSet<String> ids) {
        this.itemIDs = ids;
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

                // Add the new tag to the database
                if (!tagName.isEmpty()) {
                    AddTagCommand tagCommand = new AddTagCommand(new Tag(tagName));
                    CommandManager.getInstance().executeCommand(tagCommand);
                }

                // TODO: finish this, maybe get the tag's ID by pulling from the tag DB again? then
                // TODO: use the id for the command below?
//                AddTagToItemCommand command;

                if (!tagName.isEmpty() && onTagAddedListener != null)  {

                    onTagAddedListener.onTagAdded(item, tagName);
                    Log.v("Tag fragment", "Tag reached fragment");
                }
                else {
                    Log.e("Tag Adding", "Error: tagName is empty or onTagAddedListener is null");
                }
            }
        });

        // Dismiss the dialog when cancel is pressed.
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }
}
