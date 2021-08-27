package com.utilities;

import android.animation.ObjectAnimator;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;


/**
 * @author Manish Kumar
 * @since 29/8/18
 */

public class ViewAnimationUtils {

    public static void expand (final View v, final InterPolatedTimeListener interPolatedTimeListener) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofInt(v, new HeightProperty(), 0, targtetHeight);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();

//        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, targtetHeight);
//        valueAnimator.setDuration(200);
//        valueAnimator.setInterpolator(new DecelerateInterpolator());
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate (ValueAnimator animation) {
//                float animatedFraction = animation.getAnimatedFraction();
//                if (interPolatedTimeListener != null) {
//                    interPolatedTimeListener.applyTransformation(animatedFraction);
//                }
//                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
//                layoutParams.height = (int) animation.getAnimatedValue();
//                v.setLayoutParams(layoutParams);
//            }
//        });
//        valueAnimator.start();
    }

    public static void collapse (final View v, final InterPolatedTimeListener interPolatedTimeListener) {
        final int initialHeight = v.getMeasuredHeight();

        ObjectAnimator animator = ObjectAnimator.ofInt(v, new HeightProperty(), initialHeight, 0);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();

//        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialHeight, 0);
//        valueAnimator.setDuration(200);
//        valueAnimator.setInterpolator(new DecelerateInterpolator());
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate (ValueAnimator animation) {
//                float animatedFraction = animation.getAnimatedFraction();
//                if (interPolatedTimeListener != null) {
//                    interPolatedTimeListener.applyTransformation(animatedFraction);
//                }
//                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
//                layoutParams.height = (int) animation.getAnimatedValue();
//                v.setLayoutParams(layoutParams);
//            }
//        });
//        valueAnimator.start();
    }

    public interface InterPolatedTimeListener {
        void applyTransformation (float interpolatedTime);
    }

    static class HeightProperty extends Property<View, Integer> {

        public HeightProperty () {
            super(Integer.class, "height");
        }

        @Override
        public Integer get (View view) {
            return view.getHeight();
        }

        @Override
        public void set (View view, Integer value) {
            view.getLayoutParams().height = value;
            view.setLayoutParams(view.getLayoutParams());
        }
    }

}
