package com.quickzetuser.model;


import com.models.BaseModel;

/**
 * Created by ubuntu on 3/4/18.
 */

public class TaxiBrandModel extends BaseModel {

    private long brand_id;
    private String brand_name;

    public long getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(long brand_id) {
        this.brand_id = brand_id;
    }

    public String getBrand_name() {
      return   getValidString(brand_name);
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    @Override
    public String toString() {
        return getBrand_name();
    }
}
