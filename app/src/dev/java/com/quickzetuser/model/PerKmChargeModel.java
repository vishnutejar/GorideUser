package com.quickzetuser.model;

import com.quickzetuser.appBase.AppBaseModel;

/**
 * Created by ubuntu on 16/4/18.
 */

public class PerKmChargeModel extends AppBaseModel {

    private int id;
    private float min_km;
    private float max_km;
    private float amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getMin_km() {
        return min_km;
    }

    public void setMin_km(float min_km) {
        this.min_km = min_km;
    }

    public float getMax_km() {
        return max_km;
    }

    public void setMax_km(float max_km) {
        this.max_km = max_km;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getAmountText() {
        return getValidDecimalFormat(amount) + PER_KM;
    }

    public String getDistanceText() {
        return String.valueOf(min_km + KM + " - " + max_km + KM);
    }

}
