package com.customviews.fancyimageviews;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.customviews.fancyimageviews.transforms.PixlateTransformation;


/**
 * @author Manish Kumar
 * @since 11/7/17
 */


public class PixlateImageView extends ImageView {

    PixlateTransformation pixlateTransformation;

    Handler handler = new Handler() {

        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            PixlateImageView.this.invalidate();
            nextInvalidateView();
        }
    };

    public PixlateImageView (Context context) {
        super(context);
        inIt(context, null);
    }

    public void nextInvalidateView () {
        if (pixlateTransformation != null && pixlateTransformation.isPixlateRemain()) {
            handler.sendEmptyMessageDelayed(1, 16);
        }
    }

    @Override
    protected void onDetachedFromWindow () {
        super.onDetachedFromWindow();
        handler.removeMessages(1);
    }

    public PixlateImageView (Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inIt(context, attrs);
    }

    public PixlateImageView (Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inIt(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PixlateImageView (Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inIt(context, attrs);
    }

    private void inIt (Context context, AttributeSet attrs) {

//        if (attrs != null) {
//            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.fiv);
//            int transformationEnumType = a.getInt(R.styleable.fiv_transformation_type, -1);
//            if (transformationEnumType == FancyImageVIew.TransformationType.CIRCULAR.getTransformationType()) {
//                transformationType = FancyImageVIew.TransformationType.CIRCULAR;
//            } else if (transformationEnumType == FancyImageVIew.TransformationType.TRIANGLE.getTransformationType()) {
//                transformationType = FancyImageVIew.TransformationType.TRIANGLE;
//            }
//            if (transformationType != null) {
//                centerX = a.getFloat(R.styleable.fiv_centerX, -1);
//                centerY = a.getFloat(R.styleable.fiv_centerY, -1);
//                if (transformationType == FancyImageVIew.TransformationType.CIRCULAR) {
//                    radius = a.getDimension(R.styleable.fiv_circular_radius, -1);
//                } else if (transformationType == FancyImageVIew.TransformationType.TRIANGLE) {
//                    radius = a.getDimension(R.styleable.fiv_triangle_inner_radius, -1);
//                    outer_radius = a.getDimension(R.styleable.fiv_triangle_outer_radius, -1);
//                    start_angle_degree = a.getFloat(R.styleable.fiv_triangle_start_angle_degree, -1);
//                    steps = a.getInt(R.styleable.fiv_triangle_steps, -1);
//                }
//            }
//
//        }
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float viewW = w;
        float viewH = h;
        float stepSize = w * 0.06f;
        pixlateTransformation = new PixlateTransformation();
        pixlateTransformation.setPixletWidth(viewW);
        pixlateTransformation.setPixletHeight(viewH);
        pixlateTransformation.setStepSize(stepSize);
    }

    @Override
    protected void onDraw (Canvas canvas) {
        if (pixlateTransformation != null) {

            pixlateTransformation.transform(canvas, this);

        }
        super.onDraw(canvas);
        nextInvalidateView();
    }

    private void printLog (String msg) {

    }
}
