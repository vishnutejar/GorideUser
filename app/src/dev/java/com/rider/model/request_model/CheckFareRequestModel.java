package com.rider.model.request_model;

/**
 * Created by ubuntu on 11/4/18.
 */

public class CheckFareRequestModel extends AppBaseRequestModel {

    public double latitude_from;
    public double longitude_from;
    public String pickupaddress;
    public long cab_type_id;
    public long package_id;
    public long city_id;
    public String dropaddress;
    public double latitude_to;
    public double longitude_to;
    public long schedule_time;// optional
    public long utc_offset;// optional
    public String promocode;// optional

    public long trip_return_by_time;// use for outstation trip
    public String drop_country;// use for outstation trip
    public String drop_admin_level2;// use for outstation trip
    public String drop_admin_level1;// use for outstation trip
    public String drop_locality;// use for outstation trip
}
