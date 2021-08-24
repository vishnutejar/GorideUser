package com.rider.model;

import com.rider.appBase.AppBaseModel;

import java.util.List;

public class RentalPackageModel extends AppBaseModel {

    private long package_id;
    private String package_name;
    private long max_duration;
    private double max_distance;

    private List<RentalFareModel> fares;

    public void setPackage_id (long package_id) {
        this.package_id = package_id;
    }

    public long getPackage_id () {
        return package_id;
    }

    public String getPackage_name () {
        return getValidString(package_name);
    }

    public void setPackage_name (String package_name) {
        this.package_name = package_name;
    }

    public long getMax_duration () {
        return max_duration;
    }

    public String getMax_durationText () {
        return getValidTimeText(max_duration);
    }

    public void setMax_duration (long max_duration) {
        this.max_duration = max_duration;
    }

    public double getMax_distance () {
        return max_distance;
    }

    public String getMax_distanceText () {
        return getValidDistanceText(max_distance * 1000);
    }

    public void setMax_distance (double max_distance) {
        this.max_distance = max_distance;
    }

    public List<RentalFareModel> getFares () {
        return fares;
    }

    public void setFares (List<RentalFareModel> fares) {
        this.fares = fares;
    }

    @Override
    public String toString () {
        return getPackage_name();
    }


    public String getCabTypes () {
        if (getFares() == null || getFares().size() == 0) {
            return "0";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (RentalFareModel rentalFareModel : getFares()) {
            if (stringBuilder.length() == 0) {
                stringBuilder.append(rentalFareModel.getVehicle().getId());
            } else {
                stringBuilder.append(",").append(rentalFareModel.getVehicle().getId());
            }
        }
        return stringBuilder.toString();
    }
}
