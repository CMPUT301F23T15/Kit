package com.example.kit.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;

import com.example.kit.R;

import java.util.HashMap;

public class TriStateSortButton extends AppCompatImageButton {

    public enum BUTTON_STATE {
        ASCENDING,
        DEFAULT,
        DESCENDING
    }

    private BUTTON_STATE currentState = BUTTON_STATE.DEFAULT;
    private HashMap<BUTTON_STATE, Integer> stateIcons;

    public TriStateSortButton(@NonNull Context context) {
        super(context);
        initialize();
    }

    public TriStateSortButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public TriStateSortButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        stateIcons = new HashMap<>();
        stateIcons.put(BUTTON_STATE.DESCENDING, R.drawable.baseline_arrow_circle_down_24);
        stateIcons.put(BUTTON_STATE.DEFAULT, R.drawable.baseline_remove_circle_outline_24);
        stateIcons.put(BUTTON_STATE.ASCENDING, R.drawable.baseline_arrow_circle_up_24);
        updateIcon();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener listener) {
        OnClickListener internalListener = v -> {
            if (listener != null) {
                listener.onClick(v);
            }
            onClickInternal();
        };
        super.setOnClickListener(internalListener);
    }

    private void onClickInternal() {
        currentState = getNextState(currentState);
        updateIcon();
    }

    private void updateIcon() {
        setImageResource(stateIcons.get(currentState));
    }

    private BUTTON_STATE getNextState(BUTTON_STATE currentState) {
        switch (currentState) {
            case DEFAULT:
                return BUTTON_STATE.DESCENDING;
            case DESCENDING:
                return BUTTON_STATE.ASCENDING;
            case ASCENDING:
                return BUTTON_STATE.DEFAULT;
            default:
                return BUTTON_STATE.DEFAULT;
        }
    }

    public BUTTON_STATE getCurrentState() {
        return currentState;
    }

    public void setCurrentState(BUTTON_STATE state) {
        this.currentState = state;
        updateIcon();
    }
}
