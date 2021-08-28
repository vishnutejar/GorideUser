package com.quickzetuser.model;



import com.models.BaseModel;

/**
 * Created by ubuntu on 3/4/18.
 */

public class  TaxiModel extends BaseModel {

    private long taxi_id;
    private long driver_id;
    private long vehicle_type_id;
    private String vehicle_type_name;
    private TaxiBrandModel brand;
    private TaxiModalTypesModel model;
    private TaxiColorModel color;
    private long year;
    private String license_plate_no;
    private String is_verified;
    private String status;
    private String seating_capacity;


    public long getTaxi_id () {
        return taxi_id;
    }

    public void setTaxi_id (long taxi_id) {
        this.taxi_id = taxi_id;
    }

    public long getDriver_id () {
        return driver_id;
    }

    public void setDriver_id (long driver_id) {
        this.driver_id = driver_id;
    }

    public long getVehicle_type_id () {
        return vehicle_type_id;
    }

    public void setVehicle_type_id (long vehicle_type_id) {
        this.vehicle_type_id = vehicle_type_id;
    }

    public String getVehicle_type_name () {
        return getValidString(vehicle_type_name);

    }

    public void setVehicle_type_name (String vehicle_type_name) {
        this.vehicle_type_name = vehicle_type_name;
    }

    public TaxiBrandModel getBrand () {

        return brand;
    }

    public void setBrand (TaxiBrandModel brand) {
        this.brand = brand;
    }

    public TaxiModalTypesModel getModel () {

        return model;
    }

    public void setModel (TaxiModalTypesModel model) {
        this.model = model;
    }

    public TaxiColorModel getColor () {

        return color;
    }

    public void setColor (TaxiColorModel color) {
        this.color = color;
    }

    public long getYear () {

        return year;
    }

    public void setYear (long year) {
        this.year = year;
    }

    public String getLicense_plate_no () {
        return getValidString(license_plate_no);
    }

    public void setLicense_plate_no (String license_plate_no) {
        this.license_plate_no = license_plate_no;
    }

    public String getIs_verified () {
        return getValidString(is_verified);
    }

    public void setIs_verified (String is_verified) {
        this.is_verified = is_verified;
    }

    public String getStatus () {
        return getValidString(status);
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public String getSeating_capacity () {
        return getValidString(seating_capacity);
    }

    public void setSeating_capacity (String seating_capacity) {
        this.seating_capacity = seating_capacity;
    }


    @Override
    public boolean equals (Object obj) {
        if (obj != null && obj instanceof TaxiModel) {
            return ((TaxiModel) obj).getTaxi_id() == getTaxi_id();
        }
        return false;
    }

    public String getTaxiColorWithModelName () {
        StringBuilder stringBuilder = new StringBuilder();
        if (color != null) {
            stringBuilder.append(color.getName());
        }
        if (model != null) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(model.getModel_name());
        }
        return stringBuilder.toString();
    }

}
