package com.quickzetuser.model;

public class  RentalFareModel extends FareModel {


    private TotalTaxModel total_tax;
    private VehicleTypeModel vehicle;
    private PromoCodeModel promocode;


    public TotalTaxModel getTotal_tax() {
        return total_tax;
    }

    public void setTotal_tax(TotalTaxModel total_tax) {
        this.total_tax = total_tax;
    }

    public VehicleTypeModel getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleTypeModel vehicle) {
        this.vehicle = vehicle;
    }

    public PromoCodeModel getPromocode() {
        return promocode;
    }

    public void setPromocode(PromoCodeModel promocode) {
        this.promocode = promocode;
    }

    @Override
    public String toString() {
        return getVehicle().getName();
    }
}
