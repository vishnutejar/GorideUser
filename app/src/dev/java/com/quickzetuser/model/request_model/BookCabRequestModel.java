package com.quickzetuser.model.request_model;


import com.medy.retrofitwrapper.ParamJSON;
import com.quickzetuser.model.CityModel;
import com.quickzetuser.model.CountryModel;
import com.quickzetuser.model.FareModel;
import com.quickzetuser.model.PromoCodeModel;
import com.quickzetuser.model.StateModel;
import com.quickzetuser.model.TaxModel;

import java.util.List;

/**
 * Created by ubuntu on 17/4/18.
 */

public class BookCabRequestModel extends AppBaseRequestModel {

    public String phone;
    public String mobile_code;
    public String booking_running_type;
    public String outstation_trip_type;
    public String pickupaddress;
    public String dropaddress;
    public String paymentmethod;
    public String special_instruction;

    public long taxi_id;
    public long booking_cancel_time_duration;
    public long booking_driver_cancel_time_duration;
    public long booking_free_waiting_time_duration;
    public long utc_offset;
    public long cab_type_id;
    public long sheduled_date;

    public int seat_count;


    public float toll_area_distance;
    public double latitude_from;
    public double longitude_from;
    public double latitude_to;
    public double longitude_to;

    @ParamJSON
    public CountryModel country;
    @ParamJSON
    public StateModel state;
    @ParamJSON
    public CityModel city;
    @ParamJSON
    public PromoCodeModel promocode;
    @ParamJSON
    public List<TaxModel> taxes;
    @ParamJSON
    public FareModel fare;


    public String package_id;
    public String package_name;
    public String max_duration;
    public String max_distance;
}
