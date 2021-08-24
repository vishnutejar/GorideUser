package com.rider.model;

import android.location.Location;

import com.addressfetching.LocationModelFull;
import com.google.android.gms.maps.model.LatLng;
import com.models.BaseModel;

public class AddressModel extends BaseModel {

    private long id;
    private String type;
    private String title;
    private String house_no;
    private String landmark;
    private String address;
    private double latitude;
    private double longitude;

    private String primarytext = "";
    private String secondarytext = "";
    private String city = "";
    private String street = "";
    private String township = "";
    private String building = "";
    private String postalcode = "";
    private String state = "";
    private String statefull = "";
    private String country = "";
    private String countryfull = "";

    public AddressModel() {

    }

    public AddressModel(LocationModelFull.LocationModel locationModel) {
        primarytext = locationModel.getPrimaryText();
        secondarytext = locationModel.getSecondaryText();
        address = locationModel.getFulladdress();
        latitude = locationModel.getLatitude();
        longitude = locationModel.getLongitude();
        city = locationModel.getCity();
        street = locationModel.getStreet();
        township = locationModel.getTownship();
        building = locationModel.getBuilding();
        postalcode = locationModel.getPostalCode();
        state = locationModel.getState();
        statefull = locationModel.getStateFull();
        country = locationModel.getCountry();
        countryfull = locationModel.getCountryFull();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return getValidString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return getValidString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHouse_no() {
        return getValidString(house_no);
    }

    public void setHouse_no(String house_no) {
        this.house_no = house_no;
    }

    public String getLandmark() {
        return getValidString(landmark);
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getAddress() {
        return getValidString(address);
    }

    public String getAddressText() {
        StringBuilder builder = new StringBuilder();
        if (isValidString(getHouse_no())) {
            builder.append(getHouse_no()).append(", ");
        }
        if (isValidString(getLandmark())) {
            builder.append(getLandmark()).append(", ");
        }
        builder.append(getAddress());
        return builder.toString();
    }

    public String getAddressTextForNearMe() {
        if (isValidString(getType())) {
            if (isOther()) {
                return getType() + " (" + getTitle() + ")";
            }
            return getType();
        }

        return getAddress();


    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getPrimarytext() {
        return getValidString(primarytext);
    }

    public void setPrimarytext(String primarytext) {
        this.primarytext = primarytext;
    }

    public String getSecondarytext() {
        return getValidString(secondarytext);
    }

    public void setSecondarytext(String secondarytext) {
        this.secondarytext = secondarytext;
    }

    public String getCity() {
        return getValidString(city);
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return getValidString(street);
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getTownship() {
        return getValidString(township);
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public String getBuilding() {
        return getValidString(building);
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getPostalcode() {
        return getValidString(postalcode);
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getState() {
        return getValidString(state);
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatefull() {
        return getValidString(statefull);
    }

    public void setStatefull(String statefull) {
        this.statefull = statefull;
    }

    public String getCountry() {
        return getValidString(country);
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryfull() {
        return getValidString(countryfull);
    }

    public void setCountryfull(String countryfull) {
        this.countryfull = countryfull;
    }

    public boolean isHome() {
        return getType().equalsIgnoreCase("HOME");
    }

    public boolean isWork() {
        return getType().equalsIgnoreCase("WORK");
    }

    public boolean isOther() {
        return getType().equalsIgnoreCase("OTHER");
    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }

    public Location getLocationModel() {
        Location location = new Location("Address_" + getId());
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
}
