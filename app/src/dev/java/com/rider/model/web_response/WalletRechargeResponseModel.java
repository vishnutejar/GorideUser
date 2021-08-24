package com.rider.model.web_response;


import com.rider.model.WalletRechargeModel;

public class WalletRechargeResponseModel extends BaseWebServiceModelResponse {

    private WalletRechargeModel data;

    public WalletRechargeModel getData() {
        return data;
    }

    public void setData(WalletRechargeModel data) {
        this.data = data;
    }
}
