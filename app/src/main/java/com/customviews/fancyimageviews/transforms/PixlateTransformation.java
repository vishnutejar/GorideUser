package com.customviews.fancyimageviews.transforms;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Manish Kumar
 * @since 11/7/17
 */


public class  PixlateTransformation extends ViewTransformation {

    float pixletWidth;
    float pixletHeight;
    float stepSize;


    int totalNoOffSteps;
    int totalWidthSteps;
    int totalHeightSteps;
    private List<Integer> randomSteps = new ArrayList<>();
    private List<Integer> mainRandomSteps = null;
    private boolean build = false;

    public PixlateTransformation () {
        op = Region.Op.DIFFERENCE;
    }

    public float getPixletWidth () {
        return pixletWidth;
    }

    public void setPixletWidth (float pixletWidth) {
        this.pixletWidth = pixletWidth;
    }

    public float getPixletHeight () {
        return pixletHeight;
    }

    public void setPixletHeight (float pixletHeight) {
        this.pixletHeight = pixletHeight;
    }

    public float getStepSize () {
        return stepSize;
    }

    public void setStepSize (float stepSize) {
        this.stepSize = stepSize;
    }

    public boolean isPixlateRemain () {
        return !(mainRandomSteps == null || mainRandomSteps.size() == 0);
    }

    @Override
    public boolean transform (Canvas canvas, View view) {
        if (build && (randomSteps == null || randomSteps.size() == 0))
            return false;

        path.reset();
        Path p = generatePixlatePath();
        if (p != null) {
            path.addPath(p);
            canvas.clipPath(path);
        }
        return false;
    }

    public Path generatePixlatePath () {
        build();

        if (mainRandomSteps.size() > 0) {
            Path p = new Path();
            randomSteps.add(mainRandomSteps.remove(0));
            for (Integer value : randomSteps) {
                int rowCount = (value / totalWidthSteps) + 1;
                int coloumCount = (value % totalWidthSteps) + 1;
                float left = stepSize * coloumCount - stepSize;
                float top = stepSize * rowCount - stepSize;
                float right = left + stepSize;
                float bottom = top + stepSize;
                p.addRect(left, top, right, bottom, Path.Direction.CW);
            }
            return p;
        }
        return null;


    }

    public void build () {
        if (build) return;
        build = true;
        this.totalWidthSteps = Math.round(pixletWidth / stepSize);
        this.totalHeightSteps = Math.round(pixletHeight / stepSize);
        this.totalNoOffSteps = totalWidthSteps * totalHeightSteps;

        printLog("totalNoOfSteps=" + totalNoOffSteps);
        mainRandomSteps = getRandomSubset(totalNoOffSteps);
        printLog("randomSteps=" + randomSteps.toString());

    }

    public List<Integer> getRandomSubset (int count) {

        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        return list.subList(0, count);
    }

    private void printLog (String msg) {
    }


}
