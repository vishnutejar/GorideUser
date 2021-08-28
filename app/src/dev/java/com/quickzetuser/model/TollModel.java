package com.quickzetuser.model;


import com.models.BaseModel;

/**
 * @author Manish Kumar
 * @since 29/8/18
 */

public class  TollModel extends BaseModel {
    private long id;
    private String toll_name;
    private float value;


    public long getId () {
        return id;
    }

    public void setId (long id) {
        this.id = id;
    }

    public String getToll_name () {
        return getValidString(toll_name);
    }

    public void setToll_name (String toll_name) {
        this.toll_name = toll_name;
    }

    public float getValue () {
        return value;
    }

    public void setValue (float value) {
        this.value = value;
    }

    @Override
    public boolean equals (Object obj) {
        if (obj != null && obj instanceof TollModel) {
            TollModel model = (TollModel) obj;
            return model.getId() == this.getId();
        }
        return false;
    }

}
