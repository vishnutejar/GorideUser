package com.permissions;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import com.quickzetuser.R;


/**
 * @author Manish Kumar
 * <p>
 * PermissionsHelper
 * <p>
 * <p>
 * This class is use for  handle run time permission in application
 * <p>
 *
 */
public class  PermissionHelperNew {

    /**
     * Request code for permissions
     */
    public static final int PermissionrequestCode = 1223;
    public static final String CALL_PERMISSION = Manifest.permission.CALL_PHONE;
    public static final int CALL_PERMISSION_REQUEST_CODE = 1224;
    public static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1225;
    public static final String STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 1226;
    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 1227;
    public static final String WIFI_PERMISSION = Manifest.permission.ACCESS_WIFI_STATE;
    public static final int WIFI_PERMISSION_REQUEST_CODE = 1228;
    public static final String SMS_PERMISSION = Manifest.permission.READ_SMS;
    public static final int SMS_PERMISSION_REQUEST_CODE = 1229;
    /**
     * preferences name where permission action is saved
     */
    private static final String Prefsname = "runtimepermission";
    /**
     * preference key for save permission action
     */
    private static final String KEY_PERMISSION_ACTION = "permission_action";
    /**
     * use for check user is go to setting screen
     */
    public static boolean requestingPermissionFromSetting = false;
    public static int requestingPermissionFromSettingCode = 0;
    static OnSpecificPermissionGranted onSpecificPermissionGranted;
    static AlertDialog alertDialog;

    /**
     * use for get {@link SharedPreferences} of  {@link #Prefsname}
     *
     * @param context
     * @return
     */
    public static SharedPreferences getPrefs(Context context) {
        if (context == null) return null;
        return context.getSharedPreferences(Prefsname,
                Context.MODE_PRIVATE);
    }

    /**
     * use for set value in {@link #Prefsname} in string format
     *
     * @param ctx
     * @param key
     * @param value
     */
    public static void setStringKeyvaluePrefs(Context ctx, String key,
                                              String value) {
        SharedPreferences prefs = getPrefs(ctx);
        if (prefs == null) return;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();

    }

    /**
     * use for get String value from {@link #Prefsname}
     *
     * @param ctx
     * @param key
     * @return
     */
    public static String getStringKeyvaluePrefs(Context ctx, String key) {
        SharedPreferences prefs = getPrefs(ctx);
        if (prefs == null) return "";
        return prefs.getString(key, "");
    }

    /**
     * use for check permission action is saved in {@link #Prefsname}
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermissionActionSaved(Context context, String permission) {
        String savedPermission = getStringKeyvaluePrefs(context, KEY_PERMISSION_ACTION);
        return savedPermission.contains(permission);
    }

    /**
     * use for save permission action in {@link #Prefsname}
     *
     * @param context
     * @param permission
     */
    public static void savePermissionAction(Context context, String permission) {
        if (!checkPermissionActionSaved(context, permission)) {
            String savedPermission = getStringKeyvaluePrefs(context, KEY_PERMISSION_ACTION);
            if (savedPermission.trim().isEmpty()) {
                savedPermission = permission;
            } else {
                savedPermission += "," + permission;
            }
            setStringKeyvaluePrefs(context, KEY_PERMISSION_ACTION, savedPermission);
        }
    }

    /**
     * use for handle specific permission request result
     *
     * @param context
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @return
     */
    public static void onSpecificRequestPermissionsResult(Activity context,
                                                          int requestCode, String[] permissions,
                                                          int[] grantResults) {
        boolean isGranted = false;
        boolean withNeverAsk = false;
        String permission;
        if (permissions != null && permissions.length > 0) {
            String permissionAction = permissions[0];
            permission = permissionAction;
            savePermissionAction(context, permissionAction);
            if (grantResults[0] == -1) {
                isGranted = false;
                withNeverAsk = isPermissionDenyWithNeverAsk(context, permissionAction);
            } else {
                isGranted = true;
            }
            if (onSpecificPermissionGranted != null) {
                onSpecificPermissionGranted.onPermissionGranted(isGranted, withNeverAsk,
                        permission, requestCode);
            }
        }
    }


    /**
     * use for specific permission not to granted
     * If permission not granted then request for that permission send from here
     *
     * @param context
     * @return
     */
    public static boolean needSpecificPermissions(Activity context, String permission,
                                                  int permissionRequestCode) {
        return needSpecificPermissions(context, permission, permissionRequestCode, null);

    }

    /**
     * use for specific permission not to granted
     * If permission not granted then request for that permission send from here
     *
     * @param context
     * @return
     */
    public static boolean needSpecificPermissions(Activity context, String permission,
                                                  int permissionRequestCode, Fragment fragment) {
        removePreviousAlert();
        if (areExplicitPermissionsRequired()) {

            boolean neverAsk = false;
            if (!isPermissionGranted(context, permission)) {
                if (checkPermissionActionSaved(context, permission)) {
                    if (isPermissionDenyWithNeverAsk(context, permission)) {
                        neverAsk = true;
                    }
                }
                if (!neverAsk) {
                    if (fragment != null) {
                        fragment.requestPermissions(new String[]{permission}, permissionRequestCode);
                    } else {
                        ActivityCompat.requestPermissions(scanForActivity(context),
                                new String[]{permission}, permissionRequestCode);
                    }

                    return true;
                }
            }

            if (neverAsk) {
                showNeverAskAlert(context, false, permissionRequestCode);
                return true;
            }
        }
        return false;

    }


    /**
     * use for show alert when any permission is deny by user
     *
     * @param context
     */
    public static void showSpecificDenyAlert(Activity context, String permission,
                                             int permissionCode, boolean closeApp) {
        showSpecificDenyAlert(context, permission, permissionCode, closeApp, null);
    }

    /**
     * use for show alert when any permission is deny by user
     *
     * @param context
     */
    public static void showSpecificDenyAlert(final Activity context, final String permission,
                                             final int permissionCode, final boolean closeApp, final Fragment fragment) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.app_name));

//        builder.setMessage(R.string.text_permission_deny_message);

        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                needSpecificPermissions(context, permission, permissionCode, fragment);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (closeApp)
                    context.finish();
            }
        });
        builder.setCancelable(false);
        removePreviousAlert();
        alertDialog = builder.create();
        alertDialog.show();
    }

    private static void removePreviousAlert() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    /**
     * use for show alert when permission are deny with never ask again
     *
     * @param context
     */
    public static void showNeverAskAlert(final Activity context, final boolean closeApp,
                                         final int permissionCode) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.app_name));

//        builder.setMessage(R.string.text_permission_never_message);

        builder.setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestingPermissionFromSetting = true;
                requestingPermissionFromSettingCode = permissionCode;
                goToAppSettingPage(context);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (closeApp)
                    context.finish();
            }
        });
        builder.setCancelable(false);
        removePreviousAlert();
        alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * use for goto Settings->Apps->ourApp
     *
     * @param activity
     */
    private static void goToAppSettingPage(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }

    /**
     * use for find activity instance from context
     *
     * @param cont
     * @return
     */
    private static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }

    /**
     * use for check permission is @{@link PackageManager#PERMISSION_GRANTED}
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean isPermissionGranted(Context context, String permission) {
        return PermissionChecker.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * use for check RunTime Permission is required or not
     *
     * @return
     */
    public static boolean areExplicitPermissionsRequired() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * use for check permission deny with never ask again
     *
     * @param activity
     * @param permission
     * @return
     */
    private static boolean isPermissionDenyWithNeverAsk(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !activity.shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }

    /**
     * call permission
     */
    public static boolean hasCallPermission(Activity context) {
        return isPermissionGranted(context, PermissionHelperNew.CALL_PERMISSION);
    }

    public static boolean needCallPermission(Activity activity,
                                             PermissionHelperNew.OnSpecificPermissionGranted listener) {
        onSpecificPermissionGranted = listener;
        return needSpecificPermissions(activity,
                PermissionHelperNew.CALL_PERMISSION,
                PermissionHelperNew.CALL_PERMISSION_REQUEST_CODE);
    }

    public static boolean needCallPermission(Fragment fragment,
                                             PermissionHelperNew.OnSpecificPermissionGranted listener) {
        if (fragment.getActivity() == null) return false;
        onSpecificPermissionGranted = listener;
        return needSpecificPermissions(fragment.getActivity(),
                PermissionHelperNew.CALL_PERMISSION,
                PermissionHelperNew.CALL_PERMISSION_REQUEST_CODE, fragment);
    }

    public static boolean hasLocationPermission(Context context) {
        return isPermissionGranted(context, PermissionHelperNew.LOCATION_PERMISSION);
    }

    public static boolean needLocationPermission(Activity activity,
                                                 PermissionHelperNew.OnSpecificPermissionGranted listener) {
        onSpecificPermissionGranted = listener;
        return needSpecificPermissions(activity,
                PermissionHelperNew.LOCATION_PERMISSION,
                PermissionHelperNew.LOCATION_PERMISSION_REQUEST_CODE);
    }

    /**
     * store permission
     */
    public static boolean hasStoragePermission(Activity context) {
        return isPermissionGranted(context, PermissionHelperNew.STORAGE_PERMISSION);
    }

    public static boolean needStoragePermission(Activity activity,
                                                PermissionHelperNew.OnSpecificPermissionGranted listener) {
        onSpecificPermissionGranted = listener;
        return needSpecificPermissions(activity,
                PermissionHelperNew.STORAGE_PERMISSION,
                PermissionHelperNew.STORAGE_PERMISSION_REQUEST_CODE);
    }

    public static boolean needStoragePermission(Fragment fragment,
                                                PermissionHelperNew.OnSpecificPermissionGranted listener) {
        if (fragment.getActivity() == null) return false;
        onSpecificPermissionGranted = listener;
        return needSpecificPermissions(fragment.getActivity(),
                PermissionHelperNew.STORAGE_PERMISSION,
                PermissionHelperNew.STORAGE_PERMISSION_REQUEST_CODE, fragment);
    }

    /**
     * camera permission
     */

    public static boolean hasCameraPermission(Activity context) {
        return isPermissionGranted(context, PermissionHelperNew.CAMERA_PERMISSION);
    }

    public static boolean needCameraPermission(Activity activity,
                                               PermissionHelperNew.OnSpecificPermissionGranted listener) {
        onSpecificPermissionGranted = listener;
        return needSpecificPermissions(activity,
                PermissionHelperNew.CAMERA_PERMISSION,
                PermissionHelperNew.CAMERA_PERMISSION_REQUEST_CODE);
    }

    public static boolean needCameraPermission(Fragment fragment,
                                               PermissionHelperNew.OnSpecificPermissionGranted listener) {
        if (fragment.getActivity() == null) return false;
        onSpecificPermissionGranted = listener;
        return needSpecificPermissions(fragment.getActivity(),
                PermissionHelperNew.CAMERA_PERMISSION,
                PermissionHelperNew.CAMERA_PERMISSION_REQUEST_CODE, fragment);
    }

    /**
     * internet
     */
    public static boolean hasWIFIPermission(Activity context) {
        return isPermissionGranted(context, PermissionHelperNew.WIFI_PERMISSION);
    }

    public static boolean needWIFIPermission(Activity activity,
                                             PermissionHelperNew.OnSpecificPermissionGranted listener) {
        onSpecificPermissionGranted = listener;
        return needSpecificPermissions(activity,
                PermissionHelperNew.WIFI_PERMISSION,
                PermissionHelperNew.WIFI_PERMISSION_REQUEST_CODE);
    }

    public static boolean needWIFIPermission(Fragment fragment,
                                             PermissionHelperNew.OnSpecificPermissionGranted listener) {
        if (fragment.getActivity() == null) return false;
        onSpecificPermissionGranted = listener;
        return needSpecificPermissions(fragment.getActivity(),
                PermissionHelperNew.WIFI_PERMISSION,
                PermissionHelperNew.WIFI_PERMISSION_REQUEST_CODE, fragment);
    }

    public static boolean hasSMSPermission(Context context) {
        return isPermissionGranted(context, PermissionHelperNew.SMS_PERMISSION);
    }

    public static boolean needSMSPermission(Activity activity,
                                            PermissionHelperNew.OnSpecificPermissionGranted listener) {
        onSpecificPermissionGranted = listener;
//        return needSpecificPermissions(activity,
//                PermissionHelperNew.SMS_PERMISSION,
//                PermissionHelperNew.SMS_PERMISSION_REQUEST_CODE);
        return false;
    }

    public interface OnSpecificPermissionGranted {
        void onPermissionGranted(boolean isGranted, boolean withNeverAsk,
                                 String permission, int requestCode);
    }


}
