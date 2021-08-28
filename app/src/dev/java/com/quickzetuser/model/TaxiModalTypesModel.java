package com.quickzetuser.model;



import com.models.BaseModel;

/**
 * Created by ubuntu on 3/4/18.
 */

public class  TaxiModalTypesModel extends BaseModel {

    private long model_id;
    private String model_name;

    public long getModel_id() {
        return model_id;
    }

    public void setModel_id(long model_id) {
        this.model_id = model_id;
    }

    public String getModel_name() {
        return   getValidString(model_name);
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    @Override
    public String toString() {
        return getModel_name();
    }

}
