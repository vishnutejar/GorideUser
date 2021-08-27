package com.quickzetuser.model.web_response;

import com.quickzetuser.model.NotificationModel;

import java.util.List;

/**
 * @author Sunil kumar Yadav
 * @Since 24/5/18
 */
public class NotificationResponseModel extends BaseWebServiceModelResponse {

    List<NotificationModel> data;

    public List<NotificationModel> getData() {
        return data;
    }

    public void setData(List<NotificationModel> data) {
        this.data = data;
    }
}
