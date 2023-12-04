package com.example.kit;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
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
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.command.AddItemCommand;
import com.example.kit.command.AddTagCommand;
import com.example.kit.command.CommandManager;
import com.example.kit.util.ImageUtils;
import com.example.kit.data.Item;
import com.example.kit.data.Tag;
import com.example.kit.data.source.DataSource;
import com.example.kit.data.source.DataSourceManager;
import com.example.kit.databinding.ItemEditBinding;

import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.carousel.HeroCarouselStrategy;

import com.example.kit.util.FormatUtils;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;

import com.google.firebase.Timestamp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * ItemDisplayFragment is a Fragment subclass used to display details of an {@link Item} object.
 * It supports creating a new item or editing an existing one, integrating with Firestore for data persistence.
 */
public class ItemEditFragment extends Fragment implements CarouselImageViewHolder.OnAddImageListener {

    // Fragment Fields
    private String itemID;
    private ItemEditBinding binding;
    private NavController navController;
    private DataSource<Tag, ArrayList<Tag>> tagDataSource;

    // Image Carousel Fields
    private CarouselImageAdapter imageAdapter;
    private CarouselSnapHelper snapHelper;
    private CarouselLayoutManager layoutManager;
    private ActivityResultLauncher<String> getContentLauncher;

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

    /**
     * Adds behavior to scroll to make the tag chip group visible when editing the tag field
     */
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

    /**
     * Initialize the Image Carousel, setting the layout and snap helpers in addition to an adapter
     * that manages clicks within the ViewHolders for adding and deleting images.
     */
    private void initializeImageCarousel() {
        // Create layout manager that makes the images morph and look pretty
        layoutManager
                = new CarouselLayoutManager(new HeroCarouselStrategy(), RecyclerView.HORIZONTAL);

        // Snap Helper snaps images into view instead of allowing free scrolling
        snapHelper = new CarouselSnapHelper();

        // Adapter for the RecyclerView, with this as a listener for when prompted to add new images
        // This is the edit fragment, so we are in edit mode.
        imageAdapter = new CarouselImageAdapter(true);
        imageAdapter.setAddImageListener(this);

        // Attach all to the RecyclerView and set properties
        snapHelper.attachToRecyclerView(binding.imageCarousel);
        binding.imageCarousel.setLayoutManager(layoutManager);
        binding.imageCarousel.setAdapter(imageAdapter);
        binding.imageCarousel.setNestedScrollingEnabled(false);

        // Update the add button's enabled/disabled status whenever the carousel is settled on an
        // image
        binding.imageCarousel.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) return;
                updateAddButtonStatus();
            }
        });


        // Create a content launcher for when we want to add images from the gallery.
        getContentLauncher =
                registerForActivityResult(new ImageUtils.GetImageURIResultContract(), this::onImagePicked);
        // To use the above: getContentLauncher.launch("image/*")
    }

    /**
     * Helper method to disable the AddImageButton unless it is the ViewHolder that is snapped in place
     */
    private void updateAddButtonStatus() {
        View snapView = snapHelper.findSnapView(layoutManager);
        if (snapView == null) return;

        // Get the position of the view within the Adapter that we are snapped to
        int snapPos;
        try {
            snapPos = Objects.requireNonNull(
                    binding.imageCarousel.findContainingViewHolder(snapView)).getBindingAdapterPosition();
        } catch (NullPointerException e){
            return;
        }

        // Loop through the children of the RecyclerView and only allow the add image button to work
        // if it is the view that is snapped to.
        // There has to a better way to do this, but I don't know how.
        for (int i = 0; i < binding.imageCarousel.getChildCount(); i++) {
            CarouselImageViewHolder viewHolder = (CarouselImageViewHolder) binding.imageCarousel.getChildViewHolder(binding.imageCarousel.getChildAt(i));
            viewHolder.setAllowAdd(snapPos == imageAdapter.getItemCount() - 1);
        }
    }


    /**
     * Callback method from selecting images on the device, converts the Uri and adds it to the list
     * @param imageURI The Uri of the image result from the Launcher
     */
    private void onImagePicked(Uri imageURI) {
        if (imageURI == null) return;

        Bitmap bitmap = ImageUtils.convertUriToBitmap(imageURI, requireContext());
        try {
            bitmap = ImageUtils.rotateImageIfRequired(requireContext(), bitmap, imageURI);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Image adapter notifies itself of the dataset change
        imageAdapter.addImage(new CarouselImage(bitmap));
        if (imageAdapter.getItemCount() < 2) {
            binding.imageCarousel.scrollToPosition(imageAdapter.getItemCount()-1);
            binding.imageCarousel.smoothScrollToPosition(0);
        } else {
            binding.imageCarousel.scrollToPosition(imageAdapter.getItemCount()-1);
            binding.imageCarousel.smoothScrollToPosition(imageAdapter.getItemCount()-2);
        }

    }

    /**
     * Launches an interface to select between adding images from the device or taking new pictures
     */
    @Override
    public void onAddImageClick() {
        ImageSourceModal dialog = new ImageSourceModal();
        dialog.setGalleryButtonListener(v -> getContentLauncher.launch("image/*"));
        dialog.setCameraButtonListener(v -> onTakePhoto());
        dialog.show(requireActivity().getSupportFragmentManager(), dialog.getTag());
    }

    private void onTakePhoto() {
        Log.d("takephoto", "reached");
        checkAndRequestPermissions();
    }
    private static final String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            // Add other required permissions as needed
    };
    //Checks Permissions and then calls start camera
    private void checkAndRequestPermissions() {
        // Check if permissions are already granted

        boolean allPermissionsGranted = true;
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                Log.d("cr1", "reached");
                //break;
                startCamera();
            }
        }

        // If any permission is not granted, request permissions
        if (!allPermissionsGranted) {
            String[] permissionsToRequest = Arrays.stream(REQUIRED_PERMISSIONS)
                    .filter(permission -> ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED)
                    .toArray(String[]::new);
            activityResultLauncher.launch(permissionsToRequest);
        } else {
            // All permissions are already granted, proceed with your logic
            Log.d("cr2", "reached");
            startCamera();
        }
    }

    private final ActivityResultLauncher<String[]> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                // Handle Permission granted/rejected
                boolean permissionGranted = true;
                for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                    if (Arrays.asList(REQUIRED_PERMISSIONS).contains(entry.getKey()) && !entry.getValue()) {
                        permissionGranted = false;
                    }
                }
                if (!permissionGranted) {
                    //to be done
                } else {
                    onTakePhoto();
                }
            });

    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    onImagePicked(data.getData());
                }
            });

    //Opens new activity

    private void startCamera() {
        Intent i = new Intent(requireContext(), CameraActivity.class);
        someActivityResultLauncher.launch(i);
    }

    /**
     * Adds input formatting to the Value field, formatting it as a currency string without the
     * currency symbol.
     */
    private void initializeItemValueField() {
        binding.itemValueDisplay.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && binding.itemValueDisplay.getText() != null) {
                // Format the value in the field whenever focus is lost
                String valueText = binding.itemValueDisplay.getText().toString();
                String formattedValue = FormatUtils.formatValue(valueText, false);
                binding.itemValueDisplay.setText(formattedValue);
            }
        });
    }

    /**
     * Create the list of existing tags to add to the Tag dropdown field, and provide click listeners
     * for clicking items in the list, as well as providing function for adding new tags typed
     * in the field.
     */
    private void initializeTagField() {
        // Add all existing tags to the list of tags for the dropdown
        tagNames = new ArrayList<>();
        ArrayList<Tag> dbTags = tagDataSource.getDataSet();
        for (Tag tag : dbTags) {
            tagNames.add(tag.getName());
        }

        tagNameAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, tagNames);
        binding.tagAutoCompleteField.setAdapter(tagNameAdapter);


        // When a Tag name is clicked, fetch the Tag from the name and remove it from the tag name
        // list so that tags that are already on the item cannot be added to the item
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

        // Check the date format
        if (FormatUtils.parseDateString(date) == null) {
            binding.itemDateDisplayLayout.setError("Date incorrectly formatted");
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
            String formattedDateString = FormatUtils.formatDateStringShort(new Date(dateSelectionMillis));
            binding.itemDateDisplay.setText(formattedDateString);
        });

        datePicker.show(getParentFragmentManager(), "Date Picker");
    }

    /**
     * Loads an item's details into the UI components if editing an existing item. Retrieves the
     * item from the fragment's arguments.
     */
    private void loadItem() {
        Item item = null;
        // Log and display nothing if we did not have an argument
        if (getArguments() == null) {
            Log.i("Navigation", "Null Arguments in the edit item fragment");
            return;
        } else if (getArguments().getString("Barcode") != null) {
            // Handled barcode information lookup, use now as default timestamp
            String barcode = getArguments().getString("Barcode");
            if(DataSourceManager.getInstance().barcodeDataSource().isItemInCache(barcode)){
                // If item is in static cache, make new item
                item = DataSourceManager.getInstance().barcodeDataSource().getItemByBarcode(barcode);
                item.setAcquisitionDate(Timestamp.now());
                item.setSerialNumber(barcode);
            } else {
                // In case item is not in static cache, populate select fields
                binding.itemDateDisplay.setText(FormatUtils.formatDateStringShort(Timestamp.now()));
                binding.itemSerialNumberDisplay.setText(barcode);
                return;
            }

        } else {
            // Retrieve the item from the bundle
            itemID = getArguments().getString("id");
            item = DataSourceManager.getInstance().getItemDataSource().getDataByID(itemID);
        }




        // If the item was null, Log and display nothing
        if (item == null) {
            Log.e("Item Display Error", "No item found for the bundled ID");
            return;
        }

        this.itemID = item.findID();

        // Use View Binding to populate UI elements with item data
        binding.itemNameDisplay.setText(item.getName());
        binding.itemValueDisplay.setText(FormatUtils.formatValue(item.valueToBigDecimal(),false));
        binding.itemDateDisplay.setText(FormatUtils.formatDateStringShort(item.getAcquisitionDate()));
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

        for (String base64 : item.getBase64Images()) {
            imageAdapter.addImage(new CarouselImage(ImageUtils.convertBase64ToBitmap(base64)));
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
            String cleanValue = FormatUtils.cleanupDirtyValueString(binding.itemValueDisplay.getText().toString());
            newItem.setValue(cleanValue);
        } else {
            newItem.setValue("0");
        }

        // Date
        if (binding.itemDateDisplay.getText() != null) {
            Timestamp date = FormatUtils.parseDateString(binding.itemDateDisplay.getText().toString());
            newItem.setAcquisitionDate(date);
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

        // Images
        ArrayList<CarouselImage> itemImages = imageAdapter.getImages();
        for (CarouselImage carouselImage : itemImages) {
            String base64 = ImageUtils.convertBitmapToBase64(carouselImage.getImage());
            newItem.addBase64ImageString(base64);
        }

        return newItem;
    }
}
