package com.example.kit;

import com.example.kit.command.AddTagCommand;
import com.example.kit.command.AddTagToItemCommand;
import com.example.kit.command.Command;
import com.example.kit.command.CommandManager;
import com.example.kit.command.MacroCommand;
import com.example.kit.data.Item;
import com.example.kit.data.Tag;
import com.example.kit.data.source.DataSourceManager;
import com.example.kit.databinding.AddTagBinding;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;

public class AddTagFragment extends DialogFragment {
    private AddTagBinding binding;
    private HashSet<String> itemIDs;

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
        builder.setPositiveButton("Add Tag", (dialog, which) -> {
            // Handle tag input and store in strings
            // Retrieve tag name from the dialog's views
            String tagSearch = binding.TagSearch.getText().toString();
            String tagName = binding.TagName.getText().toString();

            // Add the new tag to the database
            if (!tagName.isEmpty()) addTag(tagName);
        });

        // Dismiss the dialog when cancel is pressed.
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void addTag(String name) {
        Tag tag = new Tag(name, Color.valueOf(Color.RED));
        AddTagCommand tagCommand = new AddTagCommand(tag);
        CommandManager.getInstance().executeCommand(tagCommand);

        MacroCommand addTagToItemMacro = new MacroCommand();
        for (String itemID : itemIDs) {
            addTagToItemMacro.addCommand(new AddTagToItemCommand(tag, itemID));
        }

        CommandManager.getInstance().executeCommand(addTagToItemMacro);
    }
}
