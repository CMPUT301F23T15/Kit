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
import android.graphics.Color;
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
 * INCOMPLETE.
 */
public class AddTagFragment extends DialogFragment {
    private AddTagBinding binding;
    private final HashSet<String> itemIDs;
    private final ArrayList<Tag> existingTags;
    private final DataSource<Tag, ArrayList<Tag>> tagDataSource;

    /**
     * Constructor that takes the itemIDs for the items that should have tags added to them.
     * @param itemIDs A set of ItemIDs to have tags added to.
     */
    public AddTagFragment(HashSet<String> itemIDs) {
        this.itemIDs = itemIDs;
        tagDataSource = DataSourceManager.getInstance().getTagDataSource();
        // Create a copy so we can remove tags from the list as they get selected.
        existingTags = new ArrayList<>(tagDataSource.getDataSet());
        // Add a dummy tag to the list that represents a hint for selection to prevent adding
        // tags at undesired times
        existingTags.add(0, new Tag("Select a Tag"));
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

        initializeNewTagField();
        initializeSpinner();

        // Add the tag to the database and item.
        builder.setPositiveButton("Add Tag", (dialog, which) -> positiveButtonClick());

        // Dismiss the dialog when cancel is pressed.
        builder.setNegativeButton("Cancel",  (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void initializeNewTagField(){
        binding.TagName.setSingleLine(true);
        binding.TagName.setOnEditorActionListener((view, actionId, event) -> {
            // If enter/done is pressed on the keyboard, TODO: Expand if not comprehensive
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addChipToGroup(view.getText().toString());
                view.setText("");
                return true;
            }
            return false;
        });
    }

    private void initializeSpinner() {
        TagAdapter adapter = new TagAdapter(getContext(), existingTags);
        binding.tagSpinner.setAdapter(adapter);

        // When an item from the spinner is selected, remove it from the spinner and add it to
        // the chip group, then reset the spinner to the Hint option (index 0)
        binding.tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Tag selectedTag = existingTags.remove(position);
                    binding.tagsToAddGroup.addTag(selectedTag);
                    adapter.notifyDataSetChanged();

                    parent.setSelection(0);
                }
            }

            // Not needed
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void addChipToGroup(String toString) {
        binding.tagsToAddGroup.addTag(new Tag(toString));
    }

    /**
     * Callback method for when the positive button is clicked on the dialog, extracts data from
     * the view fields and checks it before adding to the data source.
     */
    private void positiveButtonClick() {
        ArrayList<Tag> tags = binding.tagsToAddGroup.getTags();
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

    private static class TagAdapter extends ArrayAdapter<Tag> {
        public TagAdapter(Context context, ArrayList<Tag> data) {
            super(context, androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, data);
            setDropDownViewResource(androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView textView = (TextView) super.getView(position, convertView, parent);
            String tagName;
            try {
                tagName = getItem(position).getName();
            } catch (NullPointerException e) {
                tagName = "null";
                Log.e("Tag Spinner", "Tag at position " + position + "didn't exist?");
            }
            textView.setText(tagName);
            return textView;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
            String tagName;
            try {
                tagName = getItem(position).getName();
            } catch (NullPointerException e) {
                tagName = "null";
                Log.e("Tag Spinner", "Tag at position " + position + "didn't exist?");
            }
            textView.setText(tagName);
            return textView;
        }
    }
}
