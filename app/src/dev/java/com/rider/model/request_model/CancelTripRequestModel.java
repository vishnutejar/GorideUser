package com.rider.model.request_model;


/**
 * Created by ubuntu on 17/4/18.
 */

public class CancelTripRequestModel extends AppBaseRequestModel {

    public long booking_id;
    public double rider_lat;
    public double rider_lng;
    public String reason;

    }
