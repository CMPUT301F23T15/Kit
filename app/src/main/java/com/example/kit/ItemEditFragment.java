package com.example.kit;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.text.InputType;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;

import com.google.firebase.Timestamp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);

    // Image Carousel Fields
    private CarouselImageAdapter imageAdapter;
    private ArrayList<CarouselImage> images;

    // Tag Dropdown Fields
    private ArrayAdapter<String> tagNameAdapter;
    private ArrayList<String> tagNames;
    private boolean tagFieldFocused = false;

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
        initializeDateField();
        initializeScrollBehaviorForTagField();
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

    private void initializeScrollBehaviorForTagField() {
        binding.tagAutoCompleteField.setOnFocusChangeListener((v, hasFocus) -> {
            tagFieldFocused = hasFocus;
            int[] pos = new int[2];
            binding.itemDisplayTagGroup.getLocationOnScreen(pos);
            binding.scrollView3.smoothScrollTo(0, pos[1]);
        });

        ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean wasOpened = false;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                binding.scrollView3.getWindowVisibleDisplayFrame(r);
                int screenHeight = binding.scrollView3.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15 && tagFieldFocused) {
                    int[] pos = new int[2];
                    binding.itemDisplayTagGroup.getLocationOnScreen(pos);
                    binding.scrollView3.smoothScrollTo(0, pos[1]);
                }

                if (!wasOpened) {
                    binding.scrollView3.getViewTreeObserver().addOnGlobalLayoutListener(this);
                    wasOpened = true;
                } else {
                    binding.scrollView3.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    wasOpened = false;
                }
            }
        };

        binding.scrollView3.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    /**
     * Initializes the floating action button to handle the creation or update of an item in the Firestore database.
     */
    private void initializeConfirmButton() {
        binding.floatingActionButton.setOnClickListener(onClick -> {
            // Add item to the database and navigate back to the list
            if(validateFields()) {
                CommandManager.getInstance().executeCommand(new AddItemCommand(buildItem()));
                navController.navigate(ItemEditFragmentDirections.itemCreatedAction());
            }
        });
    }

    private void initializeImageCarousel() {
        images = new ArrayList<>();
        CarouselLayoutManager layoutManager
                = new CarouselLayoutManager(new HeroCarouselStrategy(), RecyclerView.VERTICAL);
        binding.imageCarousel.setLayoutManager(layoutManager);
        binding.imageCarousel.setNestedScrollingEnabled(false);
        CarouselSnapHelper snapHelper = new CarouselSnapHelper();
        snapHelper.attachToRecyclerView(binding.imageCarousel);
        imageAdapter = new CarouselImageAdapter();
        binding.imageCarousel.setAdapter(imageAdapter);





        ActivityResultLauncher<String> getContentLauncher = registerForActivityResult(new GetImageURIResultContract(), this::onImagePicked);
        binding.tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentLauncher.launch("image/*");
            }
        });
    }


    private void onImagePicked(Uri imageURI) {
        Bitmap bitmap;
        ContentResolver contentResolver = requireContext().getContentResolver();
        try {
            InputStream inputStream  = contentResolver.openInputStream(imageURI);
            bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        bitmap = BitmapFactory.decodeStream(inputStream);
//        Glide.with(requireContext())
//                .asBitmap()
//                .load(bitmap)
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        binding.testImageView.setImageBitmap(resource);
//                        Log.i("image", "reasource ready");
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                    }
//                });

        images.add(new CarouselImage(bitmap));
        imageAdapter.submitList(images);
    }

    private void initializeItemValueField() {
        binding.itemValueDisplay.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String input = binding.itemValueDisplay.getText().toString();
                if (input.isEmpty()) return;
                input = input.replaceAll("[^\\d.]", "");

                double parsedInput = Double.parseDouble(input);

                String formattedValue =
                        NumberFormat.getCurrencyInstance().format(parsedInput).substring(1);

                binding.itemValueDisplay.setText(formattedValue);
            }
        });
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

    /**
     * Sets a click and focus listener for the item date display to open a date picker when it's clicked
     */
    private void initializeDateField() {
        binding.itemDateDisplay.setInputType(InputType.TYPE_NULL);

        binding.itemDateDisplay.setOnFocusChangeListener((view, hasFocus) -> {
            if(hasFocus) {
                openDatePicker();
            }
        });

        binding.itemDateDisplay.setOnClickListener(view -> openDatePicker());
    }

    /**
     * Validates the mandatory fields for an item prior to creation or update
     * @return True, if all of the fields are valid, false otherwise
     */
    private boolean validateFields() {
        String name, value, date;
        if (binding.itemNameDisplay.getText() == null) {
            name = "";
        } else {
            name = binding.itemNameDisplay.getText().toString();
        }

        if (binding.itemDateDisplay.getText() == null) {
            date = "";
        } else {
            date = binding.itemDateDisplay.getText().toString();
        }

        if (binding.itemValueDisplay.getText() == null) {
            value = "";
        } else {
            value = binding.itemValueDisplay.getText().toString();
        }

        boolean validName = true;
        boolean validValue = true;
        boolean validDate = true;

        binding.itemNameDisplayLayout.setError(null);
        binding.itemValueDisplayLayout.setError(null);
        binding.itemDateDisplayLayout.setError(null);

        // Test empty values
        if (name.equals("")) {
            binding.itemNameDisplayLayout.setError("Name is required");
            validName = false;
        }
        if (value.equals("")) {
            binding.itemValueDisplayLayout.setError("Value is required");
            validValue = false;
        }
        if (date.equals("")) {
            binding.itemDateDisplayLayout.setError("Date is required");
            validDate = false;
        }

        return validName && validValue && validDate;
    }

    /**
     * Opens a date picker, when the item date field is clicked, and sets the date to what the user selects
     * Only allows dates to be selected from before the present time.
     */
    private void openDatePicker() {
        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setValidator(DateValidatorPointBackward.now());

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Acquisition Date")
                .setCalendarConstraints(constraintBuilder.build())
                .build();

        datePicker.addOnPositiveButtonClickListener(dateSelectionMillis -> {
            binding.itemDateDisplay.setText(dateFormat.format(new Date(dateSelectionMillis)));
        });

        datePicker.show(getParentFragmentManager(), "Date Picker");
    }

    /**
     * Loads an item's details into the UI components if editing an existing item. Retrieves the
     * item from the fragment's arguments.
     */
    private void loadItem() {
        // Return to previous screen if we did not arrive with any arguments
        if (getArguments() == null) {
            Log.e("Navigation", "Null Arguments in the edit item fragment");
            return;
        }

        // Retrieve the item from the bundle
        String id = getArguments().getString("id");
        this.itemID = id;
        Item item = DataSourceManager.getInstance().getItemDataSource().getDataByID(id);

        // If the item was null, return to the previous screen
        if (item == null) {
            Log.e("Item Display Error", "No item found for the bundled ID");
            return;
        }

        this.itemID = item.findID();

        // Use View Binding to populate UI elements with item data
        binding.itemNameDisplay.setText(item.getName());


        String value = item.getValue();
        value = value.replaceAll("[^\\d.]", "");
        double parsedInput;
        try {
            parsedInput = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            parsedInput = 0;
        }

        String formattedValue =
                NumberFormat.getCurrencyInstance().format(parsedInput).substring(1);

        binding.itemValueDisplay.setText(formattedValue);

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
            // Remove any commas, periods, and whitespace
            String cleanString = binding.itemValueDisplay.getText().toString().replaceAll("[,.\\s]", "");
            newItem.setValue(cleanString);
        } else {
            newItem.setValue("0");
        }

        // Date
        try {
            Date date = DateFormat.getDateInstance(DateFormat.SHORT).parse(binding.itemDateDisplay.getText().toString());
            newItem.setAcquisitionDate(new Timestamp(date));
        } catch (ParseException e) {
            Log.e("Item Edit Fragment", "Date parsing error. Something went very wrong to get here");
        } catch (NullPointerException e) {
            Log.e("Item Edit Fragment", "Date field value was null, somehow.");
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
