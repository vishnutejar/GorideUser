package com.rider.model;

import com.models.BaseModel;

/**
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class StateModel extends BaseModel {

    private float state_id;
    private String state_name;

    public float getState_id() {
        return state_id;
    }

    public void setState_id(float state_id) {
        this.state_id = state_id;
    }

    public String getState_name() {
        return getValidString(state_name);
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    @Override
    public String toString() {
        return state_name;
    }
}
