package com.quickzetuser.ui.main.booking.rideDetail;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.medy.retrofitwrapper.WebRequest;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.database.tables.BookingTable;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.model.DriverModel;
import com.quickzetuser.model.FareModel;
import com.quickzetuser.model.NightChargeModel;
import com.quickzetuser.model.PeakHourChargeModel;
import com.quickzetuser.model.PerKmChargeModel;
import com.quickzetuser.model.PromoCodeModel;
import com.quickzetuser.model.TotalTaxModel;
import com.quickzetuser.model.TotalTollsModel;
import com.quickzetuser.model.request_model.RatingRequestModel;
import com.quickzetuser.model.request_model.SendBillRequestModel;
import com.quickzetuser.model.web_response.BaseWebServiceModelResponse;
import com.quickzetuser.model.web_response.BookingRouteImageSavedResponseModel;
import com.quickzetuser.model.web_response.RatingResponseModel;
import com.quickzetuser.ui.main.booking.rideDetail.adapter.DriverSuggestionAdapter;
import com.quickzetuser.ui.main.dialog.distancecharges.DistanceFaresDialog;
import com.quickzetuser.ui.main.dialog.tollsdetail.TollsDetailDialog;
import com.quickzetuser.ui.main.sidemenu.myRide.trip_details.TripDetailsFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.utilities.ItemClickSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class RideDetailFragment extends AppBaseFragment {

    private DriverSuggestionAdapter adapter;
    private BookCabModel bookCabModel;
    private LinearLayout ll_hide_fares;
    private TextView tv_booking_id;
    private TextView tv_booking_status;
    private TextView tv_license_plate_no;
    private TextView tv_pickup_address;
    private TextView tv_drop_address;
    private LinearLayout ll_start_time;
    private TextView tv_start_time;
    private LinearLayout ll_booking_time;
    private TextView tv_booking_time;
    private LinearLayout ll_end_time;
    private TextView tv_end_time;
    private LinearLayout ll_cancel_time;
    private TextView tv_cancel_time;
    private TextView tv_travel_time;
    private TextView tv_final_payable_fare;
    private TextView tv_total_distances;
    private TextView tv_seat_number;
    private LinearLayout ll_send_invoice;
    private RelativeLayout rl_waiting;
    private TextView tv_waiting_charge_text;
    private TextView tv_waiting_charge;
    private TextView tv_base_fare;
    private TextView tv_distance;
    private TextView tv_time;
    private TextView tv_booking_fee;
    private RelativeLayout rl_peak_hour;
    private TextView tv_peak_charge;
    private RelativeLayout rl_night_charge;
    private TextView tv_night_charge;
    private TextView tv_ride_fare;
    private RelativeLayout rl_minimum_fare;
    private TextView tv_minimum_fare;
    private RelativeLayout rl_driver_allowance;
    private TextView tv_driver_allowance;
    private RelativeLayout rl_night_allowance;
    private TextView tv_night_allowance;
    private RelativeLayout rl_tolls_fare;
    private TextView tv_tolls_fare;
    private TextView tv_taxes;
    private TextView tv_payable_fare;
    private LinearLayout ll_wallet;
    private TextView tv_wallet_money;
    private TextView tv_final_payable_fare_small;
    private RelativeLayout rl_cancellation_fare;
    private TextView tv_cancellation_fare;
    private LinearLayout ll_payment_remaining;
    private TextView tv_payment_remaining;
    private TextView tv_payment_pay;
    private TripDetailsFragment.PayPaymentListener paymentListener;
    private ImageView iv_route_image;

    private RelativeLayout rl_route;
    private RelativeLayout rl_promocode;
    private TextView tv_promocode;
    private ProgressBar pb_route;
    private ImageView iv_status_img;

    private ImageView iv_driver_image;
    private TextView tv_driver_name;
    private RatingBar driver_RatingBar;
    private RecyclerView recycler_view;
    private EditText et_coment;

    private TextView tv_submit;

    private RelativeLayout rl_distance_fare;
    private ImageView iv_distance_info;
    private RelativeLayout rl_booking_fee;

    private LinearLayout ll_package;
    private TextView tv_package_title;
    private TextView tv_package_name;
    private RelativeLayout rl_time_charges;

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_ride_details;
    }

    @Override
    public void initializeComponent() {
        super.initializeComponent();
        initView();
        initializeRecyclerView();
        setData();
        loadRouteImage();

    }

    private void initView() {
        ll_hide_fares = getView().findViewById(R.id.ll_hide_fares);
        tv_booking_id = getView().findViewById(R.id.tv_booking_id);
        tv_booking_status = getView().findViewById(R.id.tv_booking_status);
        tv_license_plate_no = getView().findViewById(R.id.tv_license_plate_no);
        ll_start_time = getView().findViewById(R.id.ll_start_time);
        tv_start_time = getView().findViewById(R.id.tv_start_time);
        ll_booking_time = getView().findViewById(R.id.ll_booking_time);
        tv_booking_time = getView().findViewById(R.id.tv_booking_time);
        tv_pickup_address = getView().findViewById(R.id.tv_pickup_address);
        ll_end_time = getView().findViewById(R.id.ll_end_time);
        tv_end_time = getView().findViewById(R.id.tv_end_time);
        ll_cancel_time = getView().findViewById(R.id.ll_cancel_time);
        tv_cancel_time = getView().findViewById(R.id.tv_cancel_time);
        tv_drop_address = getView().findViewById(R.id.tv_drop_address);
        tv_travel_time = getView().findViewById(R.id.tv_travel_time);
        tv_final_payable_fare = getView().findViewById(R.id.tv_final_payable_fare);
        tv_total_distances = getView().findViewById(R.id.tv_total_distances);
        tv_seat_number = getView().findViewById(R.id.tv_seat_number);
        ll_send_invoice = getView().findViewById(R.id.ll_send_invoice);
        rl_waiting = getView().findViewById(R.id.rl_waiting);
        tv_waiting_charge_text = getView().findViewById(R.id.tv_waiting_charge_text);
        tv_waiting_charge = getView().findViewById(R.id.tv_waiting_charge);
        tv_base_fare = getView().findViewById(R.id.tv_base_fare);
        tv_distance = getView().findViewById(R.id.tv_distance);
        tv_time = getView().findViewById(R.id.tv_time);
        tv_booking_fee = getView().findViewById(R.id.tv_booking_fee);
        rl_peak_hour = getView().findViewById(R.id.rl_peak_hour);
        tv_peak_charge = getView().findViewById(R.id.tv_peak_charge);
        rl_night_charge = getView().findViewById(R.id.rl_night_charge);
        tv_night_charge = getView().findViewById(R.id.tv_night_charge);
        tv_ride_fare = getView().findViewById(R.id.tv_ride_fare);
        rl_minimum_fare = getView().findViewById(R.id.rl_minimum_fare);
        tv_minimum_fare = getView().findViewById(R.id.tv_minimum_fare);
        rl_driver_allowance = getView().findViewById(R.id.rl_driver_allowance);
        tv_driver_allowance = getView().findViewById(R.id.tv_driver_allowance);
        rl_night_allowance = getView().findViewById(R.id.rl_night_allowance);
        tv_night_allowance = getView().findViewById(R.id.tv_night_allowance);
        rl_tolls_fare = getView().findViewById(R.id.rl_tolls_fare);
        rl_tolls_fare.setOnClickListener(this);
        tv_tolls_fare = getView().findViewById(R.id.tv_tolls_fare);
        tv_taxes = getView().findViewById(R.id.tv_taxes);
        tv_payable_fare = getView().findViewById(R.id.tv_payable_fare);
        ll_wallet = getView().findViewById(R.id.ll_wallet);
        tv_wallet_money = getView().findViewById(R.id.tv_wallet_money);
        tv_final_payable_fare_small = getView().findViewById(R.id.tv_final_payable_fare_small);
        rl_cancellation_fare = getView().findViewById(R.id.rl_cancellation_fare);
        tv_cancellation_fare = getView().findViewById(R.id.tv_cancellation_fare);
        ll_payment_remaining = getView().findViewById(R.id.ll_payment_remaining);
        tv_payment_remaining = getView().findViewById(R.id.tv_payment_remaining);
        tv_payment_pay = getView().findViewById(R.id.tv_payment_pay);

        iv_route_image = getView().findViewById(R.id.iv_route_image);
        pb_route = getView().findViewById(R.id.pb_route);
        rl_route = getView().findViewById(R.id.rl_route);
        iv_status_img = getView().findViewById(R.id.iv_status_img);

        rl_promocode = getView().findViewById(R.id.rl_promocode);
        tv_promocode = getView().findViewById(R.id.tv_promocode);

        iv_distance_info = getView().findViewById(R.id.iv_distance_info);
        rl_distance_fare = getView().findViewById(R.id.rl_distance_fare);
        rl_booking_fee = getView().findViewById(R.id.rl_booking_fee);
        ll_package = getView().findViewById(R.id.ll_package);
        tv_package_title = getView().findViewById(R.id.tv_package_title);
        tv_package_name = getView().findViewById(R.id.tv_package_name);
        rl_time_charges = getView().findViewById(R.id.rl_time_charges);

        ll_send_invoice.setOnClickListener(this);

        tv_payment_pay.setOnClickListener(this);
        updateViewVisibility(rl_promocode, View.GONE);
        updateViewVisibility(rl_tolls_fare, View.GONE);
        updateViewVisibility(iv_distance_info, View.GONE);
        updateViewVisibility(rl_booking_fee, View.GONE);
        updateViewVisibility(ll_package, View.GONE);


        iv_driver_image = getView().findViewById(R.id.iv_driver_image);
        tv_driver_name = getView().findViewById(R.id.tv_driver_name);
        driver_RatingBar = getView().findViewById(R.id.driver_RatingBar);
        recycler_view = getView().findViewById(R.id.recycler_view);
        tv_submit = getView().findViewById(R.id.tv_submit);
        et_coment = getView().findViewById(R.id.et_coment);
        tv_submit.setOnClickListener(this);
    }

    private void loadRouteImage() {
        if (!isValidObject(bookCabModel)) return;
        if (getActivity() == null) return;
        String travel_route_map = bookCabModel.getTravel_route_map();
        if (!isValidString(travel_route_map)) {
            try {
                getMainActivity().getWebRequestHelper().
                        bookingRouteImageSave(bookCabModel.getBooking_id(), this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {

            Picasso.get()
                    .load(travel_route_map)
                    .placeholder(R.drawable.map_grid_bg)
                    .error(R.drawable.map_grid_bg)
                    .into(iv_route_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            updateViewVisibility(pb_route, View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            updateViewVisibility(pb_route, View.GONE);
                        }
                    });

        }
    }

    private void setData() {
        if (bookCabModel == null) return;
        String booking_id_text = "Booking ID-" + bookCabModel.getBooking_id();
        tv_booking_id.setText(booking_id_text);

        if (bookCabModel.isSharingBooking()) {
            tv_seat_number.setVisibility(View.VISIBLE);
            tv_seat_number.setText("Seat No : " + bookCabModel.getSeat_no());
        } else {
            tv_seat_number.setVisibility(View.GONE);
        }

        if (bookCabModel.isRentalBooking()) {
            tv_package_title.setText(getString(R.string.Rental_Package));
            tv_package_name.setText(bookCabModel.getPackage_name());
            updateViewVisibility(ll_package, View.VISIBLE);
        } else if (bookCabModel.isOutstationBooking()) {
            tv_package_title.setText(getString(R.string.Outstation_Package));
            tv_package_name.setText(bookCabModel.getPackage_name());
            updateViewVisibility(ll_package, View.VISIBLE);
        }
        if (bookCabModel.getStatus() == 5) {
            iv_status_img.setImageResource(R.mipmap.stamp_completed);
            iv_status_img.setColorFilter(getResources().getColor(R.color.color_green));
            updateViewVisibility(ll_hide_fares, View.VISIBLE);
            updateViewVisibility(rl_cancellation_fare, View.GONE);
            updateViewVisibility(ll_start_time, View.VISIBLE);
            updateViewVisibility(ll_end_time, View.VISIBLE);
            updateViewVisibility(ll_booking_time, View.GONE);
            updateViewVisibility(ll_cancel_time, View.GONE);
            updateViewVisibility(tv_license_plate_no, View.VISIBLE);
            if (bookCabModel.getDriver() != null)
                tv_booking_status.setText(bookCabModel.getDriver().getFullName());
            if (bookCabModel.getTaxi() != null)
                tv_license_plate_no.setText(bookCabModel.getTaxi().getLicense_plate_no());
            tv_start_time.setText(bookCabModel.getFormattedBookingStartTime(2));
            tv_end_time.setText(bookCabModel.getFormattedBookingEndTime(2));
            tv_pickup_address.setText(bookCabModel.getStart_address());
            tv_drop_address.setText(bookCabModel.getEnd_address());
        } else {
            iv_status_img.setImageResource(R.mipmap.stamp_cancelled);
            iv_status_img.setColorFilter(getResources().getColor(R.color.red));
            if (bookCabModel.getDriver() == null)
                updateViewVisibility(tv_license_plate_no, View.GONE);
            updateViewVisibility(ll_hide_fares, View.GONE);
            updateViewVisibility(rl_cancellation_fare, View.VISIBLE);
            updateViewVisibility(ll_start_time, View.GONE);
            updateViewVisibility(ll_end_time, View.GONE);
            updateViewVisibility(ll_booking_time, View.VISIBLE);
            updateViewVisibility(ll_cancel_time, View.VISIBLE);
            if (bookCabModel.getTaxi() != null)
                tv_license_plate_no.setText(bookCabModel.getTaxi().getLicense_plate_no());
            tv_booking_time.setText(bookCabModel.getFormattedBookingTime(2));
            tv_cancel_time.setText(bookCabModel.getFormattedBookingCancelTime(2));
            tv_pickup_address.setText(bookCabModel.getPickup_address());
            tv_drop_address.setText(bookCabModel.getDrop_address());
            if (bookCabModel.getStatus() == 6) {
                tv_booking_status.setText(getString(R.string.cancel_by_you));
            } else {
                tv_booking_status.setText(getString(R.string.cancel_by_driver));
            }
        }
        tv_travel_time.setText(bookCabModel.getTimeText());
        tv_total_distances.setText(bookCabModel.getTotal_distanceText());
        FareModel fare = bookCabModel.getFare();
        if (fare != null) {

            float trip_fare = fare.getTrip_fare();
            float applied_fare = fare.getApplied_fare();
            if (trip_fare < applied_fare) {
                updateViewVisibility(rl_minimum_fare, View.VISIBLE);
                String minimum_charge = currency_symbol + fare.getMinimum_chargeText(bookCabModel.getSeat_no_count());
                tv_minimum_fare.setText(minimum_charge);
            } else {
                updateViewVisibility(rl_minimum_fare, View.GONE);
            }

            tv_cancellation_fare.setText(currency_symbol + fare.getCancellation_fareText());
            tv_final_payable_fare.setText(currency_symbol + fare.getPayble_fare());
            if (fare.getWaiting_charges() > 0) {
                updateViewVisibility(rl_waiting, View.VISIBLE);
                tv_waiting_charge_text.setText(getContext().getResources().getString(R.string.waiting_charge)
                        + " (" + bookCabModel.getTotal_waiting_time() + " Min) ");
                tv_waiting_charge.setText(currency_symbol + fare.getWaiting_charges());
            } else {
                updateViewVisibility(rl_waiting, View.GONE);
            }
            tv_base_fare.setText(currency_symbol + fare.getBase_fareText());
            List<PerKmChargeModel> per_km_charge = fare.getPer_km_charge();
            if (per_km_charge != null && per_km_charge.size() > 0) {
                tv_distance.setText(currency_symbol + per_km_charge.get(0).getAmountText());
                if (per_km_charge.size() > 1) {
                    updateViewVisibility(iv_distance_info, View.VISIBLE);
                    rl_distance_fare.setOnClickListener(this);

                } else {
                    updateViewVisibility(iv_distance_info, View.GONE);
                    rl_distance_fare.setOnClickListener(null);
                }
            }
            tv_time.setText(currency_symbol + fare.getPer_min_chargeText());
            if (bookCabModel.isRentalBooking() || bookCabModel.isOutstationBooking()) {
                if (fare.getKm_charges() <= 0)
                    updateViewVisibility(rl_distance_fare, View.GONE);
                if (fare.getTime_charges() <= 0)
                    updateViewVisibility(rl_time_charges, View.GONE);
            }
            if (fare.getBooking_fee() > 0) {
                updateViewVisibility(rl_booking_fee, View.VISIBLE);
                tv_booking_fee.setText(currency_symbol + fare.getBooking_feeText());
            } else {
                updateViewVisibility(rl_booking_fee, View.GONE);

            }

            PeakHourChargeModel peak_hour_charge = fare.getPeak_hour_charge();
            if (peak_hour_charge != null) {
                updateViewVisibility(rl_peak_hour, View.VISIBLE);
                tv_peak_charge.setText(currency_symbol + peak_hour_charge.getPeak_hour_charge_amountText());
            } else {
                updateViewVisibility(rl_peak_hour, View.GONE);
            }

            NightChargeModel night_charge = fare.getNight_charge();
            if (night_charge != null) {
                updateViewVisibility(rl_night_charge, View.VISIBLE);
                tv_night_charge.setText(currency_symbol + night_charge.getNight_charge_amountText());
            } else {
                updateViewVisibility(rl_night_charge, View.GONE);
            }
            TotalTollsModel total_tolls = bookCabModel.getTotal_tolls();
            if (isValidObject(total_tolls) && total_tolls.getToll_fare() > 0) {
                updateViewVisibility(rl_tolls_fare, View.VISIBLE);
                tv_tolls_fare.setText(currency_symbol + total_tolls.getToll_fareText());
            } else {
                updateViewVisibility(rl_tolls_fare, View.GONE);
            }

            tv_ride_fare.setText(currency_symbol + fare.getTrip_fareText());
            tv_minimum_fare.setText(currency_symbol + fare.getMinimum_chargeText(bookCabModel.getSeat_no_count()));

            if (fare.getRemaining_payble_fare() > 0) {
                updateViewVisibility(ll_payment_remaining, View.VISIBLE);
                tv_payment_remaining.setText(currency_symbol + fare.getFinal_payble_fareText());
            } else {
                updateViewVisibility(ll_payment_remaining, View.GONE);
            }

            if (fare.getBooking_driver_allowance() > 0) {
                updateViewVisibility(rl_driver_allowance, View.VISIBLE);
                tv_driver_allowance.setText(currency_symbol + fare.getBooking_driver_allowance());
            } else {
                updateViewVisibility(rl_driver_allowance, View.GONE);
            }

            if (fare.getBooking_night_allowance() > 0) {
                updateViewVisibility(rl_night_allowance, View.VISIBLE);
                tv_night_allowance.setText(currency_symbol + fare.getBooking_night_allowance());
            } else {
                updateViewVisibility(rl_night_allowance, View.GONE);
            }
        }

        TotalTaxModel total_tax = bookCabModel.getTotal_tax();
        if (total_tax != null) {
            tv_taxes.setText(currency_symbol + total_tax.getTax_amountText());
        }

        tv_payable_fare.setText(currency_symbol + fare.getPayble_fare());
        float wallet_money = fare.getWallet_money();
        if (wallet_money > 0) {
            updateViewVisibility(ll_wallet, View.VISIBLE);
            tv_wallet_money.setText(currency_symbol + fare.getWallet_moneyText());
        } else {
            updateViewVisibility(ll_wallet, View.GONE);
        }
        tv_final_payable_fare_small.setText(currency_symbol + fare.getFinal_payble_fareText());

        PromoCodeModel promocode = bookCabModel.getPromocode();
        if (promocode != null) {
            updateViewVisibility(rl_promocode, View.VISIBLE);
            tv_promocode.setText(currency_symbol + promocode.getPromocode_amount());
        } else {
            updateViewVisibility(rl_promocode, View.GONE);
        }

        DriverModel driver = bookCabModel.getDriver();
        if (driver != null) {
            Picasso.get().load(driver.getImage()).into(iv_driver_image);
            tv_driver_name.setText(driver.getFullName());
            driver_RatingBar.setRating(3);
        }

    }

    private void initializeRecyclerView() {
        final List<String> data = getData();
        adapter = new DriverSuggestionAdapter(getActivity(), data);
        recycler_view.setLayoutManager(getFullHeightLinearLayoutManager());
        recycler_view.setAdapter(adapter);

        ItemClickSupport.addTo(recycler_view).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                String suggestion = data.get(position);
                if (adapter.getMap().containsKey(position)) {
                    adapter.getMap().remove(position);
                } else {
                    adapter.getMap().put(position, suggestion);
                }
                adapter.notifyItemChanged(position);
            }
        });
    }

    public List<String> getData() {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.polite_and_professional_driver));
        list.add(getString(R.string.on_time_pickup));
        list.add(getString(R.string.driver_familiar_with_route));
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_submit:
                onSubmit();
                break;
            case R.id.ll_send_invoice:
                callSendInVoice();
                break;
            case R.id.rl_tolls_fare:
                showTollsDetail();
                break;
            case R.id.rl_distance_fare:
                showDistanceFaresDialog();
                break;
        }
    }

    private void showDistanceFaresDialog() {
        FareModel fareModel = bookCabModel.getFare();
        List<PerKmChargeModel> per_km_charge = fareModel.getPer_km_charge();
        if (isValidObject(per_km_charge) && per_km_charge.size() > 0) {
            DistanceFaresDialog tollsDetailDialog = new DistanceFaresDialog();
            tollsDetailDialog.setPer_km_charge(per_km_charge);
            tollsDetailDialog.setTitle(getString(R.string.distance_fare));
            try {
                tollsDetailDialog.show(getMainActivity().getFm(), tollsDetailDialog.getClass().getSimpleName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    private void callSendInVoice() {
        if (bookCabModel == null) return;
        SendBillRequestModel requestModel = new SendBillRequestModel();
        requestModel.booking_id = bookCabModel.getBooking_id();
        displayProgressBar(false);
        getWebRequestHelper().sendBill(requestModel, this);
    }


    private void onSubmit() {
        if (bookCabModel == null) return;
        DriverModel driver = bookCabModel.getDriver();
        if (driver == null) return;
        RatingRequestModel requestModel = new RatingRequestModel();
        Map<Integer, String> map = adapter.getMap();
        String suggestion = "";
        if (map != null && map.size() > 0) {
            Collection<String> strings = map.values();

            for (String s : strings) {
                if (suggestion.isEmpty()) {
                    suggestion = s;
                } else {
                    suggestion += ", " + s;
                }
            }
        }

        String coment = et_coment.getText().toString().trim();
        if (!coment.isEmpty()) {
            if (suggestion.isEmpty()) {
                suggestion = coment;
            } else {
                suggestion += ", " + coment;
            }
        }

        requestModel.booking_id = bookCabModel.getBooking_id();
        requestModel.rating = driver_RatingBar.getRating();
        requestModel.reviews = suggestion;
        requestModel.driver_id = driver.getDriver_id();
        displayProgressBar(false);
        getWebRequestHelper().give_rating(requestModel, this);
//        } else {
//            showErrorMessage("Please select suggestion");
//        }
    }

    private void showTollsDetail() {
        TotalTollsModel total_tolls = bookCabModel.getTotal_tolls();
        if (isValidObject(total_tolls) && isValidObject(total_tolls.getTolls())
                && total_tolls.getTolls().size() > 0) {
            TollsDetailDialog tollsDetailDialog = new TollsDetailDialog();
            tollsDetailDialog.setTotalTollsModel(total_tolls);
            tollsDetailDialog.setTitle(getString(R.string.Tolls_Detail));

            try {
                tollsDetailDialog.show(getMainActivity().getFm(), tollsDetailDialog.getClass().getSimpleName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onWebRequestCall(WebRequest webRequest) {
        try {
            getMainActivity().onWebRequestCall(webRequest);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        dismissProgressBar();
        super.onWebRequestResponse(webRequest);
        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_GIVE_RATING:
                    handleRatingResponse(webRequest);
                    break;
                case ID_SEND_BILL:
                    handleSendBillResponse(webRequest);
                    break;
                case ID_BOOKING_ROUTE_IMAGE_SAVE:
                    handleSaveRouteImageResponse(webRequest);
                    break;
            }
        } else {
            String errorMessageFromResponse = webRequest.getErrorMessageFromResponse();
            if (isValidString(errorMessageFromResponse)) {
                showErrorMessage(errorMessageFromResponse);
            }
        }
    }

    private void handleSaveRouteImageResponse(WebRequest webRequest) {

        BookingRouteImageSavedResponseModel responseModel = webRequest.getResponsePojo(BookingRouteImageSavedResponseModel.class);
        if (isValidObject(responseModel) && bookCabModel != null) {
            bookCabModel.setTravel_route_map(responseModel.getData());
            loadRouteImage();
        }

    }

    private void handleSendBillResponse(WebRequest webRequest) {

        BaseWebServiceModelResponse responseModel = webRequest.getResponsePojo(BaseWebServiceModelResponse.class);
        if (isValidObject(responseModel) && bookCabModel != null) {
            showToast(responseModel.getMessage());
        }
    }


    private void handleRatingResponse(WebRequest webRequest) {
        RatingResponseModel responseModel = webRequest.getResponsePojo(RatingResponseModel.class);
        if (responseModel == null) return;
        BookingTable.getInstance().deleteBooking(bookCabModel.getBooking_id());
        String message = responseModel.getMessage();
        showCustomToast(message);
        getActivity().onBackPressed();
    }

    public void setBookCabModel(BookCabModel bookCabModel) {
        this.bookCabModel = bookCabModel;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getMainActivity().updateHeadService(true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getMainActivity().updateHeadService(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
