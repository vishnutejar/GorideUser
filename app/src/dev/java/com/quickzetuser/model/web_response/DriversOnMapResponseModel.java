package com.quickzetuser.model.web_response;

import com.quickzetuser.model.VehicleTypeModel;

import java.util.List;


/**
 * Created by Sunil kumar yadav on 4/5/18.
 */
public class  DriversOnMapResponseModel extends BaseWebServiceModelResponse {

    List<VehicleTypeModel> data;

    public List<VehicleTypeModel> getData() {
        return data;
    }

    public void setData(List<VehicleTypeModel> data) {
        this.data = data;
    }
}
