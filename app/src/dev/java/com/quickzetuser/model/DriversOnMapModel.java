package com.quickzetuser.model;

import com.models.BaseModel;

import java.util.List;

/**
 * Created by Sunil kumar yadav on 4/5/18.
 */
public class DriversOnMapModel extends BaseModel {

    long id;
    String name;
    int seating_capacity;
    String type;
    String image_selected;
    String image_unselected;
    List<DriverModel> driver;

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

    public int getSeating_capacity() {
        return seating_capacity;
    }

    public void setSeating_capacity(int seating_capacity) {
        this.seating_capacity = seating_capacity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage_selected() {
        return image_selected;
    }

    public void setImage_selected(String image_selected) {
        this.image_selected = image_selected;
    }

    public String getImage_unselected() {
        return image_unselected;
    }

    public void setImage_unselected(String image_unselected) {
        this.image_unselected = image_unselected;
    }

    public List<DriverModel> getDriver() {
        return driver;
    }

    public void setDriver(List<DriverModel> driver) {
        this.driver = driver;
    }
}
