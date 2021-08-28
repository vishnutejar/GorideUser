package com.customviews.fancyimageviews;


import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.quickzetuser.R;


/**
 * @author Manish Kumar
 * @since 16/8/17
 */


public class  AnimatedImageView extends ImageView {

    Animation clickAnimation;


    public AnimatedImageView(Context context) {
        super(context);
    }

    public AnimatedImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public AnimatedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AnimatedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void inIt(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            if (isClickable()) {
                clickAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.button_click_anim);
            }
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
        clickAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.button_click_anim);

    }

    @Override
    public boolean performClick() {
        playClickFun();
        return super.performClick();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (clickAnimation != null) {
            clickAnimation.cancel();
        }
    }

    private void playClickFun() {
        if (clickAnimation != null) {
            clickAnimation.cancel();
            this.setAnimation(null);
            this.startAnimation(clickAnimation);
        }
    }

}
