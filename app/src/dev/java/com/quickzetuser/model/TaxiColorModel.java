package com.quickzetuser.model;



import com.models.BaseModel;

/**
 * Created by ubuntu on 3/4/18.
 */

public class  TaxiColorModel extends BaseModel {
    private String name;
    private String hexcode;

    public String getName() {
        return   getValidString(name);

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHexcode() {
        return   getValidString(hexcode);
    }

    public void setHexcode(String hexcode) {
        this.hexcode = hexcode;
    }

    @Override
    public String toString() {
        return getName();
    }
}
