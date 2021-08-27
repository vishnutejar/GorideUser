package com.quickzetuser.model;

import com.models.BaseModel;


/**
 * Created by Sunil kumar yadav on 16/4/18.
 */

public class PromoCodeModel extends BaseModel {

    private double promocode_id;
    private String promocode_value;
    private String promocode_value_type;
    private String promocode;
    private double promocode_amount;

    public double getPromocode_id() {
        return promocode_id;
    }

    public void setPromocode_id(double promocode_id) {
        this.promocode_id = promocode_id;
    }

    public String getPromocode_value() {
        return getValidString(promocode_value);
    }

    public void setPromocode_value(String promocode_value) {
        this.promocode_value = promocode_value;
    }

    public String getPromocode_value_type() {
        return getValidString(promocode_value_type);
    }

    public void setPromocode_value_type(String promocode_value_type) {
        this.promocode_value_type = promocode_value_type;
    }

    public String getPromocode() {
        return getValidString(promocode);
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }

    public String getPromocode_amount() {
        return getValidDecimalFormat(promocode_amount);
    }

    public void setPromocode_amount(double promocode_amount) {
        this.promocode_amount = promocode_amount;
    }
}
