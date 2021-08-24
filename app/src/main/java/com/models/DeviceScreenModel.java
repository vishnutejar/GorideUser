package com.models;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by bitu on 17/8/17.
 */

public  class DeviceScreenModel {
    private static DeviceScreenModel deviceScreenModel;
    private Rect deviceRect;
    private float density;
    private float scaledDensity;
    private float xdpi;
    private int densityDpi;
    private DisplayMetrics displayMetrics;

    public DeviceScreenModel (DisplayMetrics displayMetrics) {
        this.displayMetrics=displayMetrics;
        this.deviceRect = new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        this.density = displayMetrics.density;
        this.xdpi = displayMetrics.xdpi;
        this.scaledDensity = displayMetrics.scaledDensity;
        this.densityDpi = displayMetrics.densityDpi;
    }

    public DisplayMetrics getDisplayMetrics() {
        return displayMetrics;
    }

    public static DeviceScreenModel getInstance () {
        if (deviceScreenModel == null) {
            deviceScreenModel = new DeviceScreenModel(Resources.getSystem().getDisplayMetrics());
        }
        return deviceScreenModel;
    }

    public Rect getDeviceRect() {
        return deviceRect;
    }

    public float getDensity() {
        return density;
    }

    public float getScaledDensity() {
        return scaledDensity;
    }

    public int getDensityDpi() {
        return densityDpi;
    }

    public int convertDpToPixel(float dp) {
        return Math.round(dp * ((float) densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int getNavigationViewWidth() {
        return (int) (deviceRect.width() * 0.70f);
    }

    public int getWidth(float value) {
        return (int) (deviceRect.width() * value);
    }

    public int getWidth(int width, float value) {
        return Math.round(((float) width) * value);
    }

    public int getHeight() {
        return (int) (deviceRect.height());
    }

    public int getHeight(float value) {
        return (int) (deviceRect.height() * value);
    }

    public int getHeight(int height, float value) {
        return Math.round(((float) height) * value);
    }

    public LinearLayout.LayoutParams getLinearLayoutParams(LinearLayout linearLayout, int width) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        return layoutParams;
    }

    public LinearLayout.LayoutParams getLinearLayoutParams(LinearLayout linearLayout,
                                                           int width, int left, int top,
                                                           int right, int bottom, float value) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = (int) (width * value);
        linearLayout.setPadding(left, top, right, bottom);
        return layoutParams;
    }

    public RelativeLayout.LayoutParams getRelativeLayoutParams(RelativeLayout linearLayout, int height) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height = height;
        return layoutParams;
    }
}
