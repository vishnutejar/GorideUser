package com.quickzetuser.model.web_response;


import com.quickzetuser.model.WalletModel;

public class WalletResponseModel extends BaseWebServiceModelResponse {

    private WalletModel data;

    public WalletModel getData() {
        return data;
    }

    public void setData(WalletModel data) {
        this.data = data;
    }
}
