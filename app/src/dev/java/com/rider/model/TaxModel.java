package com.rider.model;

import com.models.BaseModel;

/**
 * Created by Sunil kumar yadav on 16/4/18.
 */

public class TaxModel extends BaseModel{

    long id;
    String name;
    int value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
