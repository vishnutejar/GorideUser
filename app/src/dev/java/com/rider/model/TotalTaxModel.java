package com.rider.model;

import com.models.BaseModel;

import java.util.List;

/**
 * Created by Sunil kumar yadav on 16/4/18.
 */

public class TotalTaxModel extends BaseModel {

    float tax_percent;
    float tax_amount;
    List<TaxModel> taxes;

    public float getTax_percent () {
        return tax_percent;
    }

    public void setTax_percent (float tax_percent) {
        this.tax_percent = tax_percent;
    }

    public float getTax_amount () {
        return tax_amount;
    }

    public void setTax_amount (float tax_amount) {
        this.tax_amount = tax_amount;
    }

    public List<TaxModel> getTaxes () {
        return taxes;
    }

    public void setTaxes (List<TaxModel> taxes) {
        this.taxes = taxes;
    }

    public String getTax_amountText () {
        return getValidDecimalFormat(getTax_amount());
    }
}
