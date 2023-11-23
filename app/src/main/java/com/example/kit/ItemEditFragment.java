package com.example.kit;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.command.AddItemCommand;
import com.example.kit.command.AddTagCommand;
import com.example.kit.command.CommandManager;
import com.example.kit.data.Item;
import com.example.kit.data.Tag;
import com.example.kit.data.source.DataSource;
import com.example.kit.data.source.DataSourceManager;
import com.example.kit.databinding.ItemEditBinding;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.carousel.HeroCarouselStrategy;
import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ItemDisplayFragment is a Fragment subclass used to display details of an {@link Item} object.
 * It supports creating a new item or editing an existing one, integrating with Firestore for data persistence.
 */
public class ItemEditFragment extends Fragment {

    private String itemID;
    private ItemEditBinding binding;
    private NavController navController;
    private DataSource<Tag, ArrayList<Tag>> tagDataSource;

    // Image Carousel Fields
    private CarouselImageAdapter imageAdapter;

    // Tag Dropdown Fields
    private ArrayAdapter<String> tagNameAdapter;
    private ArrayList<String> tagNames;

    /**
     * Standard fragment lifecycle, stores a reference to the NavController.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        tagDataSource = DataSourceManager.getInstance().getTagDataSource();
    }

    /**
     * Standard Fragment lifecycle function, inflates the binding and initializes buttons.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The root view of the binding.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ItemEditBinding.inflate(inflater, container, false);
        initializeConfirmButton();
        initializeImageCarousel();
        initializeItemValueField();
        initializeTagField();
        return binding.getRoot();
    }

    /**
     * Loads the inputted item on start
     */
    @Override
    public void onStart() {
        super.onStart();
        loadItem();
    }

    /**
     * Initializes the floating action button to handle the creation or update of an item in the Firestore database.
     */
    private void initializeConfirmButton() {
        binding.floatingActionButton.setOnClickListener(onClick -> {
            // Add item to the database and navigate back to the list
            CommandManager.getInstance().executeCommand(new AddItemCommand(buildItem()));
            navController.navigate(ItemEditFragmentDirections.itemCreatedAction());
        });
    }

    private void initializeImageCarousel() {
        CarouselLayoutManager layoutManager
                = new CarouselLayoutManager(new HeroCarouselStrategy(), RecyclerView.VERTICAL);

        binding.imageCarousel.setLayoutManager(layoutManager);
        binding.imageCarousel.setNestedScrollingEnabled(false);
        CarouselSnapHelper snapHelper = new CarouselSnapHelper();
        snapHelper.attachToRecyclerView(binding.imageCarousel);
        imageAdapter = new CarouselImageAdapter();
    }

    private void initializeItemValueField() {
        binding.itemValueDisplay.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            // Taken and adapted from User Val Okafor from https://stackoverflow.com/a/27028225
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    binding.itemValueDisplay.removeTextChangedListener(this);

                    String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }
                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    formatter.setMaximumFractionDigits(0);
                    String formatted = formatter.format(parsed).substring(1);

                    current = formatted;
                    binding.itemValueDisplay.setText(formatted);
                    binding.itemValueDisplay.setSelection(formatted.length());
                    binding.itemValueDisplay.addTextChangedListener(this);
            }
        }});
    }

    private void initializeTagField() {
        tagNames = new ArrayList<>();
        ArrayList<Tag> dbTags = tagDataSource.getDataSet();
        for (Tag tag : dbTags) {
            tagNames.add(tag.getName());
        }
        tagNameAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, tagNames);
        binding.tagAutoCompleteField.setAdapter(tagNameAdapter);

        binding.tagAutoCompleteField.setOnItemClickListener((parent, view, position, id) -> {
            Tag addTag = tagDataSource.getDataByID(tagNames.remove(position));
            binding.itemDisplayTagGroup.addTag(addTag);
            tagNameAdapter.notifyDataSetChanged();
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
                    tagNameAdapter.notifyDataSetChanged();
                }

                binding.itemDisplayTagGroup.addTag(newTag);

                // Clear the field
                binding.tagAutoCompleteField.setText("", false);
                return true;
            }
            return false;
        });
    }


    private void updateCarousel() {
        List<CarouselImage> images = new ArrayList<>();
        images.add(new CarouselImage(R.drawable.baseline_add_24));
        images.add(new CarouselImage(R.drawable.baseline_attach_money_24));
        imageAdapter.submitList(images);
    }

    /**
     * Loads an item's details into the UI components if editing an existing item. Retrieves the
     * item from the fragment's arguments.
     */
    private void loadItem() {
        // Return to previous screen if we did not arrive with any arguments
        if (getArguments() == null) {
            Log.e("Navigation", "Null Arguments in the edit item fragment");
            navController.popBackStack();
            return;
        }

        // Retrieve the item from the bundle
        String id = getArguments().getString("id");
        this.itemID = id;
        Item item = DataSourceManager.getInstance().getItemDataSource().getDataByID(id);

        // If the item was null, return to the previous screen
        if (item == null) {
            Log.e("Item Display Error", "No item found for the bundled ID");
            navController.popBackStack();
            return;
        }

        this.itemID = item.findID();

        updateCarousel();

        // Use View Binding to populate UI elements with item data
        binding.itemNameDisplay.setText(item.getName());

        // Value should get formatted appropriately by the text changed listener
        binding.itemValueDisplay.setText(item.getValue());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
        String formattedDate = dateFormat.format(item.getAcquisitionDate().toDate());
        binding.itemDateDisplay.setText(formattedDate);

        binding.itemDescriptionDisplay.setText(item.getDescription());
        binding.itemCommentDisplay.setText(item.getComment());
        binding.itemMakeDisplay.setText(item.getMake());
        binding.itemModelDisplay.setText(item.getModel());
        binding.itemSerialNumberDisplay.setText(item.getSerialNumber());

        binding.itemDisplayTagGroup.removeAllViews();
        for (Tag tag : item.getTags()) {
            binding.itemDisplayTagGroup.addTag(tag);
            // Remove tags that are already on the item from the list of available existing tags
            tagNames.remove(tag.getName());
        }
    }

    /**
     * Constructs an {@link Item} object from the data input in the UI fields.
     * This method includes date parsing and exception handling for invalid date formats.
     *
     * @return {@link Item} The newly constructed or updated item.
     * @throws RuntimeException If there is a problem parsing the acquisition date.
     */
    private Item buildItem() {
        Item newItem = new Item();

        // Name
        if (binding.itemNameDisplay.getText() != null) {
            newItem.setName(binding.itemNameDisplay.getText().toString());
        } else {
            newItem.setName("");
        }

        // Value
        if (binding.itemValueDisplay.getText() != null) {
            String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
            String cleanString = binding.itemValueDisplay.getText().toString().replaceAll(replaceable, "");
            newItem.setValue(cleanString);
        } else {
            newItem.setValue("0");
        }

        // Absolutely terrible garbage, TODO: Improve data input handling. Currently only takes XX/XX/XXXX dates
        try {
            Date date = DateFormat.getDateInstance(DateFormat.SHORT).parse(binding.itemDateDisplay.getText().toString());
            newItem.setAcquisitionDate(new Timestamp(date));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Description
        if (binding.itemDescriptionDisplay.getText() != null) {
            newItem.setDescription(binding.itemDescriptionDisplay.getText().toString());
        } else {
            newItem.setDescription("");
        }

        // Comment
        if (binding.itemCommentDisplay.getText() != null) {
            newItem.setComment(binding.itemCommentDisplay.getText().toString());
        } else {
            newItem.setComment("");
        }

        // Make
        if (binding.itemMakeDisplay.getText() != null) {
            newItem.setMake(binding.itemMakeDisplay.getText().toString());
        } else {
            newItem.setMake("");
        }

        // Model
        if (binding.itemModelDisplay.getText() != null) {
            newItem.setModel(binding.itemModelDisplay.getText().toString());
        } else {
            newItem.setModel("");
        }

        // Serial Number
        if (binding.itemSerialNumberDisplay.getText() != null) {
            newItem.setSerialNumber(binding.itemSerialNumberDisplay.getText().toString());
        } else {
            newItem.setSerialNumber("");
        }

        // Tags
        ArrayList<Tag> tags = binding.itemDisplayTagGroup.getTags();
        for (Tag tag : tags) {
            newItem.addTag(tag);
        }

        // Attach the existing ID to the item if we have it
        if (itemID != null && !itemID.isEmpty()) {
            newItem.attachID(itemID);
        }

        return newItem;
    }
}
