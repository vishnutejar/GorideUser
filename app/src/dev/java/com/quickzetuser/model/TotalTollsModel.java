package com.quickzetuser.model;


import com.models.BaseModel;

import java.util.List;

/**
 * Created by Sunil kumar yadav on 16/4/18.
 */

public class TotalTollsModel extends BaseModel {

    private List<TollModel> tolls;
    private float toll_fare;

    public List<TollModel> getTolls () {
        return tolls;
    }

    public void setTolls (List<TollModel> tolls) {
        this.tolls = tolls;
    }

    public float getToll_fare () {
        return toll_fare;
    }

    public String getToll_fareText () {
        return getValidDecimalFormat(toll_fare);
    }

    public void setToll_fare (float toll_fare) {
        this.toll_fare = toll_fare;
    }
}
