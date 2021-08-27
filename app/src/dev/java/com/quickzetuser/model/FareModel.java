package com.quickzetuser.model;

import com.quickzetuser.appBase.AppBaseModel;

import java.util.List;

/**
 * Created by ubuntu on 13/4/18.
 */

public class FareModel extends AppBaseModel {


    private String admin_commission_type;
    private String payble_fare;
    private String fare_rules;
    private String final_payble_fare;
    private float estimated_fare;
    private float remaining_payble_fare;
    private float waiting_charge;
    private float minimum_charge;
    private float per_min_charge;
    private float cancellation_charge;
    private float admin_commission;
    private float km_charges;
    private float applied_fare;
    private float trip_fare;
    private float base_fare;
    private int saetCount;
    private float second_seat_extra_charge;
    private float booking_fee;
    private float time_charges;
    private float toll_fare;
    private float cancellation_fare;
    private float wallet_money;
    private float cash_money;
    private float booking_driver_allowance;
    private float booking_night_allowance;
    private float waiting_charges;
    private float driver_cancellation_charge;

    private NightChargeModel night_charge;
    private PeakHourChargeModel peak_hour_charge;

    private List<PerKmChargeModel> per_km_charge;

    public float getApplied_fare() {
        return applied_fare;
    }

    public void setApplied_fare(float applied_fare) {
        this.applied_fare = applied_fare;
    }

    public float getTime_charges() {
        return time_charges;
    }

    public void setTime_charges(float time_charges) {
        this.time_charges = time_charges;
    }

    public String getPayble_fare() {
        return getValidDecimalFormat(payble_fare);
    }

    public String getFare_rules() {
        return getValidString(fare_rules);
    }

    public String getFinal_payble_fare() {
        return getValidDecimalFormat(final_payble_fare);
    }

    public float getEstimated_fare() {
        return estimated_fare;
    }

    public void setEstimated_fare(float estimated_fare) {
        this.estimated_fare = estimated_fare;
    }

    public void setFinal_payble_fare(String final_payble_fare) {
        this.final_payble_fare = final_payble_fare;
    }

    public float getRemaining_payble_fare() {
        return remaining_payble_fare;
    }

    public void setRemaining_payble_fare(float remaining_payble_fare) {
        this.remaining_payble_fare = remaining_payble_fare;
    }

    public float getToll_fare() {
        return toll_fare;
    }

    public void setToll_fare(float toll_fare) {
        this.toll_fare = toll_fare;
    }

    public float getCancellation_fare() {
        return cancellation_fare;
    }

    public void setCancellation_fare(float cancellation_fare) {
        this.cancellation_fare = cancellation_fare;
    }

    public float getWallet_money() {
        return wallet_money;
    }

    public void setWallet_money(float wallet_money) {
        this.wallet_money = wallet_money;
    }

    public void setPayble_fare(String payble_fare) {
        this.payble_fare = payble_fare;
    }


    public float getWaiting_charge() {
        return waiting_charge;
    }

    public void setWaiting_charge(float waiting_charge) {
        this.waiting_charge = waiting_charge;
    }

    public float getMinimum_charge() {
        return minimum_charge;
    }

    public void setMinimum_charge(float minimum_charge) {
        this.minimum_charge = minimum_charge;
    }

    public float getPer_min_charge() {
        return per_min_charge;
    }

    public void setPer_min_charge(float per_min_charge) {
        this.per_min_charge = per_min_charge;
    }

    public float getCancellation_charge() {
        return cancellation_charge;
    }

    public void setCancellation_charge(float cancellation_charge) {
        this.cancellation_charge = cancellation_charge;
    }

    public float getAdmin_commission() {
        return admin_commission;
    }

    public void setAdmin_commission(float admin_commission) {
        this.admin_commission = admin_commission;
    }

    public String getAdmin_commission_type() {
        return getValidString(admin_commission_type);
    }

    public void setAdmin_commission_type(String admin_commission_type) {
        this.admin_commission_type = admin_commission_type;
    }

    public float getKm_charges() {
        return km_charges;
    }

    public void setKm_charges(float km_charges) {
        this.km_charges = km_charges;
    }

    public float getTrip_fare() {
        return trip_fare;
    }

    public void setTrip_fare(float trip_fare) {
        this.trip_fare = trip_fare;
    }

    public float getBase_fare() {
        return base_fare;
    }

    public void setBase_fare(float base_fare) {
        this.base_fare = base_fare;
    }

    public int getSaetCount() {
        return saetCount;
    }

    public void setSaetCount(int saetCount) {
        this.saetCount = saetCount;
    }

    public float getSecond_seat_extra_charge() {
        return second_seat_extra_charge;
    }

    public void setSecond_seat_extra_charge(float second_seat_extra_charge) {
        this.second_seat_extra_charge = second_seat_extra_charge;
    }

    public float getBooking_fee() {
        return booking_fee;
    }

    public void setBooking_fee(float booking_fee) {
        this.booking_fee = booking_fee;
    }

    public PeakHourChargeModel getPeak_hour_charge() {
        return peak_hour_charge;
    }

    public void setPeak_hour_charge(PeakHourChargeModel peak_hour_charge) {
        this.peak_hour_charge = peak_hour_charge;
    }

    public NightChargeModel getNight_charge() {
        return night_charge;
    }

    public void setNight_charge(NightChargeModel night_charge) {
        this.night_charge = night_charge;
    }


    public float getDriver_cancellation_charge() {
        return driver_cancellation_charge;
    }

    public void setDriver_cancellation_charge(float driver_cancellation_charge) {
        this.driver_cancellation_charge = driver_cancellation_charge;
    }

    public List<PerKmChargeModel> getPer_km_charge() {
        return per_km_charge;
    }


    public void setPer_km_charge(List<PerKmChargeModel> per_km_charge) {
        this.per_km_charge = per_km_charge;
    }

    public float getCash_money() {
        return cash_money;
    }

    public void setCash_money(float cash_money) {
        this.cash_money = cash_money;
    }

    public float getBooking_driver_allowance() {
        return booking_driver_allowance;
    }

    public void setBooking_driver_allowance(float booking_driver_allowance) {
        this.booking_driver_allowance = booking_driver_allowance;
    }

    public float getBooking_night_allowance() {
        return booking_night_allowance;
    }

    public void setBooking_night_allowance(float booking_night_allowance) {
        this.booking_night_allowance = booking_night_allowance;
    }


    public float getWaiting_charges() {
        return waiting_charges;
    }

    public void setWaiting_charges(float waiting_charges) {
        this.waiting_charges = waiting_charges;
    }

    public String getSecondSeatCharge() {
        float secondSeatCharge = getSecond_seat_extra_charge() + Float.parseFloat(getPayble_fare());
        return getValidDecimalFormat(secondSeatCharge);
    }

    public String getFinal_payble_fareText() {
        return getValidDecimalFormat(getFinal_payble_fare());
    }

    public String getKm_chargesText() {
        return getValidDecimalFormat(getKm_charges());
    }

    public String getCancellation_fareText() {
        return getValidDecimalFormat(getCancellation_fare());
    }

    public String getTime_chargesText() {
        return getValidDecimalFormat(getTime_charges());
    }

    public String getTrip_fareText() {
        return getValidDecimalFormat(getTrip_fare());
    }

    public String getTax_amountText() {
        return getValidDecimalFormat("");
    }

    public String getBase_fareText() {
        return getValidDecimalFormat(getBase_fare());
    }

    public String getBooking_feeText() {
        return getValidDecimalFormat(getBooking_fee());
    }

    public String getMinimum_chargeText(int seat_no_count) {
        return getValidDecimalFormat(getMinimum_charge() * seat_no_count);
    }

    public String getWallet_moneyText() {
        return getValidDecimalFormat(getWallet_money());
    }

    public String getRemaining_payble_fareText() {
        return getValidDecimalFormat(getRemaining_payble_fare());
    }

    public String getPer_min_chargeText() {
        return getValidDecimalFormat(getPer_min_charge()) + PER_MIN;
    }
}
