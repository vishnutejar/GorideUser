package com.quickzetuser.model.web_response;

import com.quickzetuser.model.StateModel;

import java.util.List;


/**
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class  StateResponseModel extends BaseWebServiceModelResponse {


    private List<StateModel> data;

    public List<StateModel> getData() {
        return data;
    }

    public void setData(List<StateModel> data) {
        this.data = data;
    }
}
