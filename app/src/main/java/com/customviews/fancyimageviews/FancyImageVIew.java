package com.customviews.fancyimageviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.customviews.fancyimageviews.transforms.CircularTransformation;
import com.customviews.fancyimageviews.transforms.RoundRectTransformation;
import com.customviews.fancyimageviews.transforms.TrinagleTransformation;
import com.customviews.fancyimageviews.transforms.ViewTransformation;
import com.quickzetuser.R;


/**
 * @author Manish Kumar
 * @since 11/7/17
 */


@SuppressLint("AppCompatCustomView")
public class FancyImageVIew extends ImageView {

    public static final TransformationType DEFAULT_TRANSFORMATION_TYPE = TransformationType.CIRCULAR;

    ViewTransformation viewTransformation;

    TransformationType transformationType;

    float centerX;
    float centerY;
    float radius;
    float outer_radius;
    float start_angle_degree;
    int steps;

    float cornerRadius;
    float topLeftRadius;
    float topRightRadius;
    float bottomLeftRadius;
    float bottomRightRadius;
    int strokeColor;
    float strokeWidth;
    int imgBgColor;

    RectF viewRect;


    public FancyImageVIew(Context context) {
        super(context);
        inIt(context, null);
    }

    public FancyImageVIew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inIt(context, attrs);
    }

    public FancyImageVIew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inIt(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FancyImageVIew(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inIt(context, attrs);
    }

    private void inIt(Context context, AttributeSet attrs) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(LAYER_TYPE_HARDWARE, null);
        } else {
            setLayerType(LAYER_TYPE_SOFTWARE, null);

        }
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.fiv);
            int transformationEnumType = a.getInt(R.styleable.fiv_transformation_type, -1);
            if (transformationEnumType == TransformationType.CIRCULAR.getTransformationType()) {
                transformationType = TransformationType.CIRCULAR;
            } else if (transformationEnumType == TransformationType.TRIANGLE.getTransformationType()) {
                transformationType = TransformationType.TRIANGLE;
            } else if (transformationEnumType == TransformationType.ROUND_RECT.getTransformationType()) {
                transformationType = TransformationType.ROUND_RECT;
            }
            if (transformationType != null) {

                if (transformationType == TransformationType.CIRCULAR) {
                    centerX = a.getFloat(R.styleable.fiv_centerX, -1);
                    centerY = a.getFloat(R.styleable.fiv_centerY, -1);
                    radius = a.getDimension(R.styleable.fiv_circular_radius, -1);
                    strokeColor = a.getColor(R.styleable.fiv_rect_strokeColor, -1);
                    imgBgColor = a.getColor(R.styleable.fiv_img_bg_color, -1);
                    strokeWidth = a.getDimension(R.styleable.fiv_rect_strokeWidth, 0);
                } else if (transformationType == TransformationType.TRIANGLE) {
                    centerX = a.getFloat(R.styleable.fiv_centerX, -1);
                    centerY = a.getFloat(R.styleable.fiv_centerY, -1);
                    radius = a.getDimension(R.styleable.fiv_triangle_inner_radius, -1);
                    outer_radius = a.getDimension(R.styleable.fiv_triangle_outer_radius, -1);
                    start_angle_degree = a.getFloat(R.styleable.fiv_triangle_start_angle_degree, -1);
                    steps = a.getInt(R.styleable.fiv_triangle_steps, -1);
                } else if (transformationType == TransformationType.ROUND_RECT) {

                    cornerRadius = a.getDimension(R.styleable.fiv_corner_radius, 0);
                    topLeftRadius = a.getDimension(R.styleable.fiv_topLeftRadius, 0);
                    topRightRadius = a.getDimension(R.styleable.fiv_topRightRadius, 0);
                    bottomLeftRadius = a.getDimension(R.styleable.fiv_bottomLeftRadius, 0);
                    bottomRightRadius = a.getDimension(R.styleable.fiv_bottomRightRadius, 0);
                    strokeColor = a.getColor(R.styleable.fiv_rect_strokeColor, -1);
                    strokeWidth = a.getDimension(R.styleable.fiv_rect_strokeWidth, 0);
                }
            }

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int save = canvas.save();
        if (viewTransformation != null) {

            viewTransformation.transform(canvas, this);

        }
        super.onDraw(canvas);
        if (viewTransformation != null) {

            viewTransformation.afterSuperDraw(canvas, this);

        }
        canvas.restoreToCount(save);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewRect = new RectF(0, 0, w, h);
        if (transformationType != null) {
            float centerX = this.centerX == -1 ? w * 0.5f : w * this.centerX;
            float centerY = this.centerY == -1 ? h * 0.5f : h * this.centerY;

            if (transformationType == TransformationType.CIRCULAR) {
                float radius = this.radius == -1 ? Math.min(w, h) * 0.5f : this.radius;
                CircularTransformation circularTransformation = new CircularTransformation();
                circularTransformation.setCenterX(centerX);
                circularTransformation.setCenterY(centerY);
                circularTransformation.setRadius(radius);
                circularTransformation.setStrokeWidth(strokeWidth);
                circularTransformation.setStrokeColor(strokeColor);
                circularTransformation.setImg_bg_color(imgBgColor);
                this.viewTransformation = circularTransformation;
            } else if (transformationType == TransformationType.TRIANGLE) {
                float outerRadius = this.outer_radius == -1 ? Math.min(w, h) * 0.5f : this.outer_radius;
                float radius = this.radius == -1 ? outerRadius * 0.5f : this.radius;
                int steps = this.steps == -1 ? 5 : this.steps;
                float startAngle = this.start_angle_degree == -1 ? -90 : this.start_angle_degree;
                TrinagleTransformation trinagleTransformation = new TrinagleTransformation();
                trinagleTransformation.setCenterX(centerX);
                trinagleTransformation.setCenterY(centerY);
                trinagleTransformation.setRadius(radius);
                trinagleTransformation.setOuterRadius(outerRadius);
                trinagleTransformation.setSteps(steps);
                trinagleTransformation.setStartAngleDegree(startAngle);
                this.viewTransformation = trinagleTransformation;
            } else if (transformationType == TransformationType.ROUND_RECT) {

                RoundRectTransformation roundRectTransformation = new RoundRectTransformation();
                roundRectTransformation.setRectF(viewRect);
                roundRectTransformation.setRadius(cornerRadius);
                roundRectTransformation.setTopLeftRadius(topLeftRadius);
                roundRectTransformation.setTopRightRadius(topRightRadius);
                roundRectTransformation.setBottomLeftRadius(bottomLeftRadius);
                roundRectTransformation.setBottomRightRadius(bottomRightRadius);
                roundRectTransformation.setStrokeWidth(strokeWidth);
                roundRectTransformation.setStrokeColor(strokeColor);
                this.viewTransformation = roundRectTransformation;
            }
        }

    }

    public void setTransformationType(TransformationType transformationType) {
        this.transformationType = transformationType;
    }

    public TransformationType getTransformationType() {
        return transformationType;
    }

    public enum TransformationType {
        CIRCULAR(0),

        TRIANGLE(1),

        ROUND_RECT(2);

        final int transformationType;

        TransformationType(int transformationType) {
            this.transformationType = transformationType;
        }

        public int getTransformationType() {
            return transformationType;
        }
    }


}
