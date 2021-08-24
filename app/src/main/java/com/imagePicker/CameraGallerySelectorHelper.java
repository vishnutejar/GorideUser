package com.imagePicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;

import com.utilities.AndroidFileProvider;

import java.io.File;


/**
 * @author Manish Kumar
 *         This class is used for Choose Picture and video from Gallery and Camera
 *         <p/>
 *         <p/>
 *         Code in on ACtivity result is
 *         <p/>
 *         <p/>
 *         if (resultCode == Activity.RESULT_OK) {
 *         <p/>
 *         if (requestCode == DialogChoosePicture.GALLERY) {
 *         if (data != null) {
 *         dialogChoosePicture.setImgUri(data.getData());
 *         }
 *         }
 *         int i = (int) (getResources().getDimension(R.dimen.imagesize) * getResources().getDisplayMetrics().density);
 *         iv_userpic.setScaleType(ImageView.ScaleType.CENTER_CROP);
 *         if (dialogChoosePicture != null) {
 *         try {
 *         int size = i + i;
 *         userpic = dialogChoosePicture.getBitmap(size, size,false);
 *         if (userpic != null) {
 *         Bitmap circleuserpic = PictureUtils.getCircleimgwithBorder(userpic, i, "#FFFFFF", 5);
 *         if (circleuserpic != null) {
 *         iv_userpic.setImageBitmap(circleuserpic);
 *         }
 *         }
 *         <p/>
 *         } catch (FileNotFoundException e) {
 *         // TODO Auto-generated catch block
 *         e.printStackTrace();
 *         restClient.showErrorDialog("File not found.");
 *         <p/>
 *         <p/>
 *         } catch (InvalidObjectException e) {
 *         // TODO Auto-generated catch block
 *         e.printStackTrace();
 *         restClient.showErrorDialog("Image dimension not found.");
 *         <p/>
 *         <p/>
 *         } catch (IOException e) {
 *         // TODO Auto-generated catch block
 *         e.printStackTrace();
 *         restClient.showErrorDialog("Something went wrong");
 *         <p/>
 *         <p/>
 *         } catch (Exception e) {
 *         e.printStackTrace();
 *         restClient.showErrorDialog("Something went wrong");
 *         <p/>
 *         <p/>
 *         }
 *         }
 *         <p/>
 *         } else {
 *         restClient.showErrorDialog("Something went wrong");
 *         }
 */
public class CameraGallerySelectorHelper {

    /**
     * Request code for open camera
     */
    public static final int CAMERA = 1222;
    /**
     * Request code for open Gallery
     */
    public static final int GALLERY = 1233;
    /**
     * use for  open this dialog for select image
     */
    public static int CHOOSE_IMAGE = 1;
    /**
     * use for open this dialog for select video
     */
    public static int CHOOSE_VIDEO = 2;


    /**
     * save extra data in this bundle
     */
    Bundle extraData;

    /**
     * use for open dialog type {@link #CHOOSE_IMAGE} or {@link #CHOOSE_VIDEO}
     */
    private int choose_type;

    /**
     * use for set uri of selected file
     */
    private Uri fileUri;


    /**
     * This is used for Image Type
     *
     * @result return on activity onActivityResult
     */

    public CameraGallerySelectorHelper() {

        this(CHOOSE_IMAGE);

    }

    /**
     * This is used for specific type
     * type=  CHOOSE_IMAGE or   CHOOSE_VIDEO
     *
     * @result return on activity onActivityResult
     */

    public CameraGallerySelectorHelper(int type) {
        this(type, null);
    }

    /**
     * This is used for  need result on Fragment   with specific type
     * type=  CHOOSE_IMAGE or   CHOOSE_VIDEO
     *
     * @param _context
     * @param type
     * @param imageName
     * @param fragment
     * @result return on Fragment onActivityResult
     */

    /**
     * @param type
     * @param extraData
     */
    public CameraGallerySelectorHelper(int type, Bundle extraData) {

        choose_type = type;
        this.extraData = extraData;
        fileUri = null;
    }


    /**
     * use for get {@link #extraData}
     *
     * @return
     */
    public Bundle getExtraData() {
        return extraData;
    }


    public void callCamera(Activity activity) {
        this.callCamera(activity, null);
    }

    public void callCamera(Fragment fragment) {
        this.callCamera(fragment.getActivity(), fragment);
    }

    public void callCamera(android.app.Fragment fragment) {
        this.callCamera(fragment.getActivity(), fragment);
    }

    public void callGallery(Activity activity) {
        this.callGallery(activity, null);
    }

    public void callGallery(Fragment fragment) {
        this.callGallery(fragment.getActivity(), fragment);
    }

    public void callGallery(android.app.Fragment fragment) {
        this.callGallery(fragment.getActivity(), fragment);
    }


    /**
     * use for open camera
     */
    private void callCamera(Activity activity, Object fragment) {

        final Intent intent = new Intent(choose_type == CHOOSE_IMAGE ?
                MediaStore.ACTION_IMAGE_CAPTURE : MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                choose_type == CHOOSE_IMAGE ? setImageUri(activity) : setVideoUri(activity));
        if (fragment != null) {

            if (fragment instanceof Fragment) {
                ((Fragment) fragment).startActivityForResult(intent, CAMERA);
            } else if (fragment instanceof android.app.Fragment) {
                ((android.app.Fragment) fragment).startActivityForResult(intent, CAMERA);
            }

        } else {
            activity.startActivityForResult(intent, CAMERA);
        }
    }

    /**
     * use for open gallery
     */
    private void callGallery(Activity activity, Object fragment) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intent.setType(choose_type == CHOOSE_IMAGE ? "image/*" : "video/*");

        Intent galleryIntent = Intent.createChooser(intent, choose_type == CHOOSE_IMAGE ?
                "Select Picture" : "Select Video");

        if (fragment != null) {
            if (fragment instanceof Fragment) {
                ((Fragment) fragment).startActivityForResult(galleryIntent, GALLERY);
            } else if (fragment instanceof android.app.Fragment) {
                ((android.app.Fragment) fragment).startActivityForResult(galleryIntent, GALLERY);
            }
        } else {
            activity.startActivityForResult(galleryIntent, GALLERY);
        }
    }

    /**
     * use for set image uri for camera
     *
     * @return
     */
    private Uri setImageUri(Activity activity) {

        File directory = createImageDirIfNotExist(activity);
        File file = new File(directory, "image_" + System.currentTimeMillis()
                + ".jpg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.fileUri = AndroidFileProvider.getUriForFile(activity,
                    activity.getPackageName(),
                    file);
        } else {
            this.fileUri = Uri.fromFile(file);
        }

        return this.fileUri;
    }

    /**
     * use for set video uri for camera
     *
     * @return
     */
    private Uri setVideoUri(Activity activity) {
        // Store image in dcim
        File directory = createVideoDirIfNotExist(activity);
        File file = new File(directory, "video_" + System.currentTimeMillis()
                + ".mp4");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.fileUri = AndroidFileProvider.getUriForFile(activity,
                    activity.getPackageName(),
                    file);
        } else {
            this.fileUri = Uri.fromFile(file);
        }

        return this.fileUri;
    }

    /**
     * use for create image dir if not exist
     *
     * @return
     */
    private File createImageDirIfNotExist(Activity activity) {
        File sdCard = activity
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (!sdCard.isDirectory()) {
            sdCard.mkdirs();
        }
        return sdCard;

    }

    /**
     * use for create mov dir if not exist
     *
     * @return
     */
    private File createVideoDirIfNotExist(Activity activity) {
        File sdCard = activity
                .getExternalFilesDir(Environment.DIRECTORY_MOVIES);

        if (!sdCard.isDirectory()) {
            sdCard.mkdirs();
        }
        return sdCard;

    }


    public FileInformation handleOnActivityResult(Context context,
                                                  int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                this.fileUri = data.getData();
            }
            return new FilePathHelper().getUriInformation(context, fileUri);

        }
        return null;
    }

}
