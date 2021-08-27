package com.quickzetuser.ui.main.booking.fareEstimateOutstation;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.distancematrix.DistanceMatrixHandler;
import com.distancematrix.DistanceMatrixModel;
import com.models.BaseModel;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.model.OutstationFareModel;
import com.quickzetuser.model.OutstationPackageModel;
import com.quickzetuser.model.TotalTaxModel;
import com.quickzetuser.model.VehicleTypeModel;
import com.quickzetuser.ui.main.dialog.paymentmode.PaymentModeDialog;
import com.quickzetuser.ui.utilities.MapHandler;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class FareEstimateOutstationDetailFragment extends AppBaseFragment implements
        DistanceMatrixHandler.DistanceMatrixListener {

    public String PAYMENT_MODE = PAYMENT_METHOD;

    private TextView tv_pickup_address;
    private TextView tv_drop_address;

    LinearLayout ll_leaveon;
    TextView tv_leaveon_date;
    LinearLayout ll_returnby;
    TextView tv_returnby_date;

    ImageView iv_vehicle_type;
    TextView tv_vehicle_name;
    TextView tv_seat_number;
    TextView tv_trip_detail;
    TextView tv_per_km_fare;
    TextView tv_fare_estimate;
    TextView tv_see_fare_detail;
    LinearLayout ll_faredetail;
    TextView tv_base_fare_desc;
    TextView tv_base_fare;
    RelativeLayout rl_remain_km_fare;
    TextView tv_remain_km_desc;
    TextView tv_remain_km_charge;
    TextView tv_tax_fee_charge;
    LinearLayout ll_driver_allowance;
    TextView tv_driver_allowance;
    LinearLayout ll_night_allowance;
    TextView tv_night_allowance;
    LinearLayout ll_taxes;
    TextView tv_taxes;
    TextView tv_fare_estimate_detail;
    TextView tv_fare_rules;

    private LinearLayout ll_promocode;
    private ImageView iv_cancel_promocode;
    private TextView tv_promocode;

    private LinearLayout ll_payment;
    private ImageView iv_payment_icon;
    private TextView tv_payment_mode;

    private TextView tv_ride_now;


    public MapHandler getMapHandler() {
        MapHandler mapHandler = null;
        try {
            mapHandler = getMainActivity().getMapHandler();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return mapHandler;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_fare_estimate_outstation_detail;
    }

    public boolean isOnewaySelected() {
        return ((FareEstimateOutstationFragment) getParentFragment()).isOnewaySelected();
    }

    public int getSelectedFarePosition() {
        return ((FareEstimateOutstationFragment) getParentFragment()).getSelectedFarePosition();
    }


    @Override
    public void initializeComponent() {
        super.initializeComponent();
        tv_pickup_address = getView().findViewById(R.id.tv_pickup_address);
        tv_drop_address = getView().findViewById(R.id.tv_drop_address);

        ll_leaveon = getView().findViewById(R.id.ll_leaveon);
        tv_leaveon_date = getView().findViewById(R.id.tv_leaveon_date);
        ll_returnby = getView().findViewById(R.id.ll_returnby);
        tv_returnby_date = getView().findViewById(R.id.tv_returnby_date);

        iv_vehicle_type = getView().findViewById(R.id.iv_vehicle_type);
        tv_vehicle_name = getView().findViewById(R.id.tv_vehicle_name);
        tv_seat_number = getView().findViewById(R.id.tv_seat_number);
        tv_trip_detail = getView().findViewById(R.id.tv_trip_detail);

        tv_per_km_fare = getView().findViewById(R.id.tv_per_km_fare);
        tv_fare_estimate = getView().findViewById(R.id.tv_fare_estimate);
        tv_see_fare_detail = getView().findViewById(R.id.tv_see_fare_detail);
        ll_faredetail = getView().findViewById(R.id.ll_faredetail);
        updateViewVisibility(ll_faredetail, View.GONE);
        tv_base_fare_desc = getView().findViewById(R.id.tv_base_fare_desc);
        tv_base_fare = getView().findViewById(R.id.tv_base_fare);
        rl_remain_km_fare = getView().findViewById(R.id.rl_remain_km_fare);
        tv_remain_km_desc = getView().findViewById(R.id.tv_remain_km_desc);
        tv_remain_km_charge = getView().findViewById(R.id.tv_remain_km_charge);
        tv_tax_fee_charge = getView().findViewById(R.id.tv_tax_fee_charge);
        ll_driver_allowance = getView().findViewById(R.id.ll_driver_allowance);
        tv_driver_allowance = getView().findViewById(R.id.tv_driver_allowance);
        ll_night_allowance = getView().findViewById(R.id.ll_night_allowance);
        tv_night_allowance = getView().findViewById(R.id.tv_night_allowance);
        ll_taxes = getView().findViewById(R.id.ll_taxes);
        tv_taxes = getView().findViewById(R.id.tv_taxes);
        tv_fare_estimate_detail = getView().findViewById(R.id.tv_fare_estimate_detail);
        tv_fare_rules = getView().findViewById(R.id.tv_fare_rules);

        ll_promocode = getView().findViewById(R.id.ll_promocode);
        iv_cancel_promocode = getView().findViewById(R.id.iv_cancel_promocode);
        tv_promocode = getView().findViewById(R.id.tv_promocode);

        ll_payment = getView().findViewById(R.id.ll_payment);
        iv_payment_icon = getView().findViewById(R.id.iv_payment_icon);
        tv_payment_mode = getView().findViewById(R.id.tv_payment_mode);
        tv_ride_now = getView().findViewById(R.id.tv_ride_now);


        tv_see_fare_detail.setOnClickListener(this);
        ll_promocode.setOnClickListener(this);
        iv_cancel_promocode.setOnClickListener(this);
        ll_payment.setOnClickListener(this);
        tv_ride_now.setOnClickListener(this);

        setFareData();
    }


    private void setFareData() {
        BookCabModel fareEstimateModel = getMapHandler().fareEstimateModel;
        if (fareEstimateModel != null) {
            tv_pickup_address.setText(fareEstimateModel.getPickup_address());
            tv_drop_address.setText(fareEstimateModel.getDrop_address());

            tv_leaveon_date.setText(fareEstimateModel.getFormattedBookingTime(BaseModel.TAG_TWO));
            tv_returnby_date.setText(fareEstimateModel.getFormattedRetrunByTime(BaseModel.TAG_TWO));

            OutstationPackageModel outstation = fareEstimateModel.getOutstation();
            if (outstation != null) {
                OutstationFareModel outstationFareModel = outstation.getFares().get(getSelectedFarePosition());
                if (outstationFareModel != null) {
                    VehicleTypeModel vehicleTypeModel = outstationFareModel.getVehicle();
                    tv_vehicle_name.setText(vehicleTypeModel.getName());
                    tv_seat_number.setText(vehicleTypeModel.getSeating_capacity() + " Seater");
                    Picasso.get()
                            .load(vehicleTypeModel.getImage_selected())
                            .placeholder(R.mipmap.ic_riksya)
                            .error(R.mipmap.ic_riksya)
                            .into(iv_vehicle_type);

                    OutstationFareModel.FareData onewayfare = outstationFareModel.getOnewayfare();
                    OutstationFareModel.FareData roundtripfare = outstationFareModel.getRoundtripfare();

                    if (isOnewaySelected()) {
                        updateViewVisibility(ll_returnby, View.GONE);
                        updateViewVisibility(tv_per_km_fare, View.GONE);
                        tv_trip_detail.setText(fareEstimateModel.getOnewayMessageDetail());

                        tv_fare_estimate.setText(currency_symbol.trim() + onewayfare.getPayble_fare());
                        tv_fare_estimate_detail.setText(currency_symbol.trim() + onewayfare.getPayble_fare());
                        tv_fare_rules.setText(Html.fromHtml(onewayfare.getFare_rules()));

                        tv_base_fare_desc.setText(onewayfare.getBaseKmText() + ", " + fareEstimateModel.getTimeText());
                        tv_base_fare.setText(currency_symbol.trim() + onewayfare.getBase_fareText());

                        tv_remain_km_desc.setText(onewayfare.getExtraKmText() + " x " +
                                currency_symbol.trim() + onewayfare.getPer_km_charge().get(0).getAmount() +
                                " (charged only if travelled)");
                        tv_remain_km_charge.setText(currency_symbol.trim() + onewayfare.getKm_chargesText());


                        TotalTaxModel totalTaxModel = onewayfare.getTotal_tax();
                        float fees = totalTaxModel.getTax_amount() + onewayfare.getDriver_allounce() + onewayfare.getNight_allounce();
                        tv_tax_fee_charge.setText(currency_symbol.trim() + onewayfare.getValidDecimalFormat(fees));

                        tv_driver_allowance.setText(currency_symbol.trim() + onewayfare.getDriver_allounceText());
                        tv_night_allowance.setText(currency_symbol.trim() + onewayfare.getNight_allounceText());
                        tv_taxes.setText(currency_symbol.trim() + totalTaxModel.getTax_amountText());


                        if (onewayfare.getExtra_km() > 0) {
                            updateViewVisibility(rl_remain_km_fare, View.VISIBLE);
                        } else {
                            updateViewVisibility(rl_remain_km_fare, View.GONE);
                        }

                        if (onewayfare.getDriver_allounce() > 0) {
                            updateViewVisibility(ll_driver_allowance, View.VISIBLE);
                        } else {
                            updateViewVisibility(ll_driver_allowance, View.GONE);
                        }
                        if (onewayfare.getNight_allounce() > 0) {
                            updateViewVisibility(ll_night_allowance, View.VISIBLE);
                        } else {
                            updateViewVisibility(ll_night_allowance, View.GONE);
                        }

                    } else {
                        updateViewVisibility(ll_returnby, View.VISIBLE);
                        updateViewVisibility(tv_per_km_fare, View.VISIBLE);
                        tv_trip_detail.setText(fareEstimateModel.getRoundtripMessageDetail());

                        tv_per_km_fare.setText(
                                currency_symbol.trim() + roundtripfare.getPer_km_charge().get(0).getAmountText());

                        tv_fare_estimate.setText(currency_symbol.trim() + roundtripfare.getPayble_fare());
                        tv_fare_estimate_detail.setText(currency_symbol.trim() + roundtripfare.getPayble_fare());
                        tv_fare_rules.setText(Html.fromHtml(roundtripfare.getFare_rules()));

                        tv_base_fare_desc.setText(roundtripfare.getBaseKmText() + " x " + roundtripfare.getPer_km_charge().get(0).getAmount());
                        tv_base_fare.setText(currency_symbol.trim() + roundtripfare.getBase_fareText());

                        tv_remain_km_desc.setText(roundtripfare.getExtraKmText() + " x " +
                                currency_symbol.trim() + roundtripfare.getPer_km_charge().get(0).getAmount() +
                                " (charged only if travelled)");
                        tv_remain_km_charge.setText(currency_symbol.trim() + roundtripfare.getKm_chargesText());


                        TotalTaxModel totalTaxModel = roundtripfare.getTotal_tax();
                        float fees = totalTaxModel.getTax_amount() + roundtripfare.getDriver_allounce() + roundtripfare.getNight_allounce();
                        tv_tax_fee_charge.setText(currency_symbol.trim() + roundtripfare.getValidDecimalFormat(fees));

                        tv_driver_allowance.setText(currency_symbol.trim() + roundtripfare.getDriver_allounceText());
                        tv_night_allowance.setText(currency_symbol.trim() + roundtripfare.getNight_allounceText());
                        tv_taxes.setText(currency_symbol.trim() + totalTaxModel.getTax_amountText());


                        if (roundtripfare.getExtra_km() > 0) {
                            updateViewVisibility(rl_remain_km_fare, View.VISIBLE);
                        } else {
                            updateViewVisibility(rl_remain_km_fare, View.GONE);
                        }

                        if (roundtripfare.getDriver_allounce() > 0) {
                            updateViewVisibility(ll_driver_allowance, View.VISIBLE);
                        } else {
                            updateViewVisibility(ll_driver_allowance, View.GONE);
                        }
                        if (roundtripfare.getNight_allounce() > 0) {
                            updateViewVisibility(ll_night_allowance, View.VISIBLE);
                        } else {
                            updateViewVisibility(ll_night_allowance, View.GONE);
                        }


                    }
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_see_fare_detail:
                updateFareDetail();
                break;
            case R.id.ll_promocode:

                break;
            case R.id.iv_cancel_promocode:

                break;
            case R.id.ll_payment:
                addPaymentModeDialog();
                break;
            case R.id.tv_ride_now:
                if (getParentFragment() != null) {
                    ((FareEstimateOutstationFragment) getParentFragment()).bookCab();
                }
                break;

        }
    }


    private void updateFareDetail() {
        if (ll_faredetail.getVisibility() != View.VISIBLE) {
            updateViewVisibility(ll_faredetail, View.VISIBLE);
            tv_see_fare_detail.setText("Hide fare detail");
        } else {
            updateViewVisibility(ll_faredetail, View.GONE);
            tv_see_fare_detail.setText("See fare detail");
        }
    }


    private void addPaymentModeDialog() {
        int height = tv_ride_now.getHeight();
        PaymentModeDialog paymentModeDialog = PaymentModeDialog.getNewInstance("Payment Mode");
        paymentModeDialog.setBottomMargin(height);
        paymentModeDialog.setConfirmationDialogListener(new PaymentModeDialog.ConfirmationDialogListener() {
            @Override
            public void onClickConfirm(DialogFragment dialogFragment, String paymentMode) {
                PAYMENT_MODE = paymentMode;
                if (PAYMENT_MODE.equalsIgnoreCase("Cash")) {
                    iv_payment_icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_cash));
                } else {
                    iv_payment_icon.setImageDrawable(getResources().getDrawable(R.mipmap.wallet_icon));
                }
                tv_payment_mode.setText(PAYMENT_MODE);
                dialogFragment.dismiss();
            }
        });
        paymentModeDialog.show(getChildFm(), paymentModeDialog.getClass().getSimpleName());
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDistanceMatrixRequestStart() {

    }

    @Override
    public void onDistanceMatrixRequestEnd(List<DistanceMatrixModel> rows) {

    }


}
