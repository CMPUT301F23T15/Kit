package com.example.kit;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;

public class GetImageURIResultContract extends ActivityResultContract<String, Uri> {

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String input) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        return intent;
    }

    @Override
    public Uri parseResult(int resultCode, Intent intent) {
        if (resultCode != RESULT_OK || intent == null) {
            return null;
        }
        return intent.getData();
    }
}
