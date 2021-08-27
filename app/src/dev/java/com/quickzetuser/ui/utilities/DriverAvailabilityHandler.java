package com.quickzetuser.ui.utilities;

import android.content.Context;

import com.distancematrix.DistanceMatrixHandler;
import com.distancematrix.DistanceMatrixModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sunil kumar Yadav
 * @Since 13/6/18
 */
public class DriverAvailabilityHandler {
    DistanceMatrixHandler distanceMatrixHandler;
    Context context;
    DistanceMatrixHandler.DistanceMatrixListener distanceMatrixListener;

    private DriverAvailabilityHandler (Context context) {
        this.context = context;
        distanceMatrixHandler = new DistanceMatrixHandler(context);
    }

    public static DriverAvailabilityHandler getNewInstances (Context context) {
        return new DriverAvailabilityHandler(context);
    }

    public void setDistanceMatrixListener (DistanceMatrixHandler.DistanceMatrixListener distanceMatrixListener) {
        this.distanceMatrixListener = distanceMatrixListener;
    }

    public void getDriverAvailability (LatLng[] drivers, LatLng me) {
        if (me == null) {
            throw new IllegalArgumentException("me invalid.");
        }
        distanceMatrixHandler.cancelPreviousRequest();
        if (!sendLocalResponse(drivers, me, distanceMatrixListener)) {
            LatLng[] des = new LatLng[]{me};
            distanceMatrixHandler.getDistanceAndTime(drivers, des, distanceMatrixListener);
        }
    }

    private boolean sendLocalResponse (LatLng[] drivers, LatLng me,
                                       DistanceMatrixHandler.DistanceMatrixListener distanceMatrixListener) {
        for (LatLng driver : drivers) {
            if (driver != null) {
                return false;
            }
        }
        if (distanceMatrixListener != null) {
            distanceMatrixListener.onDistanceMatrixRequestStart();
        }
        List<DistanceMatrixModel> routes = new ArrayList<>();
        for (LatLng driver : drivers) {
            DistanceMatrixModel distanceMatrixModel = new DistanceMatrixModel();
            distanceMatrixModel.setLatLng(driver);
            distanceMatrixModel.setAddress("");

            DistanceMatrixModel.DestinationModel destinationModel = new DistanceMatrixModel.DestinationModel();
            destinationModel.setAddress("");
            destinationModel.setLatLng(me);
            destinationModel.setStatus("NOT_FOUND");
            distanceMatrixModel.addDestination(destinationModel);

            routes.add(distanceMatrixModel);
        }
        if (distanceMatrixListener != null) {
            distanceMatrixListener.onDistanceMatrixRequestEnd(routes);
        }


        return true;
    }


}
