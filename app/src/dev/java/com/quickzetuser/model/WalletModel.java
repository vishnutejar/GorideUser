package com.quickzetuser.model;



import com.models.BaseModel;

public class  WalletModel extends BaseModel {

    private float wallet_amount;
    int free_ride;

    public float getWallet_amount() {
        return wallet_amount;
    }

    public String getWallet_amountText() {
        return getValidDecimalFormat(wallet_amount);
    }

    public void setWallet_amount(float wallet_amount) {
        this.wallet_amount = wallet_amount;
    }

    public int getFree_ride() {
        return free_ride;
    }

    public void setFree_ride(int free_ride) {
        this.free_ride = free_ride;
    }
}
