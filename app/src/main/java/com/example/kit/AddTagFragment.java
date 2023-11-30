package com.example.kit;

import com.example.kit.command.AddTagCommand;
import com.example.kit.command.AddTagToItemCommand;
import com.example.kit.command.CommandManager;
import com.example.kit.command.MacroCommand;
import com.example.kit.data.Tag;
import com.example.kit.data.source.DataSource;
import com.example.kit.data.source.DataSourceManager;
import com.example.kit.databinding.AddTagBinding;
import com.example.kit.views.ColorPalette;
import com.example.kit.views.TagChipGroup;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Small dialog fragment that facilitates adding tags to the database and to the selected items.
 */
public class AddTagFragment extends DialogFragment implements ColorPalette.OnColorSplotchClickListener {
    private AddTagBinding binding;
    private final HashSet<String> itemIDs;
    private final DataSource<Tag, ArrayList<Tag>> tagDataSource;
    private Tag underConstructionTag;

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

        // Initialize the color palette view
        binding.colorPalette.setColorSplotchClickListener(this);
        showColorPalette(false);

        // Add the tag to the database and item.
        builder.setPositiveButton("Add Tag(s)", (dialog, which) -> positiveButtonClick());

        // Dismiss the dialog when cancel is pressed.
        builder.setNegativeButton("Cancel",  (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    /**
     * Initialize an autocomplete field that is filled with the existing tags in the database, but
     * also facilitates adding a new tag to both the database and the items.
     */
    private void initializeTagField() {
        // Build the list of existing tag names and bind it to the dropdown
        ArrayList<String> tagNames = new ArrayList<>();
        ArrayList<Tag> dbTags = tagDataSource.getDataSet();
        for (Tag tag : dbTags) {
            tagNames.add(tag.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, tagNames);
        binding.tagAutoCompleteField.setAdapter(adapter);

        // Add existing tags if they were clicked in the drop down list
        binding.tagAutoCompleteField.setOnItemClickListener((parent, view, position, id) -> {
            Tag addTag = tagDataSource.getDataByID(tagNames.remove(position));
            binding.tagsToAddGroup.addTag(addTag);
            adapter.notifyDataSetChanged();
            // Clear the field
            binding.tagAutoCompleteField.setText("", false);
        });

        // Listener for enter key pressed to add a tag
        binding.tagAutoCompleteField.setOnEditorActionListener((v, actionId, event) -> {
            // User hasn't finished typing or moved on yet, don't continue.
            if ((actionId != EditorInfo.IME_ACTION_NEXT) && (actionId != EditorInfo.IME_ACTION_DONE)) {
                return false;
            }
            // Field is empty, do nothing.
            String newTagName = binding.tagAutoCompleteField.getText().toString();
            if (newTagName.isEmpty()) {
                return false;
            }

            // Fetch the tag by name if it exists
            Tag newTag = tagDataSource.getDataByID(newTagName);

            // Tag doesn't exist, create open color palette and create new tag
            if (newTag == null) {
                underConstructionTag = new Tag(newTagName);
                showColorPalette(true);

            // Tag already exists, add it to the tag group and remove it from the drop down
            } else {
                tagNames.remove(newTag.getName());
                adapter.notifyDataSetChanged();

                binding.tagsToAddGroup.addTag(newTag);

                // Clear the field
                binding.tagAutoCompleteField.setText("", false);
            }

            return true;
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

    /**
     * Sets the visibility of the {@link ColorPalette}
     * @param show Desired state of the visibility of the ColorPalette
     */
    private void showColorPalette(boolean show) {
        if (show){
            binding.colorPalette.setVisibility(View.VISIBLE);
        }
        else {
            binding.colorPalette.setVisibility(View.GONE);
        }

    }

    /**
     * Callback for the {@link ColorPalette.OnColorSplotchClickListener}
     * Adds the provided ColorInt to the tag, then clears the Tag Field and adds the Tag to the
     * {@link TagChipGroup}. Hides the {@link ColorPalette}.
     */
    @Override
    public void onColorSplotchClick(int colorInt) {
        underConstructionTag.setColor(Color.valueOf(colorInt));
        binding.tagAutoCompleteField.setText("");
        binding.tagsToAddGroup.addTag(underConstructionTag);
        underConstructionTag = null;
        showColorPalette(false);
    }
}
