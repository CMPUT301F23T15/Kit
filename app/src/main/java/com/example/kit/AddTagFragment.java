package com.example.kit;

import com.example.kit.command.AddTagCommand;
import com.example.kit.command.AddTagToItemCommand;
import com.example.kit.command.CommandManager;
import com.example.kit.command.MacroCommand;
import com.example.kit.data.Tag;
import com.example.kit.databinding.AddTagBinding;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.HashSet;

/**
 * Small dialog fragment that facilitates adding tags to the database and to the selected items.
 * INCOMPLETE.
 */
public class AddTagFragment extends DialogFragment {
    private AddTagBinding binding;
    private HashSet<String> itemIDs;

    /**
     * Constructor that takes the itemIDs for the items that should have tags added to them.
     * @param itemIDs A set of ItemIDs to have tags added to.
     */
    public AddTagFragment(HashSet<String> itemIDs) {
        this.itemIDs = itemIDs;
    }

    /**
     * Creation method for the dialog fragment, inflates the view and binds the button callbacks.
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return The created dialog fragment.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate binding and set view of dialog to the root of binding
        binding = AddTagBinding.inflate(getLayoutInflater());
        builder.setView(binding.getRoot());

        // Add the tag to the database and item.
        builder.setPositiveButton("Add Tag", (dialog, which) -> positiveButtonClick());

        // Dismiss the dialog when cancel is pressed.
        builder.setNegativeButton("Cancel",  (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    /**
     * Callback method for when the positive button is clicked on the dialog, extracts data from
     * the view fields and checks it before adding to the data source.
     */
    private void positiveButtonClick() {
        // Handle tag input and store in strings
        // Retrieve tag name from the dialog's views
        String tagSearch = binding.TagSearch.getText().toString();
        String tagName = binding.TagName.getText().toString();

        // Add the new tag to the database
        if (!tagName.isEmpty()) {
            addTag(tagName);
        }
    }

    /**
     * Adds the new tag to the data source, and adds it to the items given in the Item IDs.
     * @param name Name of the tag to be added.
     */
    private void addTag(String name) {
        // Build Tag, red default color until color picker is implemented.
        Tag tag = new Tag(name, Color.valueOf(Color.RED));

        // Add the tag to the database
        AddTagCommand tagCommand = new AddTagCommand(tag);
        CommandManager.getInstance().executeCommand(tagCommand);

        // Attach the tag to the items associated with the selected Item IDs
        MacroCommand addTagToItemMacro = new MacroCommand();
        for (String itemID : itemIDs) {
            addTagToItemMacro.addCommand(new AddTagToItemCommand(tag, itemID));
        }

        CommandManager.getInstance().executeCommand(addTagToItemMacro);
    }
}
