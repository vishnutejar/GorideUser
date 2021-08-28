package com.quickzetuser.model.request_model;

import com.addressfetching.LocationModelFull;
import com.medy.retrofitwrapper.ParamJSON;


/**
 * @author Sunil kumar Yadav
 * @Since 3/7/18
 */
public class  FavouriteRequestModel extends AppBaseRequestModel {
    public String type;

    @ParamJSON
    public LocationModelFull.LocationModel detail;
}
