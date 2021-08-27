package com.quickzetuser.model;

import com.distancematrix.DistanceMatrixModel;
import com.models.BaseModel;
import com.quickzetuser.rest.WebRequestConstants;

import java.util.List;

/**
 * Created by Sunil kumar yadav on 13/4/18.
 */

public class VehicleTypeModel extends BaseModel {

    long id;
    String name;
    int seating_capacity;
    String type;
    String image_selected;
    String image_unselected;
    List<DriverModel> driver;
    DistanceMatrixModel distanceMatrixModel;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return getValidString(name);
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
        return getValidString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage_selected() {
        return getValidString(image_selected);
    }

    public void setImage_selected(String image_selected) {
        this.image_selected = image_selected;
    }

    public String getImage_unselected() {
        return getValidString(image_unselected);
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

    public DistanceMatrixModel getDistanceMatrixModel() {
        return distanceMatrixModel;
    }

    public void setDistanceMatrixModel(DistanceMatrixModel distanceMatrixModel) {
        this.distanceMatrixModel = distanceMatrixModel;
    }

    @Override
    public String toString() {
        return String.valueOf(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof VehicleTypeModel) {
            return ((VehicleTypeModel) obj).getId() == getId();
        }
        return false;
    }

    public String getAvailableDriverTime() {
        if (distanceMatrixModel != null && distanceMatrixModel.getDestinationModelList().size() > 0) {
            DistanceMatrixModel.DestinationModel destinationModel = distanceMatrixModel.getDestinationModelList().get(0);
            if (destinationModel.isStatusOk()) {
                return destinationModel.getDuration_text();
            }
        }
        return null;
    }

    public boolean isRentalVehicleType() {
        return id == WebRequestConstants.RENTAL_CAB_TYPE_ID;
    }

    public boolean isOutstationVehicleType() {
        return id == WebRequestConstants.OUSTATION_CAB_TYPE_ID;
    }

    public boolean isSharingVehicleType() {
        return id == WebRequestConstants.SHARING_CAB_TYPE_ID;
    }
}
