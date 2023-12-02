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

import com.example.kit.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Collection of utility functions for converting images between formats, and fetching images
 * from the device
 */
public class ImageUtils {
    private static final int JPEG_QUALITY = 50;

    /**
     * Convert an image Uri to Bitmap with compression
     * @param imageUri The image Uri
     * @param context The context with which this conversion should take place in
     * @return The Bitmap image
     */
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

    /**
     * Convert an image Uri to a Base64 representation of the image
     * @param imageUri The image Uri
     * @param context The context with which this conversion should take place in
     * @return The Base64 string representation
     */
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

    /**
     * Convert a Base64 string representation of an image into a Bitmap
     * @param base64 The Base64 string representation of an image
     * @return The Bitmap representation of that image
     */
    public static Bitmap convertBase64ToBitmap(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * Convert a Bitmap image into a Base64 string representation of that image
     * @param bitmap The Bitmap image
     * @return The Base64 string representation of the image
     */
    public static String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // From https://stackoverflow.com/a/69580381 by dotrinh DM and sankettt
    public static Bitmap cropBitmapToThumbnail(Bitmap src, Context context) {
        Bitmap bitmapRes;
        int imageWidth = src.getWidth();
        int imageHeight = src.getHeight();

        float newWidth = context.getResources().getDimension(R.dimen.item_thumbnail_size);
        float scaleFactor = newWidth / imageWidth;
        int newHeight = (int) (imageHeight * scaleFactor);
        bitmapRes = Bitmap.createScaledBitmap(src, (int) newWidth, newHeight, true);
        int scaledH = bitmapRes.getHeight();
        int scaledW = bitmapRes.getWidth();
        int dimen = Math.min(scaledW, scaledH);
        bitmapRes = Bitmap.createBitmap(bitmapRes, 0, 0, dimen, dimen);
        return bitmapRes;
    }

    /**
     * An {@link ActivityResultContract} implementation for launching an image picker
     */
    public static class GetImageURIResultContract extends ActivityResultContract<String, Uri> {

        /**
         * Creates an intent for picking an image from the device
         * @param context Context within this is launched
         * @param input Technically not necessary, hardcoded for "image/*"
         * @return The intent for this Contract
         */
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, String input) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            return intent;
        }

        /**
         * Parse the result for an image Uri if it was successful
         * @param resultCode Result code of the intent
         * @param intent Intent of the contract
         * @return The image Uri if it was successful, otherwise null.
         */
        @Override
        public Uri parseResult(int resultCode, Intent intent) {
            if (resultCode != RESULT_OK || intent == null) {
                return null;
            }
            return intent.getData();
        }
    }
}
