package com.quickzetuser.model;

import com.models.BaseModel;


/**
 * Created by Sunil kumar yadav on 16/4/18.
 */

public class  NightChargeModel extends BaseModel {

    private float night_charge_amount;
    private float night_charge_value;
    private String night_charge_value_type;

    public float getNight_charge_amount() {
        return night_charge_amount;
    }

    public void setNight_charge_amount(float night_charge_amount) {
        this.night_charge_amount = night_charge_amount;
    }

    public float getNight_charge_value() {
        return night_charge_value;
    }

    public void setNight_charge_value(float night_charge_value) {
        this.night_charge_value = night_charge_value;
    }

    public String getNight_charge_value_type() {
        return getValidString(night_charge_value_type);
    }

    public void setNight_charge_value_type(String night_charge_value_type) {
        this.night_charge_value_type = night_charge_value_type;
    }

    public String getNight_charge_amountText() {
        return getValidDecimalFormat(getNight_charge_amount());
    }
}
