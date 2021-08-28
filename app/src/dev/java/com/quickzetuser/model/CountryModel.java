package com.quickzetuser.model;

import com.models.BaseModel;


/**
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class  CountryModel extends BaseModel{

    private float country_id;
    private String country_name;
    private String country_mobile_code;
    private String country_code;


    public float getCountry_id() {
        return country_id;
    }

    public void setCountry_id(float country_id) {
        this.country_id = country_id;
    }

    public String getCountry_name() {
        return getValidString(country_name);
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCountry_mobile_code() {
        return getValidString(country_mobile_code);
    }

    public void setCountry_mobile_code(String country_mobile_code) {
        this.country_mobile_code = country_mobile_code;
    }

    public String getCountry_code() {
        return getValidString(country_code);
    }

    public String setCountry_code(String country_code) {
        return this.country_code = country_code;
    }

    @Override
    public String toString() {
        return getCountry_name();
    }
}
