package com.example.kit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddTagFragment extends DialogFragment {
    private EditText tag_name;
    private EditText tag_color;
    private OnFragmentInteractionListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context + "OnFragmentInteractionListener is not implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_tag, null);

        tag_name = view.findViewById(R.id.TagName);
        tag_color = view.findViewById(R.id.TagColor);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setTitle("Add Tag to Item")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", (dialog, which) -> {
                    String name = tag_name.getText().toString();
                    String color = tag_color.getText().toString();
                    if (name.isEmpty() || color.isEmpty()) {
                        return;
                    }
                    //listener.onOKPressed(new Tag(name, color));
                }).create();
    }

    public interface OnFragmentInteractionListener {
        //void onOKPressed(Tag tag);
    }

}