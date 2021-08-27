package com.quickzetuser.model.pusher;


import com.distancematrix.DistanceMatrixModel;
import com.google.android.gms.maps.model.LatLng;


public class PusherEtaModel {

    public double latitude;
    public double longitude;
    public float accuracy;
    public long user_id;
    public long booking_id;

    public DistanceMatrixModel eta;

    public LatLng getLatLng () {
        return new LatLng(latitude, longitude);
    }


}
