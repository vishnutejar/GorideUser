package com.quickzetuser.ui.main.dialog.runningBooking;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.medy.retrofitwrapper.WebRequest;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseDialogFragment;
import com.quickzetuser.database.tables.BookingTable;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.ui.main.MainActivity;
import com.quickzetuser.ui.main.dialog.cancelBooking.CancelBookingDialog;
import com.quickzetuser.ui.main.dialog.runningBooking.adapter.RunningBookingAdapter;
import com.utilities.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sunil kumar yadav on 15/3/18.
 */

@SuppressLint("ValidFragment")
public class RunningBookingDialog extends AppBaseDialogFragment {

    MainActivity _activity;
    ConfirmationDialogListener confirmationDialogListener;
    private RecyclerView recycler_view;
    private RunningBookingAdapter adapter;
    private List<BookCabModel> list = new ArrayList<>();


    public RunningBookingDialog(MainActivity mainActivity) {
        this._activity = mainActivity;
    }


    public void setConfirmationDialogListener (ConfirmationDialogListener confirmationDialogListener) {
        this.confirmationDialogListener = confirmationDialogListener;
    }

    @Override
    public int getLayoutResourceId () {
        return R.layout.dialog_running_booking;
    }

    @Override
    public void initializeComponent () {

        recycler_view = getView().findViewById(R.id.recycler_view);
        initializeRecyclerView();

        getCurrentBookingFromDB();
    }

    @Override
    public int getFragmentContainerResourceId () {
        return -1;
    }


    private void initializeRecyclerView () {
        adapter = new RunningBookingAdapter(getActivity(), list);
        recycler_view.setLayoutManager(getVerticalLinearLayoutManager());
        recycler_view.setAdapter(adapter);

        ItemClickSupport.addTo(recycler_view).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked (RecyclerView recyclerView, int position, View v) {
                BookCabModel bookCabModel = list.get(position);
                if (bookCabModel == null) return;
                try {
                    if (bookCabModel.getStatus() < 1) {
                        if (bookCabModel.isScheduleBooking()) {
                            if (v.getId() == R.id.iv_delete) {
                                addCancelBookingDialog(bookCabModel);
                                return;
                            }

                        } else {
                            dismiss();
                            getMainActivity().getMapHandler().showConfirmBookingWaitingFragment(bookCabModel);
                        }
                    } else if (bookCabModel.getStatus() < 5) {
                        dismiss();
                        getMainActivity().getMapHandler().showConfirmBookingFragment(bookCabModel);
                    } else {
                        dismiss();
                        getMainActivity().clearFragmentBackStack();
                        getMainActivity().getMapHandler().showRideDetailFragment(bookCabModel, false);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void setupDialog (Dialog dialog, int style) {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflate = LayoutInflater.from(getActivity());
        View layout = inflate.inflate(getLayoutResourceId(), null);

        //set dialog view
        dialog.setContentView(layout);
        //setup dialog window param
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.windowAnimations = R.style.BadeDialogStyle;
        wlmp.gravity = Gravity.CENTER;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setTitle(null);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }


    @Override
    public void onWebRequestCall (WebRequest webRequest) {
        _activity.onWebRequestCall(webRequest);
    }

    public void getCurrentBookingFromDB() {
        List<BookCabModel> bookCabList = BookingTable.getInstance().getRunningBooking();
        list.clear();
        list.addAll(bookCabList);
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }

    public interface ConfirmationDialogListener {
        void onClickConfirm (DialogFragment dialogFragment, BookCabModel data);
    }


    private void addCancelBookingDialog (final BookCabModel bookCabModel) {
        CancelBookingDialog cancelBookingDialog = CancelBookingDialog.getNewInstance("");
        cancelBookingDialog.setBookCabModel(bookCabModel);
        cancelBookingDialog.setConfirmationDialogListener(new CancelBookingDialog.ConfirmationDialogListener() {
            @Override
            public void onClickConfirm (DialogFragment dialogFragment, BookCabModel data) {
                dialogFragment.dismiss();
                BookingTable.getInstance().deleteBooking(bookCabModel.getBooking_id());
                handleBookingDeleteSuccess(bookCabModel);
            }
        });
        cancelBookingDialog.show(getActivity().getSupportFragmentManager(), cancelBookingDialog.getClass().getSimpleName());
    }

    private void handleBookingDeleteSuccess (BookCabModel bookCabModel) {
        int index = list.indexOf(bookCabModel);
        if (index == -1) return;
        list.remove(index);
        if (list.size() == 0) {
            dismiss();
            return;
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void dismiss () {
        _activity.updateHeadService(false);
        super.dismiss();
    }
}
