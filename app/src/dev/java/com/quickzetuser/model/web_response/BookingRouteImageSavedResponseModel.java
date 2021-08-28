package com.quickzetuser.model.web_response;


public class  BookingRouteImageSavedResponseModel extends BaseWebServiceModelResponse {



    private String data;

    public String getData () {
        return getValidString(data);
    }

    public void setData (String data) {
        this.data = data;
    }
}
