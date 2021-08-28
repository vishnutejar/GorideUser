package com.customviews.fancyimageviews.transforms;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * @author Manish Kumar
 * @since 11/7/17
 */


public class  CircularTransformation extends ViewTransformation {

    float centerX;
    float centerY;
    float radius;
    int img_bg_color = -14568;
    int strokeColor = -14568;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }


    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }


    public void setStrokeWidth(float strokeWidth) {
        paint.setStrokeWidth(strokeWidth);
    }

    public void setImg_bg_color(int img_bg_color) {
        this.img_bg_color = img_bg_color;
    }

    @Override
    public boolean transform(Canvas canvas, View view) {

        path.reset();
        path.addCircle(centerX, centerY, radius,
                Path.Direction.CW);
       // path.close();
        canvas.clipPath(path, op);
        if (img_bg_color != -14568) {
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setColor(img_bg_color);
            canvas.drawCircle(centerX, centerY, radius, paint);
        }
        return false;
    }

    @Override
    public boolean afterSuperDraw(Canvas canvas, View view) {
        if (strokeColor != -14568) {
            paint.setColor(strokeColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setAntiAlias(true);
            paint.setDither(true);
            canvas.drawCircle(centerX, centerY, radius - (paint.getStrokeWidth() * 0.5f), paint);
        }
        return false;
    }
}
