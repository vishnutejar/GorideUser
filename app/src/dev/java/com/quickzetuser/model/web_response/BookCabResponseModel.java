package com.quickzetuser.model.web_response;


import com.quickzetuser.model.BookCabModel;


/**
 * Created by ubuntu on 18/4/18.
 */

public class  BookCabResponseModel extends BaseWebServiceModelResponse{

    private BookCabModel data;

    public BookCabModel getData() {
        return data;
    }

    public void setData(BookCabModel data) {
        this.data = data;
    }

}
