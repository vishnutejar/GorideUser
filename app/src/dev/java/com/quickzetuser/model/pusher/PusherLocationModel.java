package com.quickzetuser.model.pusher;



import com.google.android.gms.maps.model.LatLng;

public class PusherLocationModel {

    public double latitude;
    public double longitude;
    public float accuracy;
    public long user_id;

    public LatLng getLatLng () {
        return new LatLng(latitude, longitude);
    }


}
