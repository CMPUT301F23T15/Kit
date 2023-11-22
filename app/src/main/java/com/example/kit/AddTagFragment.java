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

        initializeNewTagField();
        initializeSpinner();

        // Add the tag to the database and item.
        builder.setPositiveButton("Add Tag", (dialog, which) -> positiveButtonClick());

        // Dismiss the dialog when cancel is pressed.
        builder.setNegativeButton("Cancel",  (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    /**
     * Initialize the New Tag field with a listener for enter clicks on the field to add the tag
     * to the {@link TagChipGroup}.
     */
    private void initializeNewTagField(){
        binding.newTagName.setOnEditorActionListener((view, actionId, event) -> {
            // If enter/done is pressed on the keyboard, TODO: Expand if not comprehensive
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.tagsToAddGroup.addTag(new Tag(view.getText().toString()));
                view.setText("");
                return true;
            }
            return false;
        });
    }

    /**
     * Initialize a spinner with the names of all Tags currently in the tag data source.
     * After a tag is selected from the spinner it is added to the {@link TagChipGroup} then removed
     * from the list.
     */
    private void initializeSpinner() {
        // Create a copy of the existing tags so we can remove tags from the list as they get selected.
        ArrayList<Tag> existingTags = new ArrayList<>(tagDataSource.getDataSet());

        // Add a dummy tag to the list that represents a hint for selection to prevent adding
        // tags at undesired times
        existingTags.add(0, new Tag("Select a Tag"));

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

    /**
     * Callback method for when the positive button is clicked on the dialog, extracts data from
     * the view fields and checks it before adding to the data source.
     */
    private void positiveButtonClick() {
        ArrayList<Tag> tags = binding.tagsToAddGroup.getTags();

        // Add whatever is typed in the tag field too
        String newTagText = binding.newTagName.getText().toString();
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

    /**
     * Custom Adapter for displaying Tags as options in a Spinner
     */
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
