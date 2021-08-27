package com.utilities.marker;

import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


public class MarkerAnimation {
    Handler movinghandler = new Handler();
    LatLngInterpolator latLngInterpolator;
    LatLng startPosition;
    LatLng finalPosition;
    double OldRotationAngle;
    double newRotationAngle;
    boolean isClockWise;
    long start;
    Marker marker;

    Runnable runnable = new Runnable() {
        long elapsed;
        float t;
        float v;

        @Override
        public void run() {
            // Calculate progress using interpolator
            final Interpolator interpolator = new LinearInterpolator();
            final float durationInMs = 2000;
            elapsed = SystemClock.uptimeMillis() - start;
            t = elapsed / durationInMs;
            v = interpolator.getInterpolation(t);
            LatLng lastLocaton = marker.getPosition();
            Location preLoc = new Location("preLoc");
            preLoc.setLatitude(lastLocaton.latitude);
            preLoc.setLongitude(lastLocaton.longitude);

            LatLng newLo = latLngInterpolator.interpolate(v,
                    startPosition, finalPosition);
            Location newloc = new Location("fnewlocation");
            newloc.setLatitude(newLo.latitude);
            newloc.setLongitude(newLo.longitude);
            if (newRotationAngle != 0) {
                if (t < 1) {
                    if (isClockWise) {
                        marker.setRotation((float) (OldRotationAngle + (newRotationAngle * t)));
                    } else {
                        marker.setRotation((float) (OldRotationAngle - (newRotationAngle * t)));
                    }
                }
            }
            marker.setPosition(newLo);

            if (t < 1) {
                movinghandler.postDelayed(this, 16);
            }
        }
    };

    public MarkerAnimation(Marker marker) {
        this.marker = marker;
    }

    public void animateMarkerToGB(LatLng newfinalPosition) {

        movinghandler.removeCallbacks(runnable);
        if (latLngInterpolator == null) {
            latLngInterpolator = new LatLngInterpolator.Spherical();
        }
        startPosition = marker.getPosition();
        finalPosition = newfinalPosition;
        double newFinalAngle = latLngInterpolator.computeHeading(startPosition, finalPosition);
        double oldFinalAngle = marker.getRotation();
        this.OldRotationAngle = oldFinalAngle;
        double newFinalAngleAbs = Math.abs(newFinalAngle);
        double oldFinalAngleAbs = Math.abs(oldFinalAngle);
        if ((oldFinalAngle < 0 && newFinalAngle > 0)
                || (oldFinalAngle > 0 && newFinalAngle < 0)) {
            this.newRotationAngle = newFinalAngleAbs + oldFinalAngleAbs;
        } else {
            if (newFinalAngleAbs > oldFinalAngleAbs) {
                this.newRotationAngle = newFinalAngleAbs - oldFinalAngleAbs;
            } else {
                this.newRotationAngle = oldFinalAngleAbs - newFinalAngleAbs;
            }
        }
        if (newFinalAngle < marker.getRotation()) {
            isClockWise = false;
        } else {
            isClockWise = true;
        }
        start = SystemClock.uptimeMillis();
        movinghandler.post(runnable);
    }


}
