package com.quickzetuser.model.web_response;

import com.quickzetuser.model.CityModel;

import java.util.List;

/**
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class CityResponseModel extends BaseWebServiceModelResponse {

    private List<CityModel> data;


    public List<CityModel> getData() {
        return data;
    }

    public void setData(List<CityModel> data) {
        this.data = data;
    }

}
