package com.rider.model;

import com.addressfetching.LocationModelFull;
import com.rider.model.web_response.BaseWebServiceModelResponse;

/**
 * @author Sunil kumar Yadav
 * @Since 3/7/18
 */
public class FavouriteModel extends BaseWebServiceModelResponse {
    private long id;
    private String type;
    private LocationModelFull.LocationModel detail;

    public FavouriteModel() {

    }

    public FavouriteModel(String type) {
        this.type = type;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return getValidString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocationModelFull.LocationModel getDetail() {
        return detail;
    }

    public void setDetail(LocationModelFull.LocationModel detail) {
        this.detail = detail;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof FavouriteModel) {
            FavouriteModel favouriteModel = (FavouriteModel) obj;
            return favouriteModel.getType().equals(getType());
        }
        return false;
    }

    public boolean isHomeType() {
        return getType().equals("Home");
    }

    public boolean isWorkType() {
        return getType().equals("Work");
    }
}
