package com.imagePicker;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.utilities.AndroidFileProvider;

import java.io.File;
import java.io.IOException;


/**
 * @author Manish Kumar
 * @since 4/9/17
 */


public class FilePathHelper {

    private ProviderInfo getProviderInfo(Context context) {
        return context.getPackageManager()
                .resolveContentProvider(context.getPackageName(), PackageManager.GET_META_DATA);
    }

    private AndroidFileProvider getAndroidFileProvider(Context context) {
        AndroidFileProvider fileProvider = new AndroidFileProvider();
        fileProvider.attachInfo(context, getProviderInfo(context));
        return fileProvider;
    }

    private String getFilePathFromUri(Context context,
                                      Uri uri) throws IOException {
        String filePath;
        if ((filePath = getFilePathFromAppProvider(context, uri)) != null)
            return filePath;
        if ((filePath = getFilePathFromMediaDatabase(context, uri)) != null)
            return filePath;
        return getFilePathFromFileSystem(uri);

    }

    private String getFilePathFromAppProvider(Context context,
                                              Uri uri) {

        ProviderInfo providerInfo = getProviderInfo(context);
        if (providerInfo == null) return null;

        if (providerInfo.authority.equals(uri.getAuthority())) {

            AndroidFileProvider androidFileProvider = getAndroidFileProvider(context);
            File file = androidFileProvider.FileForUri(uri);
            if (file != null) {
                return file.getAbsolutePath();
            }

        }
        return null;
    }

    private String getFilePathFromMediaDatabase(Context context,
                                                Uri uri) {

        String[] fields = new String[]{MediaStore.MediaColumns.DATA};

        Cursor cursor = context.getContentResolver().query(uri,
                fields, null, null, null);
        if (cursor == null)
            return null;
        try {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    private Matrix getMatrixFromMediaDatabase(Context context,
                                              Uri uri) {
        Matrix matrix = null;
        String[] fields = new String[]{MediaStore.Images.Media.ORIENTATION};

        Cursor cursor = context.getContentResolver().query(uri,
                fields, null, null, null);
        if (cursor == null)
            return matrix;
        try {
            if (cursor.moveToFirst()) {
                try {
                    matrix = getMatrix(cursor.getInt(0));
                } catch (CursorIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            cursor.close();
        }

        return matrix;
    }

    private Matrix getMatrixFromFileSystem(String path) {
        Matrix matrix = null;

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            matrix = getMatrix(orientation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrix;
    }

    private Matrix getMatrix(int orientation) {
        Matrix matrix = null;
        if (orientation != 0) {
            matrix = new Matrix();
        }
        switch (orientation) {
            case 90:
                matrix.setRotate(90);
                break;
            case 180:
                matrix.setRotate(180);
                break;
            case 270:
                matrix.setRotate(-90);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setScale(1, -1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
        }
        return matrix;
    }

    private String getFilePathFromFileSystem(Uri uri)
            throws IOException {
        return uri.getPath();
    }

    public String getFileExtensionFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }

            int query = url.lastIndexOf('?');
            if (query > 0) {
                url = url.substring(0, query);
            }

            int filenamePos = url.lastIndexOf('/');
            String filename =
                    0 <= filenamePos ? url.substring(filenamePos + 1) : url;

            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            if (!filename.isEmpty()) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }

        return "";
    }

    private String getMimeType(String filePath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        if (extension.trim().isEmpty()) {
            extension = getFileExtensionFromUrl(filePath);
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public FileInformation getUriInformation(Context context, Uri uri) {
        try {
            String filePath = getFilePathFromUri(context, uri);
            if (filePath == null) return null;
            String mimeType = getMimeType(filePath);
            Matrix matrix = getMatrixFromMediaDatabase(context, uri);
            FileInformation fileInformation = new FileInformation();
            fileInformation.setFileUri(uri);
            fileInformation.setFilePath(filePath);
            fileInformation.setMimeType(mimeType);
            if (matrix != null) {
                fileInformation.setOrientation(matrix);
            } else {
                matrix = getMatrixFromFileSystem(filePath);
                if (matrix != null) {
                    fileInformation.setOrientation(matrix);
                } else {
                    fileInformation.setOrientation(new Matrix());
                }
            }
            return fileInformation;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
