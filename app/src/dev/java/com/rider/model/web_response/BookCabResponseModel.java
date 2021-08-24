package com.rider.model.web_response;


import com.rider.model.BookCabModel;

/**
 * Created by ubuntu on 18/4/18.
 */

public class BookCabResponseModel extends BaseWebServiceModelResponse{

    private BookCabModel data;

    public BookCabModel getData() {
        return data;
    }

    public void setData(BookCabModel data) {
        this.data = data;
    }

}
