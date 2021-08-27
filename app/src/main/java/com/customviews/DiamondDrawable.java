package com.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import com.quickzetuser.R;


/**
 * Created by ubuntu on 3/12/16.
 */


public class DiamondDrawable extends Drawable {

    Paint mPaint;
    Rect rectBound;
    Context context;


    public DiamondDrawable (Context context) {
        this.context = context;
        inIt();
    }

    private int dpToPx (float dp) {
        DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private void inIt () {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        try {
            TypedArray typedArray = context.obtainStyledAttributes(R.styleable.AppTheme);
            int button_bg_color = typedArray.getColor(R.styleable.AppTheme_btn_bg_color, 0xff000000);
            mPaint.setColor(button_bg_color);
            typedArray.recycle();
        } catch (Resources.NotFoundException ignore) {

        }

    }

    @Override
    public void draw (Canvas canvas) {
        Path p = generateDiamondPath();
        canvas.drawPath(p, mPaint);
    }

    @Override
    public void setAlpha (int alpha) {
        if (mPaint != null) {
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter (ColorFilter colorFilter) {
        if (mPaint != null) {
            mPaint.setColorFilter(colorFilter);
            invalidateSelf();
        }
    }

    @Override
    public int getOpacity () {
        return PixelFormat.UNKNOWN;
    }

    @Override
    protected void onBoundsChange (Rect bounds) {
        rectBound = new Rect(bounds);
        super.onBoundsChange(rectBound);
    }

    @Override
    public boolean getPadding (Rect padding) {
        // TODO Auto-generated method stub
        return true;
    }

    private Path generateDiamondPath () {
        RectF rectF = new RectF(rectBound);
        Path p = new Path();
        p.moveTo(rectF.centerX(), 0);
        p.lineTo(rectF.width(), rectF.centerY());
        p.lineTo(rectF.centerX(), rectF.height());
        p.lineTo(0, rectF.centerY());
        p.close();
        return p;
    }


}
