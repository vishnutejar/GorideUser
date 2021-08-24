package com.rider.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.models.BaseModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ubuntu on 18/4/18.
 */

public class BookCabModel extends BaseModel {

    private UserModel rider;
    private long booking_id;
    private long booking_date;
    private long return_by_time;
    private String pickup_address;
    private String drop_address;
    private String travel_route_map;
    private float total_distance;
    private float total_distance_round_trip;
    private String start_address;
    private String booking_user_type;
    private String booking_type;
    private String booking_running_type;
    private String outstation_trip_type;
    private String special_instruction;
    private String booking_cancelled_by;
    private String cancellation_reason;
    private float toll_area_distance;
    private long total_travel_time;
    private long total_waiting_time;
    private long total_travel_time_roundtrip;
    private float total_distance_extra;
    private long total_travel_time_extra;
    private String seat_no;
    private int seat_no_count;
    private double latitude_from;
    private double longitude_from;
    private double latitude_to;
    private double longitude_to;
    private double start_lat;
    private double end_lng;
    private double end_lat;
    private String end_address;
    private double cancellation_driver_lat;
    private double cancellation_driver_lng;
    private double start_lng;
    private long booking_start_time;
    private long booking_accept_time;
    private long booking_cancel_time_duration;
    private long booking_free_waiting_time_duration;
    private long booking_driver_cancel_time_duration;
    private long utc_offset;
    private long booking_end_time;
    private long taxi_id;
    private PromoCodeModel promocode;
    private CityModel city;
    private StateModel state;
    private CountryModel country;
    private int status;
    private String paymentmethod;
    private VehicleTypeModel vehicle;
    private FareModel fare;
    private String otp;
    private TotalTaxModel total_tax;
    private TaxiModel taxi;
    private DriverModel driver;
    private TotalTollsModel total_tolls;

    private List<RentalPackageModel> packages;
    private OutstationPackageModel outstation;


    private float trip_commission;
    private float driver_commission;
    private float admin_hand;
    private float driver_hand;
    private float payout_amount;

    private String schedule_success_msg;


    private long package_id;
    private String package_name;
    private long max_duration;
    private float max_distance;


    public long getPackage_id() {
        return package_id;
    }

    public void setPackage_id(long package_id) {
        this.package_id = package_id;
    }

    public String getPackage_name() {
        return getValidString(package_name);
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public long getMax_duration() {
        return max_duration;
    }

    public void setMax_duration(long max_duration) {
        this.max_duration = max_duration;
    }

    public float getMax_distance() {
        return max_distance;
    }

    public void setMax_distance(float max_distance) {
        this.max_distance = max_distance;
    }

    public String getEnd_address() {
        return end_address;
    }

    public void setEnd_address (String end_address) {
        this.end_address = end_address;
    }

    public float getToll_area_distance () {
        return toll_area_distance;
    }

    public void setToll_area_distance (float toll_area_distance) {
        this.toll_area_distance = toll_area_distance;
    }

    public long getTotal_travel_time () {
        return total_travel_time;
    }

    public void setTotal_travel_time (long total_travel_time) {
        this.total_travel_time = total_travel_time;
    }

    public long getTotal_waiting_time() {
        return total_waiting_time;
    }

    public void setTotal_waiting_time(long total_waiting_time) {
        this.total_waiting_time = total_waiting_time;
    }

    public long getTotal_travel_time_extra() {
        return total_travel_time_extra;
    }

    public void setTotal_travel_time_extra(long total_travel_time_extra) {
        this.total_travel_time_extra = total_travel_time_extra;
    }

    public long getBooking_start_time() {
        return booking_start_time;
    }

    public void setBooking_start_time (long booking_start_time) {
        this.booking_start_time = booking_start_time;
    }

    public long getBooking_accept_time () {
        return booking_accept_time;
    }

    public void setBooking_accept_time (long booking_accept_time) {
        this.booking_accept_time = booking_accept_time;
    }

    public String getStart_address () {
        return start_address;
    }

    public void setStart_address (String start_address) {
        this.start_address = start_address;
    }

    public double getStart_lat () {
        return start_lat;
    }

    public void setStart_lat (double start_lat) {
        this.start_lat = start_lat;
    }

    public double getStart_lng () {
        return start_lng;
    }

    public void setStart_lng (double start_lng) {
        this.start_lng = start_lng;
    }

    public UserModel getRider () {
        return rider;
    }

    public void setRider (UserModel rider) {
        this.rider = rider;
    }

    public long getBooking_id () {
        return booking_id;
    }

    public void setBooking_id (long booking_id) {
        this.booking_id = booking_id;
    }

    public long getBooking_date () {
        return booking_date;
    }

    public void setBooking_date (long booking_date) {
        this.booking_date = booking_date;
    }

    public long getReturn_by_time() {
        return return_by_time;
    }

    public void setReturn_by_time(long return_by_time) {
        this.return_by_time = return_by_time;
    }

    public String getPickup_address () {
        return getValidString(pickup_address);
    }

    public void setPickup_address (String pickup_address) {
        this.pickup_address = pickup_address;
    }

    public String getDrop_address () {
        return getValidString(drop_address);
    }

    public void setDrop_address (String drop_address) {
        this.drop_address = drop_address;
    }

    public String getTravel_route_map () {
        return getValidString(travel_route_map);
    }

    public void setTravel_route_map (String travel_route_map) {
        this.travel_route_map = travel_route_map;
    }

    public float getTotal_distance () {
        return total_distance;
    }

    public void setTotal_distance (float total_distance) {
        this.total_distance = total_distance;
    }

    public float getTotal_distance_extra() {
        return total_distance_extra;
    }

    public void setTotal_distance_extra(float total_distance_extra) {
        this.total_distance_extra = total_distance_extra;
    }

    public double getLatitude_from() {
        return latitude_from;
    }

    public void setLatitude_from (double latitude_from) {
        this.latitude_from = latitude_from;
    }

    public double getLongitude_from () {
        return longitude_from;
    }

    public void setLongitude_from (double longitude_from) {
        this.longitude_from = longitude_from;
    }

    public double getLatitude_to () {
        return latitude_to;
    }

    public void setLatitude_to (double latitude_to) {
        this.latitude_to = latitude_to;
    }

    public double getLongitude_to () {
        return longitude_to;
    }

    public void setLongitude_to (double longitude_to) {
        this.longitude_to = longitude_to;
    }

    public long getBooking_cancel_time_duration () {
        return booking_cancel_time_duration;
    }

    public void setBooking_cancel_time_duration (long booking_cancel_time_duration) {
        this.booking_cancel_time_duration = booking_cancel_time_duration;
    }
    public long getBooking_driver_cancel_time_duration() {
        return booking_driver_cancel_time_duration;
    }

    public void setBooking_driver_cancel_time_duration(long booking_driver_cancel_time_duration) {
        this.booking_driver_cancel_time_duration = booking_driver_cancel_time_duration;
    }




    public long getBooking_free_waiting_time_duration() {
        return booking_free_waiting_time_duration;
    }

    public void setBooking_free_waiting_time_duration(long booking_free_waiting_time_duration) {
        this.booking_free_waiting_time_duration = booking_free_waiting_time_duration;
    }

    public long getUtc_offset () {
        return utc_offset;
    }

    public void setUtc_offset (long utc_offset) {
        this.utc_offset = utc_offset;
    }

    public PromoCodeModel getPromocode () {
        return promocode;
    }

    public void setPromocode (PromoCodeModel promocode) {
        this.promocode = promocode;
    }

    public CityModel getCity () {
        return city;
    }

    public void setCity (CityModel city) {
        this.city = city;
    }

    public StateModel getState () {
        return state;
    }

    public void setState (StateModel state) {
        this.state = state;
    }

    public CountryModel getCountry () {
        return country;
    }

    public void setCountry (CountryModel country) {
        this.country = country;
    }

    public int getStatus () {
        return status;
    }

    public void setStatus (int status) {
        this.status = status;
    }

    public String getPaymentmethod () {
        return getValidString(paymentmethod);
    }

    public void setPaymentmethod (String paymentmethod) {
        this.paymentmethod = paymentmethod;
    }

    public VehicleTypeModel getVehicle () {
        return vehicle;
    }

    public void setVehicle (VehicleTypeModel vehicle) {
        this.vehicle = vehicle;
    }

    public FareModel getFare () {
        return fare;
    }

    public void setFare (FareModel fare) {
        this.fare = fare;
    }

    public String getOtp () {
        return getValidString(otp);
    }

    public void setOtp (String otp) {
        this.otp = otp;
    }

    public TotalTaxModel getTotal_tax () {
        return total_tax;
    }

    public void setTotal_tax (TotalTaxModel total_tax) {
        this.total_tax = total_tax;
    }

    public LatLng getPickupLatLng () {
        return new LatLng(latitude_from, longitude_from);
    }

    public LatLng getDropLatLng () {
        return new LatLng(latitude_to, longitude_to);
    }

    public boolean isNearByPickup (Location location) {

        return getDistanceBetweenLatLng(new LatLng(location.getLatitude(), location.getLongitude())
                , getPickupLatLng()) < 50;
    }

    public boolean isNearByDrop (Location location) {
        return getDistanceBetweenLatLng(new LatLng(location.getLatitude(), location.getLongitude())
                , getDropLatLng()) < 50;
    }

    public double getDistanceBetweenLatLng (LatLng origion_latLng, LatLng dest_latlng) {
        Location origion_location = new Location("origion");
        origion_location.setLatitude(origion_latLng.latitude);
        origion_location.setLongitude(origion_latLng.longitude);

        Location dest_location = new Location("destination");
        dest_location.setLatitude(dest_latlng.latitude);
        dest_location.setLongitude(dest_latlng.longitude);

        return origion_location.distanceTo(dest_location);
    }

    public boolean isBookingStarted () {
        return getStatus() == 4;
    }

    public boolean isBookingAccepted () {
        return getStatus() == 1;
    }


    public String getFormattedBookingStartTime (int tag) {
        if (tag == TAG_FOUR) {
            return getFormattedCalendar(TIME_FOUR, getBooking_start_time());
        }

        if (tag == TAG_TWO) {
            return getFormattedCalendar(DATE_TIME_TWO, getBooking_start_time());
        }

        return getFormattedCalendar(TIME_FOUR, getBooking_start_time());

    }

    public String getFormattedBookingEndTime (int tag) {
        if (tag == TAG_FOUR)
            return getFormattedCalendar(TIME_FOUR, getBooking_end_time());
        else if (tag == TAG_THREE) {
            return getFormattedCalendar(DATE_THREE, getBooking_end_time());
        }
        return getFormattedCalendar(DATE_TIME_TWO, getBooking_end_time());
    }

    public String getFormattedBookingTime (int tag) {
        String time = "0";
        if (getBooking_date() > 0) {
            if (tag == TAG_FOUR) {
                time = getFormattedCalendar(TIME_FOUR, getBooking_date());
            } else if (tag == TAG_THREE) {
                time = getFormattedCalendar(DATE_THREE, getBooking_date());
            } else {
                time = getFormattedCalendar(DATE_TIME_TWO, getBooking_date());
            }
        }
        return time;
    }

    public String getFormattedRetrunByTime(int tag) {
        String time = "0";
        if (getReturn_by_time() > 0) {
            if (tag == TAG_FOUR) {
                time = getFormattedCalendar(TIME_FOUR, getReturn_by_time());
            } else if (tag == TAG_THREE) {
                time = getFormattedCalendar(DATE_THREE, getReturn_by_time());
            } else {
                time = getFormattedCalendar(DATE_TIME_TWO, getReturn_by_time());
            }
        }
        return time;
    }

    public String getFormattedBookingCancelTime(int tag) {
        String time = "0";
        if (getBooking_end_time() > 0) {
            if (tag == TAG_FOUR) {
                time = getFormattedCalendar(TIME_FOUR, getBooking_end_time());
            } else if (tag == TAG_THREE) {
                time = getFormattedCalendar(DATE_THREE, getBooking_end_time());
            } else {
                time = getFormattedCalendar(DATE_TIME_TWO, getBooking_end_time());
            }
        }
        return time;
    }

    /************************/


    public String getBooking_user_type () {
        return getValidString(booking_user_type);
    }

    public void setBooking_user_type (String booking_user_type) {
        this.booking_user_type = booking_user_type;
    }

    public boolean isScheduleBooking () {
        return getBooking_type().equals("S");
    }

    public String getBooking_type () {
        return getValidString(booking_type);
    }

    public void setBooking_type (String booking_type) {
        this.booking_type = booking_type;
    }

    public String getBooking_running_type () {
        return getValidString(booking_running_type);
    }

    public void setBooking_running_type (String booking_running_type) {
        this.booking_running_type = booking_running_type;
    }

    public String getOutstation_trip_type() {
        return getValidString(outstation_trip_type);
    }

    public void setOutstation_trip_type(String outstation_trip_type) {
        this.outstation_trip_type = outstation_trip_type;
    }

    public String getSpecial_instruction() {
        return getValidString(special_instruction);
    }

    public void setSpecial_instruction (String special_instruction) {
        this.special_instruction = special_instruction;
    }

    public String getBooking_cancelled_by () {
        return getValidString(booking_cancelled_by);
    }

    public void setBooking_cancelled_by (String booking_cancelled_by) {
        this.booking_cancelled_by = booking_cancelled_by;
    }

    public String getCancellation_reason () {
        return getValidString(cancellation_reason);
    }

    public void setCancellation_reason (String cancellation_reason) {
        this.cancellation_reason = cancellation_reason;
    }

    public String getSeat_no () {
        return seat_no;
    }

    public void setSeat_no (String seat_no) {
        this.seat_no = seat_no;
    }

    public int getSeat_no_count () {
        return seat_no_count;
    }

    public void setSeat_no_count (int seat_no_count) {
        this.seat_no_count = seat_no_count;
    }

    public double getEnd_lng () {
        return end_lng;
    }

    public void setEnd_lng (double end_lng) {
        this.end_lng = end_lng;
    }

    public double getEnd_lat () {
        return end_lat;
    }

    public void setEnd_lat (double end_lat) {
        this.end_lat = end_lat;
    }

    public double getCancellation_driver_lat () {
        return cancellation_driver_lat;
    }

    public void setCancellation_driver_lat (double cancellation_driver_lat) {
        this.cancellation_driver_lat = cancellation_driver_lat;
    }

    public double getCancellation_driver_lng () {
        return cancellation_driver_lng;
    }

    public void setCancellation_driver_lng (double cancellation_driver_lng) {
        this.cancellation_driver_lng = cancellation_driver_lng;
    }

    public long getBooking_end_time () {
        return booking_end_time;
    }

    public void setBooking_end_time (long booking_end_time) {
        this.booking_end_time = booking_end_time;
    }

    public long getTaxi_id () {
        return taxi_id;
    }

    public void setTaxi_id (long taxi_id) {
        this.taxi_id = taxi_id;
    }

    public TaxiModel getTaxi () {
        return taxi;
    }

    public void setTaxi (TaxiModel taxi) {
        this.taxi = taxi;
    }

    public DriverModel getDriver () {
        return driver;
    }

    public void setDriver (DriverModel driver) {
        this.driver = driver;
    }

    public TotalTollsModel getTotal_tolls () {
        return total_tolls;
    }

    public void setTotal_tolls (TotalTollsModel total_tolls) {
        this.total_tolls = total_tolls;
    }

    public float getTrip_commission () {
        return trip_commission;
    }

    public void setTrip_commission (float trip_commission) {
        this.trip_commission = trip_commission;
    }

    public float getDriver_commission () {
        return driver_commission;
    }

    public void setDriver_commission (float driver_commission) {
        this.driver_commission = driver_commission;
    }

    public float getAdmin_hand () {
        return admin_hand;
    }

    public void setAdmin_hand (float admin_hand) {
        this.admin_hand = admin_hand;
    }

    public float getDriver_hand () {
        return driver_hand;
    }

    public void setDriver_hand (float driver_hand) {
        this.driver_hand = driver_hand;
    }

    public float getPayout_amount () {
        return payout_amount;
    }

    public void setPayout_amount (float payout_amount) {
        this.payout_amount = payout_amount;
    }

    public String getSchedule_success_msg () {
        return getValidString(schedule_success_msg);
    }

    public void setSchedule_success_msg (String schedule_success_msg) {
        this.schedule_success_msg = schedule_success_msg;
    }

    public String getBookingId () {
        return "ID-" + getBooking_id();
    }

    public String getVehicleId () {
        VehicleTypeModel vehicle = getVehicle();
        String s = "";
        if (getStatus() == 6) {
            s += " Cancel by you";
        } else if (getStatus() == 7) {
            s += " Cancel by driver";
        } else {
            s += vehicle.getName();
        }
        return s;
    }

    public String getTimeText () {
        return getValidTimeText(getTotal_travel_time());
    }

    public String getRoundTimeText() {
        long total_travel_time_roundtrip = getTotal_travel_time_roundtrip();
        long days = TimeUnit.MINUTES.toDays(total_travel_time_roundtrip);
        StringBuilder builder = new StringBuilder();
        if (days > 0) {
            builder.append(days);
            builder.append((days > 1) ? " days" : " day");
        }
        if (builder.length() > 0) {
            builder.append(" ");
        }
        builder.append(getValidTimeText(total_travel_time_roundtrip - TimeUnit.DAYS.toMinutes(days)).toLowerCase());

        return builder.toString();
    }

    public String getTotal_distanceText() {
        float total_distance = getTotal_distance() * 1000;
        return getValidDistanceText(total_distance);
    }

    public String getTotalDistanceRoundText() {
        float total_distance = getTotal_distance_round_trip() * 1000;
        return getValidDistanceText(total_distance);
    }

    public boolean hasDropAddress() {
        return getDrop_address().equals("N/A") ? false : true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof BookCabModel) {
            return ((BookCabModel) obj).getBooking_id() == getBooking_id();
        }
        return false;
    }

    public boolean isSharingBooking() {
        return getBooking_running_type().equals("S");
    }

    public boolean isRentalBooking() {
        return getBooking_running_type().equals("R");
    }

    public boolean isOutstationBooking() {
        return getBooking_running_type().equals("O");
    }

    public boolean isOutstationOnewayTrip() {
        return getOutstation_trip_type().equals("O");
    }

    public boolean isOutstationRoundTrip() {
        return getOutstation_trip_type().equals("R");
    }

    public List<RentalPackageModel> getPackages() {
        return packages;
    }

    public void setPackages(List<RentalPackageModel> packages) {
        this.packages = packages;
    }

    public float getTotal_distance_round_trip() {
        return total_distance_round_trip;
    }

    public void setTotal_distance_round_trip(float total_distance_round_trip) {
        this.total_distance_round_trip = total_distance_round_trip;
    }

    public long getTotal_travel_time_roundtrip() {
        return total_travel_time_roundtrip;
    }

    public void setTotal_travel_time_roundtrip(long total_travel_time_roundtrip) {
        this.total_travel_time_roundtrip = total_travel_time_roundtrip;
    }

    public OutstationPackageModel getOutstation() {
        return outstation;
    }

    public void setOutstation(OutstationPackageModel outstation) {
        this.outstation = outstation;
    }


    public String getOnewayMessageDetail() {
        return "One way trip of about " + getTotal_distanceText().toLowerCase();
    }

    public String getRoundtripMessageDetail() {
        return getRoundTimeText() + " round trip of about " + getTotalDistanceRoundText().toLowerCase();
    }
}
