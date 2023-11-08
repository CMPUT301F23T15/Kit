package com.example.kit;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AddTagFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        // Inflate a custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.add_tag, null);
        builder.setView(dialogView);

        // Set positive and negative buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle tag input and store in strings
                // Retrieve tag name and color from the dialog's views
                EditText searchTag = dialogView.findViewById(R.id.TagSearch);
                String search = searchTag.getText().toString();
                Spinner addtag = dialogView.findViewById(R.id.TagName);
                String add = addtag.getSelectedItem().toString();


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}