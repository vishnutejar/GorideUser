package com.customviews.fancyimageviews.transforms;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.view.View;

/**
 * @author Manish Kumar
 * @since 11/7/17
 */


public abstract class ViewTransformation {
    Path path = new Path();

    Region.Op op = Region.Op.INTERSECT;

    public abstract boolean transform (Canvas canvas, View view);

    public boolean afterSuperDraw (Canvas canvas, View view) {
        return false;
    }

}
