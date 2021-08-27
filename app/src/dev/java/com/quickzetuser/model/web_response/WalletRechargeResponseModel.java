package com.quickzetuser.model.web_response;


import com.quickzetuser.model.WalletRechargeModel;

public class WalletRechargeResponseModel extends BaseWebServiceModelResponse {

    private WalletRechargeModel data;

    public WalletRechargeModel getData() {
        return data;
    }

    public void setData(WalletRechargeModel data) {
        this.data = data;
    }
}
