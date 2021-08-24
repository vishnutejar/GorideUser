package com.rider.model.web_response;


import com.rider.model.CountryModel;

import java.util.List;

/**
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class CountriesResponseModel extends BaseWebServiceModelResponse{

    private List<CountryModel> data;

    public List<CountryModel> getData() {
        return data;
    }

    public void setData(List<CountryModel> data) {
        this.data = data;
    }

}
