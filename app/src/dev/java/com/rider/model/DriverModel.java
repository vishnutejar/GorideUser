package com.rider.model;

import com.google.android.gms.maps.model.LatLng;
import com.models.BaseModel;


/**
 * Created by ubuntu on 26/4/18.
 */

public class DriverModel extends BaseModel {


    private long driver_id;
    private String firstname;
    private String lastname;
    private String phone;
    private String mobile_code;
    private float avg_rating;
    private String image;
    private double latitude;
    private double longitude;
    private float distance;

    public long getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(long driver_id) {
        this.driver_id = driver_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile_code() {
        return mobile_code;
    }

    public void setMobile_code(String mobile_code) {
        this.mobile_code = mobile_code;
    }

    public float getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(float avg_rating) {
        this.avg_rating = avg_rating;
    }

    public String getImage() {
        return getValidString(image);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return String.valueOf(getDriver_id());
    }

    public LatLng getPosition() {
        return new LatLng(getLatitude(), getLongitude());
    }

    public String getFullName(){
        return getFirstname()+" "+getLastname();
    }

    public String getFullMobile () {
        if (isValidString(getMobile_code())) {
            return getMobile_code() + " " + getPhone();
        }
        return getPhone();
    }

    public String getMobileForCall () {
        if (isValidString(getMobile_code())) {
            return getMobile_code() + getPhone();
        }
        return getPhone();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null &&
                obj instanceof DriverModel &&
                ((DriverModel) obj).getDriver_id() == getDriver_id();
    }


    public String getRating(){
        return getValidRatingFormat(String.valueOf(getAvg_rating()));
    }
}
