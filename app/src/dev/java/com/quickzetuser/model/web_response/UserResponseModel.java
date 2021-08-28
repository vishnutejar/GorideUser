package com.quickzetuser.model.web_response;



import com.quickzetuser.model.UserModel;

/**
 * Created by ubuntu on 29/3/18.
 */

public class  UserResponseModel extends BaseWebServiceModelResponse {

    private UserModel data;

    public UserModel getData() {
        return data;
    }

    public void setData(UserModel data) {
        this.data = data;
    }

}
