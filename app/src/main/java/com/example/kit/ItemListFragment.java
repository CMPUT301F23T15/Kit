package com.example.kit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kit.data.Filter;
import com.example.kit.data.Tag;
import com.example.kit.databinding.FilterSheetBinding;
import com.example.kit.databinding.ItemListBinding;
import com.example.kit.util.FormatUtils;
import com.example.kit.views.TriStateSortButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;

/**
 * A Fragment that displays a RecyclerView that contains a list of {@link com.example.kit.data.Item},
 * Displays the total value of the items currently displayed. Controlled by {@link ItemListController}
 */
public class ItemListFragment extends Fragment implements SelectListener, ItemListController.ItemSetValueChangedCallback {

    // Item List Fields
    private ItemListBinding binding;
    private NavController navController;
    private ItemListController controller;
    private ItemAdapter adapter;
    private boolean inMultiSelectMode = false;

    // Filter Sheet Fields
    private FilterSheetBinding filterBinding;
    private final AlphaAnimation fadeInFAB;
    private final AlphaAnimation fadeOutFAB;

    {
        fadeOutFAB = new AlphaAnimation(1.0f, 0.0f);
        fadeOutFAB.setDuration(100);
        fadeOutFAB.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if (inMultiSelectMode) {
                    binding.deleteItemButton.setVisibility(View.GONE);
                } else {
                    binding.addItemButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        fadeInFAB = new AlphaAnimation(0.0f, 1.0f);
        fadeInFAB.setDuration(100);
        fadeInFAB.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if (inMultiSelectMode) {
                    binding.deleteItemButton.setVisibility(View.VISIBLE);
                } else {
                    binding.addItemButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get NavController for screen navigation
        navController = NavHostFragment.findNavController(this);
        // Create controller for the Item List
        controller = new ItemListController();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ItemListBinding.inflate(inflater, container, false);
        filterBinding = binding.filterSheet;
        initializeItemList();
        initializeUIInteractions();
        initializeFilterSheet();

        // Add self as callback for updates when the dataset changes
        controller.setCallback(this);
        return binding.getRoot();
    }

    /**
     * Initialize the RecyclerView of the fragment, setting the Adapter and registering this Fragment
     * as a listener for clicks.
     */
    private void initializeItemList() {
        adapter = new ItemAdapter();

        // Bind the fragment as a listener for Clicks, Long Clicks, and the add TagButton clicks
        adapter.setListener(this);

        // Make controller aware of the adapter
        controller.setItemAdapter(adapter);

        // Initialize RecyclerView with adapter and layout manager
        binding.itemList.setAdapter(adapter);
        binding.itemList.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
  
    /**
     * Initializes UI interactions, setting up listeners for add and delete buttons.
     */
    //TODO: Implement add and profile buttons here
    public void initializeUIInteractions(){
        binding.addItemButton.setOnClickListener(onClick -> {
            navController.navigate(R.id.newItemAction);
        });
        binding.deleteItemButton.setOnClickListener(onClick -> onDelete());
        binding.addItemButton.setOnClickListener(onClick -> navController.navigate(ItemListFragmentDirections.newItemAction()));
        Intent login = new Intent(getActivity(), ProfileActivity.class);
        binding.profileButton.setOnClickListener(onClick -> startActivity(login));
        binding.addTagsButton.setOnClickListener(onClick -> onAddTagMultipleItems());
    }

    private void initializeFilterSheet() {
        BottomSheetBehavior<ConstraintLayout> filterSheetBehavior = BottomSheetBehavior.from(filterBinding.getRoot());
        filterSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        fadeInFABs();
                        setCollapsedFilterLayout();
                        break;

                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_SETTLING:
                        fadeOutFABs();
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        setExpandedFilterLayout();
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });

        filterBinding.tagAutoCompleteField.setAdapter(controller.createTagAdapter(requireContext()));

        // When a Tag name is clicked, fetch the Tag from the name and remove it from the tag name
        // list so that tags that are already on the item cannot be added to the item
        filterBinding.tagAutoCompleteField.setOnItemClickListener((parent, view, position, id) -> {
            Tag addTag = controller.tagClickedAtPosition(position);
            filterBinding.tagsFilter.addTag(addTag);
//            if (!filterBinding.tagsFilter.isEmpty()) {
//                filterBinding.tagsFilter.setVisibility(View.VISIBLE);
//            }
            // Clear the field
            filterBinding.tagAutoCompleteField.setText("", false);
        });

        // Listener for enter key pressed to add a tag that doesn't exist
        filterBinding.tagAutoCompleteField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                String newTagName = filterBinding.tagAutoCompleteField.getText().toString();
                if (newTagName.isEmpty()) {
                    return false;
                }
                Tag addTag = controller.tagFilterEntered(newTagName);

                if (addTag != null) {
                    filterBinding.tagsFilter.addTag(addTag);
//                    if (!filterBinding.tagsFilter.isEmpty()) {
//                        filterBinding.tagsFilter.setVisibility(View.VISIBLE);
//                    }
                }

                // Clear the field
                filterBinding.tagAutoCompleteField.setText("", false);
                return true;
            }
            return false;
        });

        filterBinding.makeAutoCompleteField.setAdapter(controller.createMakeAdapter(requireContext()));

        filterBinding.makeAutoCompleteField.setOnItemClickListener((parent, view, position, id) -> {
            String make = controller.makeClickedAtPosition(position);
            filterBinding.makesFilter.addMake(make);
//            if (!filterBinding.makesFilter.isEmpty()) {
//                filterBinding.makesFilter.setVisibility(View.VISIBLE);
//            }
            filterBinding.makeAutoCompleteField.setText("", false);
        });

        filterBinding.makeAutoCompleteField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                String newMake = filterBinding.makeAutoCompleteField.getText().toString();
                if (newMake.isEmpty()) {
                    return false;
                }

                filterBinding.makesFilter.addMake(newMake);
//                if (!filterBinding.makesFilter.isEmpty()) {
//                    filterBinding.makesFilter.setVisibility(View.VISIBLE);
//                }

                // Clear the field
                filterBinding.makeAutoCompleteField.setText("", false);
                return true;
            }
            return false;
        });

        filterBinding.searchBar.addTextChangedListener(new FilterFieldChangedListener());
        filterBinding.dateStart.addTextChangedListener(new FilterFieldChangedListener());
        filterBinding.dateEnd.addTextChangedListener(new FilterFieldChangedListener());
        filterBinding.valueLow.addTextChangedListener(new FilterFieldChangedListener());
        filterBinding.valueHigh.addTextChangedListener(new FilterFieldChangedListener());

        filterBinding.dateEnd.setInputType(InputType.TYPE_NULL);
        filterBinding.dateEnd.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) openDatePicker();
        });
        filterBinding.dateEnd.setOnClickListener(v -> openDatePicker());

        filterBinding.dateStart.setInputType(InputType.TYPE_NULL);
        filterBinding.dateStart.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) openDatePicker();
        });
        filterBinding.dateStart.setOnClickListener(v -> openDatePicker());

        filterBinding.valueLow.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus &&  filterBinding.valueLow.getText() != null) {
                // Format the value in the field whenever focus is lost
                String valueText =  filterBinding.valueLow.getText().toString();
                String formattedValue = FormatUtils.formatValue(valueText, false);
                filterBinding.valueLow.setText(formattedValue);
            }
        });

        filterBinding.valueHigh.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && filterBinding.valueHigh.getText() != null) {
                // Format the value in the field whenever focus is lost
                String valueText = filterBinding.valueHigh.getText().toString();
                String formattedValue = FormatUtils.formatValue(valueText, false);
                filterBinding.valueHigh.setText(formattedValue);
            }
        });

        filterBinding.dateEndLayout.setEndIconOnClickListener(v -> {
            filterBinding.dateStart.setText("");
            filterBinding.dateEnd.setText("");
            filterBinding.dateEnd.clearFocus();
        });

        filterBinding.dateSortButton.setOnClickListener(v -> {
            TriStateSortButton.BUTTON_STATE state = filterBinding.dateSortButton.getCurrentState();
            filterBinding.tagSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.searchSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.valueSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.makeSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            // do something with the state
        });

        filterBinding.valueSortButton.setOnClickListener(v -> {
            TriStateSortButton.BUTTON_STATE state = filterBinding.valueSortButton.getCurrentState();
            filterBinding.tagSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.dateSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.searchSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.makeSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            // do something with the state
        });

        filterBinding.makeSortButton.setOnClickListener(v -> {
            TriStateSortButton.BUTTON_STATE state = filterBinding.makeSortButton.getCurrentState();
            filterBinding.tagSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.dateSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.valueSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.searchSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            // do something with the state
        });

        filterBinding.tagSortButton.setOnClickListener(v -> {
            TriStateSortButton.BUTTON_STATE state = filterBinding.tagSortButton.getCurrentState();
            filterBinding.searchSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.dateSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.valueSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.makeSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            // do something with the state
        });

        filterBinding.searchSortButton.setOnClickListener(v -> {
            TriStateSortButton.BUTTON_STATE state = filterBinding.searchSortButton.getCurrentState();
            filterBinding.tagSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.dateSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.valueSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
            filterBinding.makeSortButton.setCurrentState(TriStateSortButton.BUTTON_STATE.DEFAULT);
        });

    }

    private void openDatePicker() {
        // Open date range picker limited to 2010 to present to reduce lag on opening. Unfortunately
        // the date range picker from google is laggy and it is a known issue.
        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setValidator(DateValidatorPointBackward.now());
        constraintBuilder.setEnd(MaterialDatePicker.thisMonthInUtcMilliseconds());
        LocalDate earliestDate = LocalDate.of(2010, 1, 1);
        constraintBuilder.setStart(earliestDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());

        MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Date Filter Range")
                .setCalendarConstraints(constraintBuilder.build())
                .build();

        datePicker.addOnPositiveButtonClickListener(dateMillisPair -> {
            long twelveHourTimezoneOffset = 43200000L; // i hate it too
            String formattedStartDate = FormatUtils.formatDateStringShort(new Date(dateMillisPair.first + twelveHourTimezoneOffset));
            String formattedEndDate = FormatUtils.formatDateStringShort(new Date(dateMillisPair.second + twelveHourTimezoneOffset));
            filterBinding.dateStart.setText(formattedStartDate);
            filterBinding.dateEnd.setText(formattedEndDate);
        });

        datePicker.show(getParentFragmentManager(), "Date Picker");
    }

    private void updateFilter() {
        controller.updateKeywordFilter(filterBinding.searchBar.getText().toString());
        updatePriceFilter();
        updateDateFilter();
        Filter filter = new Filter();

        controller.updateDataFilter(filter);
    }

    private void setExpandedFilterLayout() {
    }

    private void setCollapsedFilterLayout() {
    }

    /**
     * Handles item click events. Opens item details if not in multiselect mode.
     * @param id The ID for the Item that was clicked.
     */
    @Override
    public void onItemClick(String id) {
        // Disable transition in delete mode
        if (inMultiSelectMode) return;

        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        navController.navigate(R.id.displayListItemAction, bundle);

        // Safe args doesn't work for me?
//        ItemListFragmentDirections.DisplayListItemAction action =
//                ItemListFragmentDirections.displayListItemAction(id);
//        navController.navigate(action);
    }

    /**
     * Handles long click events on items to change the UI mode for item deletion.
     */
    @Override
    public void onItemLongClick() {
        toggleMultiSelectMode();
    }

    private void fadeOutFABs() {
        if (inMultiSelectMode) {
            if (binding.deleteItemButton.getAnimation() != null || binding.deleteItemButton.getVisibility() == View.GONE) return;
            if (binding.addTagsButton.getAnimation() != null || binding.addTagsButton.getVisibility() == View.GONE) return;
            binding.deleteItemButton.startAnimation(fadeOutFAB);
            binding.addTagsButton.startAnimation(fadeOutFAB);
        } else {
            if (binding.addItemButton.getAnimation() != null || binding.addItemButton.getVisibility() == View.GONE) return;
            binding.addItemButton.startAnimation(fadeOutFAB);
        }
    }

    private void fadeInFABs() {
        if (inMultiSelectMode) {
            binding.deleteItemButton.startAnimation(fadeInFAB);
            binding.addTagsButton.startAnimation(fadeInFAB);
        } else {
            binding.addItemButton.startAnimation(fadeInFAB);
        }
    }

    /**
     * Toggles the UI mode between normal and multiselect mode, showing or hiding checkboxes and buttons accordingly.
     */
    private void toggleMultiSelectMode() {
        inMultiSelectMode = !inMultiSelectMode;

        if (inMultiSelectMode) { // Show delete button
            binding.addItemButton.setVisibility(View.GONE);
            binding.addTagsButton.setVisibility(View.VISIBLE);
            binding.deleteItemButton.setVisibility(View.VISIBLE);
        } else {            // Show add button
            binding.addItemButton.setVisibility(View.VISIBLE);
            binding.addTagsButton.setVisibility(View.GONE);
            binding.deleteItemButton.setVisibility(View.GONE);
        }



        toggleViewHolderCheckBoxes();
    }

    /**
     * Toggles the state of the checkboxes used for multi select items in the RecyclerView
     */
    private void toggleViewHolderCheckBoxes() {
        int numItems = adapter.getItemCount();
        for (int i = 0; i < numItems; i++) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) binding.itemList.findViewHolderForAdapterPosition(i);

            if (itemViewHolder == null) {
                Log.e("RecyclerView", "ItemViewHolder at position " + i + " was null.");
                continue;
            }

            if(inMultiSelectMode) {
                itemViewHolder.showCheckbox();
            } else {
                itemViewHolder.hideCheckbox();
            }
        }
    }

    /**
     * Handles the deletion of selected items. Collects all items marked for deletion and requests their removal.
     */
    public void onDelete(){
        controller.deleteCheckedItems(checkedItems());
        toggleMultiSelectMode();
        // TODO: Show SnackBar with option to undo
    }

    /**
     * Helper method to return the positions of the items within the RecyclerView that are checked.
     * @return HashSet of the checked item positions.
     */
    private HashSet<Integer> checkedItems() {
        HashSet<Integer> checkedPositions = new HashSet<>();
        int numItems = adapter.getItemCount();
        for (int i = 0; i < numItems; i++) {
            ItemViewHolder viewHolder = (ItemViewHolder) binding.itemList.findViewHolderForAdapterPosition(i);
            if (viewHolder == null) {
                Log.e("RecyclerView", "ItemViewHolder at position " + i + " was null.");
                continue;
            }

            if (viewHolder.isChecked()) {
                checkedPositions.add(i);
            }
        }
        return checkedPositions;
    }

    /**
     * Opens the {@link AddTagFragment} dialog to add tags to the item clicked on
     */
    @Override
    public void onAddTagClick(String itemID) {
        HashSet<String> itemIDs = new HashSet<>();
        itemIDs.add(itemID);

        Log.i("Tag Dialog", "Adding tags to a single item.");
        AddTagFragment dialogFragment = new AddTagFragment(itemIDs);
        dialogFragment.show(getChildFragmentManager(), "tag_input_dialog");
    }

    /**
     * Opens the {@link AddTagFragment} dialog to add tags to all the checked items.
     */
    private void onAddTagMultipleItems() {
        Log.i("Tag Dialog", "Adding tags to checked items.");
        AddTagFragment dialogFragment =
                new AddTagFragment(controller.getItemIDsAtPositions(checkedItems()));
        dialogFragment.show(getChildFragmentManager(), "tag_input_dialog");
        toggleMultiSelectMode();
    }

    /**
     * Updates the displayed total value of all items in the set.
     * Called by {@link ItemListController} when the dataset changes.
     * @param value The total value to display.
     */
    @Override
    public void onItemSetValueChanged(BigDecimal value) {
        String formattedValue = NumberFormat.getCurrencyInstance().format(value);
        binding.itemSetTotalValue.setText(formattedValue);
    }

    private class FilterFieldChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            updateFilter();
        }
    }

    private void updateDateFilter() {
        String lowerDate = filterBinding.dateStart.getText().toString();
        String upperDate = filterBinding.dateEnd.getText().toString();
        controller.updateDateRangeFilter(lowerDate, upperDate);
    }

    private void updatePriceFilter() {
        String lowerPrice = filterBinding.valueLow.getText().toString();
        lowerPrice = FormatUtils.cleanupDirtyValueString(lowerPrice);
        String upperPrice = filterBinding.valueHigh.getText().toString();
        upperPrice = FormatUtils.cleanupDirtyValueString(upperPrice);

        controller.updatePriceRangeFilter(lowerPrice, upperPrice);
    }
}
