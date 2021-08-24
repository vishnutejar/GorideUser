package com.rider.ui.main.dialog.outstationtimepicker;

import android.app.Dialog;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.customviews.WrappingViewPager;
import com.google.android.material.tabs.TabLayout;
import com.rider.R;
import com.rider.appBase.AppBaseDialogFragment;
import com.rider.appBase.AppBaseModel;
import com.rider.appBase.WrappingViewPagerAdapter;
import com.rider.ui.MyApplication;

import java.util.Calendar;

public class OutstationTimeDialog extends AppBaseDialogFragment {

    public static final int OPEN_FOR_ONEWAY = 1;
    public static final int OPEN_FOR_ROUNDTRIP = 2;

    int returnTripMinPackageInMillis = 6 * 60 * 60 * 1000;

    Calendar minDateTimeBooking;
    Calendar startTimeCal;
    Calendar returnByTimeCal;
    TextView tv_dialog_title;
    TextView tv_dialog_subtitle;
    TabLayout tab_layout;

    WrappingViewPager view_pager;
    WrappingViewPagerAdapter viewPagerAdapter;

    OutstationTimeDialogListener outstationTimeDialogListener;
    int defaultPagePosition;
    int openFor = OPEN_FOR_ONEWAY;
    AppBaseModel appBaseModel = new AppBaseModel();

    public void setStartTimeCal(Calendar startTimeCal) {
        this.startTimeCal = startTimeCal;
    }

    public void setReturnByTimeCal(Calendar returnByTimeCal) {
        this.returnByTimeCal = returnByTimeCal;
    }

    public void setOutstationTimeDialogListener(OutstationTimeDialogListener outstationTimeDialogListener) {
        this.outstationTimeDialogListener = outstationTimeDialogListener;
    }

    public void setDefaultPagePosition(int defaultPagePosition) {
        this.defaultPagePosition = defaultPagePosition;
    }

    public void setOpenFor(int openFor) {
        this.openFor = openFor;
    }

    public void setReturnTripMinPackageInMillis(int returnTripMinPackageInMillis) {
        this.returnTripMinPackageInMillis = returnTripMinPackageInMillis;
    }

    public int getOpenFor() {
        return openFor;
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
            updateReturnCalender();
            handlePageTitle();
            updateUi();
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    };

    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_outstaion_timepicker;
    }


    @Override
    public void initializeComponent() {
        tv_dialog_title = getView().findViewById(R.id.tv_dialog_title);
        tv_dialog_subtitle = getView().findViewById(R.id.tv_dialog_subtitle);
        tab_layout = getView().findViewById(R.id.tab_layout);
        view_pager = getView().findViewById(R.id.view_pager);
        setupViewPager();
        view_pager.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (defaultPagePosition == 0)
                    onPageChangeListener.onPageSelected(0);
                else
                    view_pager.setCurrentItem(1);
            }
        }, 100);

        if (getOpenFor() == OPEN_FOR_ONEWAY) {
            tv_dialog_title.setText("Schedule one - way ride");
        } else {
            tv_dialog_title.setText("Schedule round trip");
        }
    }

    private void handlePageTitle() {
        if (view_pager.getCurrentItem() == 0) {
            viewPagerAdapter.updatePageTitle(0, "Leave on");

            TabLayout.Tab tabAt = tab_layout.getTabAt(0);
            tabAt.setText("Leave on");

        } else {
            String tripstartDate = appBaseModel.getFormattedCalendar(AppBaseModel.DATE_TIME_THREE,
                    (startTimeCal.getTimeInMillis() / 1000));

            viewPagerAdapter.updatePageTitle(0, "Leave on" + "\n" + tripstartDate);

            TabLayout.Tab tabAt = tab_layout.getTabAt(0);
            tabAt.setText(Html.fromHtml("<b>Leave on</b>" + "<br></br>" + tripstartDate));
        }
    }

    @Override
    public int getFragmentContainerResourceId() {
        return 0;
    }

    private void setupViewPager() {

        minDateTimeBooking = MyApplication.getInstance().getOutstationBookingMinTime();

        Calendar fromDateTime = MyApplication.getInstance().getOutstationBookingMinTime();


        viewPagerAdapter = new WrappingViewPagerAdapter(getChildFm());
        DateTimePickerTabFragment dateTimePickerTabFragment = new DateTimePickerTabFragment();
        if (startTimeCal != null) {
            dateTimePickerTabFragment.setSelectedData(appBaseModel.getFormattedCalendar(AppBaseModel.DATE_THREE,
                    startTimeCal.getTimeInMillis() / 1000)
                    , startTimeCal.get(Calendar.HOUR_OF_DAY), String.valueOf(startTimeCal.get(Calendar.MINUTE)));
        } else {
            dateTimePickerTabFragment.setSelectedData(appBaseModel.getFormattedCalendar(AppBaseModel.DATE_THREE,
                    fromDateTime.getTimeInMillis() / 1000)
                    , fromDateTime.get(Calendar.HOUR_OF_DAY), String.valueOf(fromDateTime.get(Calendar.MINUTE)));
        }

        dateTimePickerTabFragment.setData(generateDates(fromDateTime, 8)
                , new String[]{"00", "15", "30", "45"}, true);
        viewPagerAdapter.addFrag(dateTimePickerTabFragment, "Leave on");
        if (startTimeCal == null)
            startTimeCal = fromDateTime;

        Calendar returnDateTime = Calendar.getInstance();
        returnDateTime.setTimeInMillis(startTimeCal.getTimeInMillis());
        returnDateTime.add(Calendar.MILLISECOND, returnTripMinPackageInMillis);


        if (getOpenFor() == OPEN_FOR_ROUNDTRIP) {
            dateTimePickerTabFragment = new DateTimePickerTabFragment();
            if (returnByTimeCal != null) {
                dateTimePickerTabFragment.setSelectedData(appBaseModel.getFormattedCalendar(AppBaseModel.DATE_THREE,
                        returnByTimeCal.getTimeInMillis() / 1000)
                        , returnByTimeCal.get(Calendar.HOUR_OF_DAY), String.valueOf(returnByTimeCal.get(Calendar.MINUTE)));
            } else {
                dateTimePickerTabFragment.setSelectedData(appBaseModel.getFormattedCalendar(AppBaseModel.DATE_THREE,
                        returnDateTime.getTimeInMillis() / 1000)
                        , returnDateTime.get(Calendar.HOUR_OF_DAY), String.valueOf(returnDateTime.get(Calendar.MINUTE)));
            }

            dateTimePickerTabFragment.setData(generateDates(returnDateTime, 11)
                    , new String[]{String.valueOf(returnDateTime.get(Calendar.MINUTE))}, false);

            viewPagerAdapter.addFrag(dateTimePickerTabFragment, "Return by");
        }
        if (returnByTimeCal == null)
            returnByTimeCal = returnDateTime;

        view_pager.setAdapter(viewPagerAdapter);
        tab_layout.setupWithViewPager(view_pager);
        view_pager.addOnPageChangeListener(onPageChangeListener);
        if (getOpenFor() == OPEN_FOR_ONEWAY) {
            updateViewVisibility(tab_layout, View.GONE);
        }

    }

    private String[] generateDates(Calendar outstationBookingMinTime, int datesCount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(outstationBookingMinTime.getTimeInMillis());
        String[] dates = new String[datesCount];
        for (int i = 0; i < dates.length; i++) {
            dates[i] = appBaseModel.getFormattedCalendar(AppBaseModel.DATE_THREE, (calendar.getTimeInMillis() / 1000));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflate = LayoutInflater.from(getActivity());
        View layout = inflate.inflate(getLayoutResourceId(), null);

        //set dialog view
        dialog.setContentView(layout);
        //setup dialog window param
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.windowAnimations = R.style.OutstationTimeDialogStyle;
        wlmp.gravity = Gravity.BOTTOM;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setTitle(null);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    public void onTimeChange(boolean startTime) {

        if (startTime) {
            DateTimePickerTabFragment startDateTimePickerTabFragment = (DateTimePickerTabFragment) viewPagerAdapter.getItem(0);

            Calendar startTimeCal = startDateTimePickerTabFragment.getSelectedDate();
            if ((this.minDateTimeBooking.getTimeInMillis() - startTimeCal.getTimeInMillis()) > 0) {
                startDateTimePickerTabFragment.setSelectedData(appBaseModel.getFormattedCalendar(AppBaseModel.DATE_THREE,
                        this.startTimeCal.getTimeInMillis() / 1000)
                        , this.startTimeCal.get(Calendar.HOUR_OF_DAY), String.valueOf(this.startTimeCal.get(Calendar.MINUTE)));
                startDateTimePickerTabFragment.updateDateAccordingToSelectedDate();
                startDateTimePickerTabFragment.updateTimeAccordingToSelectedTime();
            } else {
                this.startTimeCal = startTimeCal;
            }

        } else {
            DateTimePickerTabFragment returnDateTimePickerTabFragment = (DateTimePickerTabFragment) viewPagerAdapter.getItem(1);

            Calendar returnByTimeCal = returnDateTimePickerTabFragment.getSelectedDate();

            Calendar minReturnDateTimeBooking = Calendar.getInstance();
            minReturnDateTimeBooking.setTimeInMillis(startTimeCal.getTimeInMillis());
            minReturnDateTimeBooking.add(Calendar.MILLISECOND, returnTripMinPackageInMillis);

            if ((minReturnDateTimeBooking.getTimeInMillis() - returnByTimeCal.getTimeInMillis()) > 0) {
                returnDateTimePickerTabFragment.setSelectedData(appBaseModel.getFormattedCalendar(AppBaseModel.DATE_THREE,
                        this.returnByTimeCal.getTimeInMillis() / 1000)
                        , this.returnByTimeCal.get(Calendar.HOUR_OF_DAY), String.valueOf(this.returnByTimeCal.get(Calendar.MINUTE)));
                returnDateTimePickerTabFragment.updateDateAccordingToSelectedDate();
                returnDateTimePickerTabFragment.updateTimeAccordingToSelectedTime();
            } else {
                this.returnByTimeCal = returnByTimeCal;
            }

        }
        updateUi();
    }

    public void updateReturnCalender() {

        Calendar returnDateTime = Calendar.getInstance();
        returnDateTime.setTimeInMillis(startTimeCal.getTimeInMillis());
        returnDateTime.add(Calendar.MILLISECOND, returnTripMinPackageInMillis);

        if ((returnByTimeCal.getTimeInMillis() - startTimeCal.getTimeInMillis()) < (returnTripMinPackageInMillis)) {
            returnByTimeCal = returnDateTime;
        }

        if (viewPagerAdapter.getCount() > 1) {
            final DateTimePickerTabFragment returnDateTimePickerTabFragment = (DateTimePickerTabFragment) viewPagerAdapter.getItem(1);

            returnDateTimePickerTabFragment.setSelectedData(appBaseModel.getFormattedCalendar(AppBaseModel.DATE_THREE,
                    returnByTimeCal.getTimeInMillis() / 1000)
                    , returnByTimeCal.get(Calendar.HOUR_OF_DAY), String.valueOf(returnByTimeCal.get(Calendar.MINUTE)));

            String returnByMinute = returnByTimeCal.get(Calendar.MINUTE) > 9 ? String.valueOf(returnByTimeCal.get(Calendar.MINUTE)) :
                    "0" + String.valueOf(returnByTimeCal.get(Calendar.MINUTE));

            String[] strings = generateDates(returnDateTime, 11);
            returnDateTimePickerTabFragment.updateData(strings, new String[]{returnByMinute});

        }
    }

    private void updateUi() {
        int currentItem = view_pager.getCurrentItem();
        if (currentItem == 0) {
            String subtitle = "Starting " + appBaseModel.getFormattedCalendar(AppBaseModel.DATE_TIME_THREE,
                    (startTimeCal.getTimeInMillis() / 1000));
            tv_dialog_subtitle.setText(subtitle);

        } else {
            long l = returnByTimeCal.getTimeInMillis() - startTimeCal.getTimeInMillis();
            long hours = Math.round((float) l / (1000 * 60 * 60));
            long days = hours / 24;
            hours = hours - (days * 24);
            StringBuilder builder = new StringBuilder();
            if (days > 0) {
                builder.append(days).append(" ");
                if (days == 1) {
                    builder.append("day");
                } else {
                    builder.append("days");
                }
                builder.append(" ");
            }

            builder.append(hours).append(" ");
            if (hours == 1) {
                builder.append("hour");
            } else {
                builder.append("hours");
            }
            builder.append(" ").append("package");
            tv_dialog_subtitle.setText(builder.toString());


        }
    }

    public void onBtnClick(DateTimePickerTabFragment dateTimePickerTabFragment) {
        if (getOpenFor() == OPEN_FOR_ONEWAY) {
            Calendar returnDateTime = Calendar.getInstance();
            returnDateTime.setTimeInMillis(startTimeCal.getTimeInMillis());
            returnDateTime.add(Calendar.MILLISECOND, returnTripMinPackageInMillis);
            returnByTimeCal = returnDateTime;

            if (outstationTimeDialogListener != null) {
                outstationTimeDialogListener.onDateTimeSelected(this,
                        startTimeCal, returnByTimeCal);
            }
        } else {
            boolean startTime = dateTimePickerTabFragment.isStartTime();
            if (startTime) {
                view_pager.setCurrentItem(1);
            } else {

                if (outstationTimeDialogListener != null) {
                    outstationTimeDialogListener.onDateTimeSelected(this,
                            startTimeCal, returnByTimeCal);
                }

            }

        }
    }


    public interface OutstationTimeDialogListener {
        void onDateTimeSelected(AppBaseDialogFragment appBaseDialogFragment, Calendar startTimeCal, Calendar retrunByTimeCal);
    }

}
