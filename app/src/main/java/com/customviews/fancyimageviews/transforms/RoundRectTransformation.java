package com.customviews.fancyimageviews.transforms;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

/**
 * @author Manish Kumar
 * @since 11/7/17
 */


public class RoundRectTransformation extends ViewTransformation {

    RectF rectF;

    float radius;
    float topLeftRadius;
    float topRightRadius;
    float bottomLeftRadius;
    float bottomRightRadius;
    float[] cornerRadius = new float[ 8 ];

    float strokeWidth;
    int strokeColor = -1;


    public float getRadius () {
        return radius;
    }

    public void setRadius (float radius) {
        this.radius = radius;
    }

    public RectF getRectF () {
        return rectF;
    }

    public void setRectF (RectF rectF) {
        this.rectF = rectF;
    }

    public float[] getCornerRadius () {
        return cornerRadius;
    }

    public void setCornerRadius (float[] cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public float getTopLeftRadius () {
        return topLeftRadius;
    }

    public void setTopLeftRadius (float topLeftRadius) {
        this.topLeftRadius = topLeftRadius;
    }

    public float getTopRightRadius () {
        return topRightRadius;
    }

    public void setTopRightRadius (float topRightRadius) {
        this.topRightRadius = topRightRadius;
    }

    public float getBottomLeftRadius () {
        return bottomLeftRadius;
    }

    public void setBottomLeftRadius (float bottomLeftRadius) {
        this.bottomLeftRadius = bottomLeftRadius;
    }


    public float getBottomRightRadius () {
        return bottomRightRadius;
    }

    public void setBottomRightRadius (float bottomRightRadius) {
        this.bottomRightRadius = bottomRightRadius;
    }

    public float getStrokeWidth () {
        return strokeWidth;
    }

    public void setStrokeWidth (float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getStrokeColor () {
        return strokeColor;
    }

    public void setStrokeColor (int strokeColor) {
        this.strokeColor = strokeColor;
    }

    @Override
    public boolean transform (Canvas canvas, View view) {
//        if (rectF == null) return false;
        if (this.radius != 0) {
            cornerRadius[ 0 ] = radius;
            cornerRadius[ 1 ] = radius;
            cornerRadius[ 2 ] = radius;
            cornerRadius[ 3 ] = radius;
            cornerRadius[ 4 ] = radius;
            cornerRadius[ 5 ] = radius;
            cornerRadius[ 6 ] = radius;
            cornerRadius[ 7 ] = radius;
        } else {
            cornerRadius[ 0 ] = topLeftRadius;
            cornerRadius[ 1 ] = topLeftRadius;
            cornerRadius[ 2 ] = topRightRadius;
            cornerRadius[ 3 ] = topRightRadius;
            cornerRadius[ 4 ] = bottomRightRadius;
            cornerRadius[ 5 ] = bottomRightRadius;
            cornerRadius[ 6 ] = bottomLeftRadius;
            cornerRadius[ 7 ] = bottomLeftRadius;
        }
        path.reset();
        path.addRoundRect(rectF, cornerRadius, Path.Direction.CW);
        canvas.clipPath(path, op);

        return false;
    }

    @Override
    public boolean afterSuperDraw (Canvas canvas, View view) {
        if (strokeColor != -1) {
            Path path = new Path();
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStrokeWidth(strokeWidth);
            paint.setColor(strokeColor);
            paint.setStyle(Paint.Style.STROKE);
            RectF rectF = new RectF(getRectF());
            rectF.inset(2, strokeWidth);
            path.addRoundRect(rectF, cornerRadius, Path.Direction.CW);
            canvas.drawPath(path, paint);
        }
        return false;
    }
}
