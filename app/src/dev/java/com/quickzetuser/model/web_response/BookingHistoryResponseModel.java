package com.quickzetuser.model.web_response;


import com.quickzetuser.model.BookCabModel;

import java.util.List;


/**
 * Created by ubuntu on 18/4/18.
 */

public class BookingHistoryResponseModel extends BaseWebServiceModelResponse{

    private List<BookCabModel> data;

    public List<BookCabModel> getData() {
        return data;
    }

    public void setData(List<BookCabModel> data) {
        this.data = data;
    }
}
