package com.example.kit.util;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;

import com.example.kit.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

    public static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {
        String path = getPathFromUri(context, selectedImage);
        File imageFile = new File(path);

        ExifInterface ei = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            ei = new ExifInterface(imageFile.getAbsoluteFile());
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    // From https://stackoverflow.com/a/39388941/11318958 by @SANJAY GUPTA
    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    // From https://stackoverflow.com/a/39388941/11318958 by @SANJAY GUPTA
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    // From https://stackoverflow.com/a/39388941/11318958 by @SANJAY GUPTA
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    // From https://stackoverflow.com/a/39388941/11318958 by @SANJAY GUPTA
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    // From https://stackoverflow.com/a/39388941/11318958 by @SANJAY GUPTA
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    // From https://stackoverflow.com/a/39388941/11318958 by @SANJAY GUPTA
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
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
