package com.example.kit;

import com.example.kit.command.AddTagCommand;
import com.example.kit.command.AddTagToItemCommand;
import com.example.kit.command.CommandManager;
import com.example.kit.command.MacroCommand;
import com.example.kit.data.Tag;
import com.example.kit.data.source.DataSource;
import com.example.kit.data.source.DataSourceManager;
import com.example.kit.databinding.AddTagBinding;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Small dialog fragment that facilitates adding tags to the database and to the selected items.
 */
public class AddTagFragment extends DialogFragment {
    private AddTagBinding binding;
    private final HashSet<String> itemIDs;
    private final DataSource<Tag, ArrayList<Tag>> tagDataSource;

    /**
     * Constructor that takes the itemIDs for the items that should have tags added to them.
     * @param itemIDs A set of ItemIDs to have tags added to.
     */
    public AddTagFragment(HashSet<String> itemIDs) {
        this.itemIDs = itemIDs;
        tagDataSource = DataSourceManager.getInstance().getTagDataSource();
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
        // Inflate binding and set view of dialog to the root of binding
        binding = AddTagBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(binding.getRoot());

        initializeTagField();

        // Add the tag to the database and item.
        builder.setPositiveButton("Add Tag", (dialog, which) -> positiveButtonClick());

        // Dismiss the dialog when cancel is pressed.
        builder.setNegativeButton("Cancel",  (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    /**
     * Initialize an autocomplete field that is filled with the existing tags in the database, but
     * also facilitates adding a new tag to both the database and the items.
     */
    private void initializeTagField() {
        ArrayList<String> tagNames = new ArrayList<>();
        ArrayList<Tag> dbTags = tagDataSource.getDataSet();
        for (Tag tag : dbTags) {
            tagNames.add(tag.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, tagNames);
        binding.tagAutoCompleteField.setAdapter(adapter);

        binding.tagAutoCompleteField.setOnItemClickListener((parent, view, position, id) -> {
            Tag addTag = tagDataSource.getDataByID(tagNames.remove(position));
            binding.tagsToAddGroup.addTag(addTag);
            adapter.notifyDataSetChanged();
            // Clear the field
            binding.tagAutoCompleteField.setText("", false);
        });

        // Listener for enter key pressed to add a tag that doesn't exist
        binding.tagAutoCompleteField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                String newTagName = binding.tagAutoCompleteField.getText().toString();
                if (newTagName.isEmpty()) {
                    return false;
                }

                // Check if the tag already exists, if not create a new Tag
                Tag newTag = tagDataSource.getDataByID(newTagName);
                if (newTag == null) {
                    newTag = new Tag(newTagName);
                    CommandManager.getInstance().executeCommand(new AddTagCommand(newTag));
                } else {
                    // Remove the existing tag from the options in the dropdown
                    tagNames.remove(newTag.getName());
                    adapter.notifyDataSetChanged();
                }

                binding.tagsToAddGroup.addTag(newTag);

                // Clear the field
                binding.tagAutoCompleteField.setText("", false);
                return true;
            }
            return false;
        });
    }

    /**
     * Callback method for when the positive button is clicked on the dialog, extracts data from
     * the view fields and checks it before adding to the data source.
     */
    private void positiveButtonClick() {
        ArrayList<Tag> tags = binding.tagsToAddGroup.getTags();

        // Add whatever is typed in the tag field too
        String newTagText = binding.tagAutoCompleteField.getText().toString();
        if (!newTagText.isEmpty()) {
            tags.add(new Tag(newTagText));
        }

        MacroCommand addTagsMacro = new MacroCommand();
        MacroCommand addTagsToItemsMacro = new MacroCommand();

        for (Tag tag : tags) {
            // Add new tags to the database
            if (tagDataSource.getDataByID(tag.getName()) == null) {
                AddTagCommand addTagCommand = new AddTagCommand(tag);
                addTagsMacro.addCommand(addTagCommand);
            }

            // Add tag to all items
            for (String itemID : itemIDs) {
                AddTagToItemCommand addTagToItemCommand = new AddTagToItemCommand(tag, itemID);
                addTagsToItemsMacro.addCommand(addTagToItemCommand);
            }
        }

        CommandManager.getInstance().executeCommand(addTagsMacro);
        CommandManager.getInstance().executeCommand(addTagsToItemsMacro);
    }
}
