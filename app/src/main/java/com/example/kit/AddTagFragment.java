package com.example.kit;

import com.example.kit.command.AddTagCommand;
import com.example.kit.command.AddTagToItemCommand;
import com.example.kit.command.CommandManager;
import com.example.kit.command.MacroCommand;
import com.example.kit.data.Tag;
import com.example.kit.data.source.AbstractItemDataSource;
import com.example.kit.data.source.DataSource;
import com.example.kit.data.source.DataSourceManager;
import com.example.kit.databinding.AddTagBinding;
import com.example.kit.views.ColorPalette;
import com.example.kit.views.TagChipGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Small dialog fragment that facilitates adding tags to the database and to the selected items.
 */
public class AddTagFragment extends DialogFragment implements ColorPalette.OnColorSplotchClickListener, TagChipGroup.OnTagChipCloseListener {
    private AddTagBinding binding;
    private final HashSet<String> itemIDs;
    private final DataSource<Tag, ArrayList<Tag>> tagDataSource;
    private ArrayList<String> tagNames;
    private ArrayAdapter<String> adapter;
    private final AbstractItemDataSource itemDataSource;
    private Tag underConstructionTag;

    /**
     * Constructor that takes the itemIDs for the items that should have tags added to them.
     * @param itemIDs A set of ItemIDs to have tags added to.
     */
    public AddTagFragment(HashSet<String> itemIDs) {
        this.itemIDs = itemIDs;
        tagDataSource = DataSourceManager.getInstance().getTagDataSource();
        itemDataSource = DataSourceManager.getInstance().getItemDataSource();
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());

        if (itemIDs.size() == 1) {
            String itemID = itemIDs.iterator().next();
            String itemName = itemDataSource.getDataByID(itemID).getName();
            builder.setTitle("Add Tags to " + itemName);
        } else {
            builder.setTitle("Add Tags to Selected Items");
        }

        builder.setView(binding.getRoot());

        initializeTagField();

        binding.tagsToAddGroup.setInEditMode(true);
        binding.tagsToAddGroup.setChipCloseListener(this);

        // Initialize the color palette view
        binding.colorPalette.setColorSplotchClickListener(this);
        showColorPalette(false);

        // Add the tag to the database and item.
        builder.setPositiveButton("Add Tag(s)", null);

        // Dismiss the dialog when cancel is pressed.
        builder.setNegativeButton("Cancel",  (dialog, which) -> dialog.dismiss());
        Dialog dialog = builder.create();
        dialog.setOnShowListener(dialog1 -> {
            Button confirmButton = ((androidx.appcompat.app.AlertDialog) dialog1).getButton(AlertDialog.BUTTON_POSITIVE);
            confirmButton.setOnClickListener(v -> positiveButtonClick());
        });
        return dialog;
    }

    /**
     * Initialize an autocomplete field that is filled with the existing tags in the database, but
     * also facilitates adding a new tag to both the database and the items.
     */
    private void initializeTagField() {
        // Build the list of existing tag names and bind it to the dropdown
        tagNames = new ArrayList<>();
        ArrayList<Tag> dbTags = tagDataSource.getDataSet();
        for (Tag tag : dbTags) {
            tagNames.add(tag.getName());
        }

        adapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, tagNames);
        binding.tagAutoCompleteField.setAdapter(adapter);

        // Add existing tags if they were clicked in the drop down list
        binding.tagAutoCompleteField.setOnItemClickListener((parent, view, position, id) -> {
            String clickedTagName = adapter.getItem(position);
            adapter.remove(clickedTagName);
            Tag addTag = tagDataSource.getDataByID(clickedTagName);
            binding.tagsToAddGroup.addTag(addTag);
            adapter.notifyDataSetChanged();

            // Clear the field
            binding.tagAutoCompleteField.setText("", true);
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
                adapter.remove(newTagName);
                adapter.notifyDataSetChanged();

                binding.tagsToAddGroup.addTag(newTag);

                // Clear the field
                binding.tagAutoCompleteField.setText("", true);
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
            underConstructionTag = new Tag(newTagText);
            showColorPalette(true);
            return;
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
        dismiss();
    }

    /**
     * Sets the visibility of the {@link ColorPalette}
     * @param show Desired state of the visibility of the ColorPalette
     */
    private void showColorPalette(boolean show) {
        if (show){
            binding.tagColorHeader.setVisibility(View.VISIBLE);
            binding.colorPalette.setVisibility(View.VISIBLE);
        }
        else {
            binding.tagColorHeader.setVisibility(View.GONE);
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

    @Override
    public void onTagChipClosed(Tag tag) {
        tagNames.add(tag.getName());
    }
}
