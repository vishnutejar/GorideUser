package com.quickzetuser.model;

import com.quickzetuser.appBase.AppBaseModel;

import java.util.List;


public class OutstationPackageModel extends AppBaseModel {
    boolean oneway;
    boolean roundtrip;
    float outstation_driver_allounce_oneway;
    float outstation_driver_allounce_roundtrip;
    float outstation_night_charge_oneway;
    float outstation_night_charge_roundtrip;
    String outstation_night_time_from;
    String outstation_night_time_to;

    private List<OutstationFareModel> fares;

    public boolean isOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    public boolean isRoundtrip() {
        return roundtrip;
    }

    public void setRoundtrip(boolean roundtrip) {
        this.roundtrip = roundtrip;
    }

    public float getOutstation_driver_allounce_oneway() {
        return outstation_driver_allounce_oneway;
    }

    public void setOutstation_driver_allounce_oneway(float outstation_driver_allounce_oneway) {
        this.outstation_driver_allounce_oneway = outstation_driver_allounce_oneway;
    }

    public float getOutstation_driver_allounce_roundtrip() {
        return outstation_driver_allounce_roundtrip;
    }

    public void setOutstation_driver_allounce_roundtrip(float outstation_driver_allounce_roundtrip) {
        this.outstation_driver_allounce_roundtrip = outstation_driver_allounce_roundtrip;
    }

    public float getOutstation_night_charge_oneway() {
        return outstation_night_charge_oneway;
    }

    public void setOutstation_night_charge_oneway(float outstation_night_charge_oneway) {
        this.outstation_night_charge_oneway = outstation_night_charge_oneway;
    }

    public float getOutstation_night_charge_roundtrip() {
        return outstation_night_charge_roundtrip;
    }

    public void setOutstation_night_charge_roundtrip(float outstation_night_charge_roundtrip) {
        this.outstation_night_charge_roundtrip = outstation_night_charge_roundtrip;
    }

    public String getOutstation_night_time_from() {
        return getValidString(outstation_night_time_from);
    }

    public void setOutstation_night_time_from(String outstation_night_time_from) {
        this.outstation_night_time_from = outstation_night_time_from;
    }

    public String getOutstation_night_time_to() {
        return getValidString(outstation_night_time_to);
    }

    public void setOutstation_night_time_to(String outstation_night_time_to) {
        this.outstation_night_time_to = outstation_night_time_to;
    }

    public List<OutstationFareModel> getFares() {
        return fares;
    }

    public void setFares(List<OutstationFareModel> fares) {
        this.fares = fares;
    }
}
