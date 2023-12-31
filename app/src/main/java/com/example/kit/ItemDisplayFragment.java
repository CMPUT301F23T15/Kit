package com.example.kit;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.data.Item;
import com.example.kit.data.Tag;
import com.example.kit.data.source.DataSourceManager;
import com.example.kit.databinding.ItemDisplayBinding;
import com.example.kit.util.FormatUtils;
import com.example.kit.util.ImageUtils;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.carousel.HeroCarouselStrategy;

/**
 * Fragment that displays an {@link Item} for viewing purposes.
 */
public class ItemDisplayFragment extends Fragment {

    private ItemDisplayBinding binding;
    private NavController navController;
    private String itemID;
    private CarouselImageAdapter imageAdapter;

    /**
     * Standard fragment lifecycle
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
    }

    /**
     * Standard fragment lifecycle, initializes binding, NavController, and UI elements
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
        binding = ItemDisplayBinding.inflate(inflater, container, false);
        initializeConfirmButton();
        initializeImageCarousel();
        disableInputs();
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
        binding.floatingActionButton.setOnClickListener(onClick -> navController.popBackStack());
        binding.floatingActionButton2.setOnClickListener(onClick -> {
            Bundle itemId = new Bundle();
            itemId.putString("id", itemID);
            navController.navigate(R.id.editDisplayItemAction, itemId);
        });
    }

    /**
     * Initialize the Carousel that displays the images associated with the item.
     */
    private void initializeImageCarousel() {
        // Create layout manager that makes the images morph and look pretty
        CarouselLayoutManager layoutManager
                = new CarouselLayoutManager(new HeroCarouselStrategy(), RecyclerView.HORIZONTAL);

        // Snap Helper snaps images into view instead of allowing free scrolling
        CarouselSnapHelper snapHelper = new CarouselSnapHelper();

        // Adapter for the RecyclerView, not in edit mode
        imageAdapter = new CarouselImageAdapter(false);

        // Attach all to the RecyclerView and set properties
        snapHelper.attachToRecyclerView(binding.imageCarousel);
        binding.imageCarousel.setLayoutManager(layoutManager);
        binding.imageCarousel.setAdapter(imageAdapter);
        binding.imageCarousel.setNestedScrollingEnabled(false);
    }

    /**
     * Disable input and set text color back to black for all edit texts. XML disable doesn't seem
     * to work.
     */
    private void disableInputs() {
        binding.itemNameDisplay.setEnabled(false);
        binding.itemNameDisplay.setTextColor(Color.BLACK);
        binding.itemDescriptionDisplay.setEnabled(false);
        binding.itemDescriptionDisplay.setTextColor(Color.BLACK);
        binding.itemCommentDisplay.setEnabled(false);
        binding.itemCommentDisplay.setTextColor(Color.BLACK);
        binding.itemDateDisplay.setEnabled(false);
        binding.itemDateDisplay.setTextColor(Color.BLACK);
        binding.itemValueDisplay.setEnabled(false);
        binding.itemValueDisplay.setTextColor(Color.BLACK);
        binding.itemMakeDisplay.setEnabled(false);
        binding.itemMakeDisplay.setTextColor(Color.BLACK);
        binding.itemModelDisplay.setEnabled(false);
        binding.itemModelDisplay.setTextColor(Color.BLACK);
        binding.itemSerialNumberDisplay.setEnabled(false);
        binding.itemSerialNumberDisplay.setTextColor(Color.BLACK);
    }

    /**
     * Loads an item's details into the UI components if editing an existing item.
     * Retrieves the item from the fragment's arguments.
     */
    private void loadItem() {
        // Return to previous screen if we didn't come with an item
        if (getArguments() == null) {
            Log.e("Navigation", "No arguments found for the transition to fragment.");
            return;
        }

        itemID = getArguments().getString("id");
        Item item = DataSourceManager.getInstance().getItemDataSource().getDataByID(itemID);

        // If the item was null, return to the previous screen
        if (item == null) {
            Log.e("Item Display Error", "No item found for the bundled ID");
            return;
        }

        // Use View Binding to populate UI elements with item data
        binding.itemNameDisplay.setText(item.getName());
        // Format value as a number, but remove the $ symbol because the new text views have icons
        binding.itemValueDisplay.setText(FormatUtils.formatValue(item.valueToBigDecimal(), false));
        binding.itemDateDisplay.setText(FormatUtils.formatDateStringShort(item.getAcquisitionDate()));
        binding.itemDescriptionDisplay.setText(item.getDescription());
        binding.itemCommentDisplay.setText(item.getComment());
        binding.itemMakeDisplay.setText(item.getMake());
        binding.itemModelDisplay.setText(item.getModel());
        binding.itemSerialNumberDisplay.setText(item.getSerialNumber());

        binding.itemDisplayTagGroup.removeAllViews();
        for (Tag tag : item.getTags()) {
            binding.itemDisplayTagGroup.addTag(tag);
        }

        for (String base64 : item.getBase64Images()) {
            imageAdapter.addImage(new CarouselImage(ImageUtils.convertBase64ToBitmap(base64)));
        }
    }
}