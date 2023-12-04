package com.example.kit;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.android.material.chip.Chip;
import org.hamcrest.Matcher;

public class ClickCloseIconAction implements ViewAction {

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Matcher<View> getConstraints() {
        return ViewMatchers.isAssignableFrom(Chip.class);
    }

    @Override
    public void perform(UiController uiController, View view) {
        Chip chip = (Chip) view;
        chip.performCloseIconClick();
    }
}
