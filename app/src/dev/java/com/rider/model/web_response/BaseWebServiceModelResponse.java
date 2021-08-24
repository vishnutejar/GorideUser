package com.rider.model.web_response;

import com.models.BaseModel;

/**
 * Created by ubuntu on 27/3/18.
 */

public class BaseWebServiceModelResponse extends BaseModel {

    int code;
    boolean error = true;
    String message;


    public int getCode () {
        return code;
    }

    public void setCode (int code) {
        this.code = code;
    }

    public boolean isError () {
        return error;
    }

    public void setError (boolean error) {
        this.error = error;
    }

    public String getMessage () {
        return message;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public boolean isValidString (String data) {
        return data != null && !data.trim().isEmpty();
    }

}
