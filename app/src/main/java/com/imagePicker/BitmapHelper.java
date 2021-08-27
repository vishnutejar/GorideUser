package com.imagePicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;

/**
 * Created by bitu on 9/9/17.
 */

public class BitmapHelper {



    public static Bitmap convertBitmapToFillWidth(Bitmap bitmap, int width) {
        float bitmapWidth = bitmap.getWidth();
        float bitmapHeight = bitmap.getHeight();
        if (bitmapWidth > 0 && bitmapHeight > 0 && width > 0) {
            float multiplier = width / bitmapWidth;
            bitmapWidth = bitmapWidth * multiplier;
            bitmapHeight = bitmapHeight * multiplier;

            if (bitmapWidth > 0 && bitmapHeight > 0) {
                return Bitmap.createScaledBitmap(bitmap, Math.round(bitmapWidth),
                        Math.round(bitmapHeight), false);

            } else {
                return bitmap;
            }
        }
        return null;

    }

    public static Bitmap getBlurBitmap(Bitmap realBitmap, int width) {
        Paint mPaint = new Paint();

        realBitmap = convertBitmapToFillWidth(realBitmap, width);
        if (realBitmap != null) {
            final Bitmap resultBitmap = Bitmap.createBitmap(realBitmap.getWidth(), realBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBitmap);
            mPaint.setAlpha(180);
            canvas.drawBitmap(realBitmap, 0, 0, mPaint);
            int blurRadius = 12;
            for (int row = -blurRadius; row < blurRadius; row += 2) {
                for (int col = -blurRadius; col < blurRadius; col += 2) {
                    if (col * col + row * row <= blurRadius * blurRadius) {
                        mPaint.setAlpha((blurRadius * blurRadius) / ((col * col + row * row) + 1) * 2);
                        canvas.drawBitmap(realBitmap, row, col, mPaint);
                    }
                }
            }
            realBitmap.recycle();
            return resultBitmap;
        }
        return null;
    }

    public InputStream openInputStream(String filePath) throws FileNotFoundException {
        return new FileInputStream(filePath);
    }

    public InputStream openInputStream(Context context, Uri fileUri) throws FileNotFoundException {
        return context.getContentResolver().openInputStream(fileUri);
    }

    public int[] getActualSize(Context context, FileInformation fileInformation) throws FileNotFoundException {
        if (fileInformation.checkValidFileInformation()) {

            if (fileInformation.hasFilePath()) {
                return getActualSize(fileInformation.getFilePath());
            }
            return getActualSize(context, fileInformation.getFileUri());

        }
        return new int[2];
    }

    public int[] getActualSize(String filePath) throws FileNotFoundException {
        if (filePath == null || filePath.trim().isEmpty()) return new int[2];

        InputStream inputStream = openInputStream(filePath);
        if (inputStream != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (options.outHeight > 0 && options.outWidth > 0)
                return new int[]{options.outWidth, options.outHeight};
        }
        return new int[2];
    }

    public int[] getActualSize(Context context, Uri fileUri) throws FileNotFoundException {
        if (context == null || fileUri == null) return new int[2];

        InputStream inputStream = openInputStream(context, fileUri);
        if (inputStream != null) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (options.outHeight > 0 && options.outWidth > 0)
                return new int[]{options.outWidth, options.outHeight};
        }
        return new int[2];
    }

    public Bitmap getImageBitmap(String imagePath, int width, int height) {
        InputStream inputStream = null;
        try {
            int[] actualSize = getActualSize(imagePath);
            if (actualSize[0] == 0 || actualSize[1] == 0 || width == 0 || height == 0)
                throw new InvalidObjectException(null);
            int subSample = 1;
            if (actualSize[0] > width || actualSize[1] > height) {
                subSample = Math.round((float) actualSize[0] / (float) width);
                int i = Math.round((float) actualSize[1] / (float) height);
                if (subSample < i) {
                    subSample = i;
                }
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = subSample;
            inputStream = openInputStream(imagePath);
            return BitmapFactory.decodeStream(inputStream, null, options);


        } catch (FileNotFoundException | InvalidObjectException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public Bitmap getImageBitmap(Context context, Uri imageUri, int width, int height) {
        InputStream inputStream = null;
        try {
            int[] actualSize = getActualSize(context, imageUri);
            if (actualSize[0] == 0 || actualSize[1] == 0 || width == 0 || height == 0)
                throw new InvalidObjectException(null);
            int subSample = 1;
            if (actualSize[0] > width || actualSize[1] > height) {
                subSample = Math.round((float) actualSize[0] / (float) width);
                int i = Math.round((float) actualSize[1] / (float) height);
                if (subSample < i) {
                    subSample = i;
                }
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = subSample;
            inputStream = openInputStream(context, imageUri);
            return BitmapFactory.decodeStream(inputStream, null, options);


        } catch (FileNotFoundException | InvalidObjectException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public Bitmap getImageBitmap(Context context, FileInformation fileInformation, int w, int h) {
        Bitmap bitmap = null;
        if (fileInformation == null || !fileInformation.checkValidFileInformation()) return null;
        if (fileInformation.hasFilePath()) {
            bitmap = getImageBitmap(fileInformation.getFilePath(), w, h);
        }
        bitmap = getImageBitmap(context, fileInformation.getFileUri(), w, h);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), fileInformation.getOrientation(), true);
        return bitmap;

    }

    public Bitmap getVideoBitmap(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) return null;
        return ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MICRO_KIND);
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

    public String getExtenstion(String filePath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        if (extension.trim().isEmpty()) {
            extension = getFileExtensionFromUrl(filePath);
        }
        return extension;
    }

    public String getMimeType(String filePath) {
        String extension = getExtenstion(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public String saveBitmap(String filePath, Bitmap bitmap) {


        if (bitmap != null) {
            FileOutputStream out = null;
            try {
                String ext = getExtenstion(filePath);
                out = new FileOutputStream(filePath);
                if (ext.toLowerCase().contains("png")) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                }
                return filePath;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

}
