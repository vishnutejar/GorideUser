package com.quickzetuser.model;

import com.medy.retrofitwrapper.BaseModel;


/**
 * Created by ubuntu on 16/4/18.
 */

public class PeakHourChargeModel extends BaseModel {

    private float peak_hour_charge_value;
    private float peak_hour_charge_amount;
    private String peak_hour_charge_value_type;

    public float getPeak_hour_charge_value() {
        return peak_hour_charge_value;
    }

    public void setPeak_hour_charge_value(float peak_hour_charge_value) {
        this.peak_hour_charge_value = peak_hour_charge_value;
    }

    public String getPeak_hour_charge_value_type() {
        return getValidString(peak_hour_charge_value_type);
    }

    public void setPeak_hour_charge_value_type(String peak_hour_charge_value_type) {
        this.peak_hour_charge_value_type = peak_hour_charge_value_type;
    }

    public float getPeak_hour_charge_amount() {
        return peak_hour_charge_amount;
    }

    public void setPeak_hour_charge_amount(float peak_hour_charge_amount) {
        this.peak_hour_charge_amount = peak_hour_charge_amount;
    }

    public String getPeak_hour_charge_amountText() {
        return getValidDecimalFormat(getPeak_hour_charge_amount());
    }
}
