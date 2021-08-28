package com.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

import androidx.viewpager.widget.ViewPager;

public class   WrappingViewPager extends ViewPager implements Animation.AnimationListener {

    private View mCurrentView;
    private PagerAnimation mAnimation = new PagerAnimation();
    private boolean mAnimStarted = false;
    private long mAnimDuration = 1;


    public WrappingViewPager(Context context) {
        super(context);
        mAnimation.setAnimationListener(this);
    }

    public WrappingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAnimation.setAnimationListener(this);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!mAnimStarted && mCurrentView != null) {
            int height;
            mCurrentView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            height = mCurrentView.getMeasuredHeight();

            if (height < getMinimumHeight()) {
                height = getMinimumHeight();
            }

            int newHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            if (getLayoutParams().height != 0 && heightMeasureSpec != newHeight) {
                mAnimation.setDimensions(height, getLayoutParams().height);
                mAnimation.setDuration(mAnimDuration);
                startAnimation(mAnimation);
                mAnimStarted = true;
            } else {
                heightMeasureSpec = newHeight;
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void onPageChanged(View currentView) {
        mCurrentView = currentView;
        requestLayout();
    }


    private class PagerAnimation extends Animation {
        private int targetHeight;
        private int currentHeight;
        private int heightChange;

        void setDimensions(int targetHeight, int currentHeight) {
            this.targetHeight = targetHeight;
            this.currentHeight = currentHeight;
            this.heightChange = targetHeight - currentHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (interpolatedTime >= 1) {
                getLayoutParams().height = targetHeight;
            } else {
                int stepHeight = (int) (heightChange * interpolatedTime);
                getLayoutParams().height = currentHeight + stepHeight;
            }
            requestLayout();
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    public void setAnimationDuration(long duration) {
        mAnimDuration = duration;
    }

    public void setAnimationInterpolator(Interpolator interpolator) {
        mAnimation.setInterpolator(interpolator);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        mAnimStarted = true;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mAnimStarted = false;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
