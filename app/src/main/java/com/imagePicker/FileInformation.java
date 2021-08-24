package com.imagePicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;

import java.io.File;

/**
 * Created by bitu on 9/9/17.
 */

public class FileInformation {

    public static final int IMAGE_SIZE_THUMB = 300;
    public static final int IMAGE_SIZE_LARGE = 800;

    Uri fileUri;
    String filePath;
    String mimeType;
    Matrix orientation = new Matrix();


    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Matrix getOrientation() {
        return orientation;
    }

    public void setOrientation(Matrix orientation) {
        this.orientation = orientation;
    }

    public boolean hasFilePath() {
        return filePath != null && !filePath.trim().isEmpty();
    }

    public boolean checkValidFileInformation() {
        return fileUri != null || hasFilePath();
    }

    public boolean isVideo() {
        return mimeType != null && mimeType.trim().toLowerCase().startsWith("video");
    }

    public boolean isImage() {
        return mimeType != null && mimeType.trim().toLowerCase().startsWith("image");
    }

    public Bitmap getThumbBitmap(Context context) {
        if (isImage()) {
            return new BitmapHelper().getImageBitmap(context, this, IMAGE_SIZE_THUMB, IMAGE_SIZE_THUMB);
        } else if (isVideo()) {
            return new BitmapHelper().getVideoBitmap(filePath);
        }
        return null;
    }

    public String getBitmapPathForUpload(Context context, int imageWidth, int imageHeight, String imageName) {

        String imageDir = "";
        int index = imageName.lastIndexOf("/");
        if (index != -1) {
            imageDir = imageName.substring(0, index + 1);
            imageName = imageName.substring(index + 1);
            if (!imageDir.startsWith("/")) {
                imageDir = "/" + imageDir;
            }
        }

        imageDir = context.getFilesDir().getAbsolutePath() + imageDir;
        if (!new File(imageDir).exists()) {
            new File(imageDir).mkdirs();
        }

        BitmapHelper bitmapHelper = new BitmapHelper();
        if (!isImage() && !isVideo()) return null;
        Bitmap bitmap;
        if (isVideo()) {
            bitmap = bitmapHelper.getVideoBitmap(filePath);
        } else {
            bitmap = bitmapHelper.getImageBitmap(context, this, imageWidth, imageHeight);
        }
        String newFilePath = new File(imageDir, imageName
                + "." + bitmapHelper.getExtenstion(this.filePath)).getAbsolutePath();
        return bitmapHelper.saveBitmap(newFilePath, bitmap);
    }


}
