package com.rider.model.web_response;

import com.rider.model.FavouriteModel;

import java.util.List;

/**
 * @author Sunil kumar Yadav
 * @Since 3/7/18
 */
public class FavouriteResponseModel extends BaseWebServiceModelResponse {

    private List<FavouriteModel> data;

    public List<FavouriteModel> getData() {
        return data;
    }

    public void setData(List<FavouriteModel> data) {
        this.data = data;
    }
}
