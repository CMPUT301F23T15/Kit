package com.example.kit.util;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    private static final int JPEG_QUALITY = 50;

    public static Bitmap convertUriToBitmap(Uri imageUri, Context context) {
        Bitmap bitmap;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            InputStream inputStream  = contentResolver.openInputStream(imageUri);
            bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }

    public static String imageUriToBase64String(Uri imageUri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            InputStream inputStream = contentResolver.openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap convertBase64ToBitmap(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static class GetImageURIResultContract extends ActivityResultContract<String, Uri> {

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
}
