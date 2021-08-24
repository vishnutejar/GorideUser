package com.customviews.fancyimageviews.transforms;

import android.graphics.Canvas;
import android.graphics.Path;
import android.view.View;

/**
 * @author Manish Kumar
 * @since 11/7/17
 */


public class TrinagleTransformation extends ViewTransformation {

    float centerX;
    float centerY;
    float radius;
    float outerRadius;
    int steps;
    float startAngleDegree;

    public float getCenterX () {
        return centerX;
    }

    public void setCenterX (float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY () {
        return centerY;
    }

    public void setCenterY (float centerY) {
        this.centerY = centerY;
    }

    public float getRadius () {
        return radius;
    }

    public void setRadius (float radius) {
        this.radius = radius;
    }

    public float getOuterRadius () {
        return outerRadius;
    }

    public void setOuterRadius (float outerRadius) {
        this.outerRadius = outerRadius;
    }

    public int getSteps () {
        return steps;
    }

    public void setSteps (int steps) {
        this.steps = steps;
    }

    public float getStartAngleDegree () {
        return startAngleDegree;
    }

    public void setStartAngleDegree (float startAngleDegree) {
        this.startAngleDegree = startAngleDegree;
    }

    @Override
    public boolean transform (Canvas canvas, View view) {

        path.reset();
        path.addPath(createStarBySyze(centerX, centerY, outerRadius, radius, startAngleDegree, steps));
        canvas.clipPath(path, op);
        return false;
    }

    private Path createStarBySyze (float centextX, float centerY, float outerRadius,
                                   float innerradius, float startAngleDegree, int steps) {

        float startAngle = (float) Math.toRadians(startAngleDegree);
        float degreesPerStep = (float) Math.toRadians(360.0F / (float) steps);
        float halfDegreesPerStep = degreesPerStep * 0.5f;
        Path ret = new Path();
        float max = (float) (2.0F * Math.PI) - startAngle;


        ret.moveTo(centextX, centerY - outerRadius);
        for (double step = startAngle; step < max; step += degreesPerStep) {

            float x = (float) (centextX + outerRadius * Math.cos(step));
            float y = (float) (centerY + outerRadius * Math.sin(step));
            ret.lineTo(x, y);


            x = (float) (centextX + innerradius * Math.cos(step + halfDegreesPerStep));
            y = (float) (centerY + innerradius * Math.sin(step + halfDegreesPerStep));
            ret.lineTo(x, y);


        }
        ret.close();

        return ret;
    }
}
