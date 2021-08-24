package com.rider.ui.main.dialog.outstationtimepicker;

import android.os.Build;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.customviews.TimePickerCustom;
import com.rider.R;
import com.rider.appBase.AppBaseFragment;
import com.rider.appBase.AppBaseModel;

import java.util.Calendar;

public class DateTimePickerTabFragment extends AppBaseFragment implements TimePicker.OnTimeChangedListener {

    String[] dates;
    String[] minuteDisplayValues;
    boolean isStartTime;
    NumberPicker np_date;
    TimePickerCustom time_picker;
    TextView tv_btn;

    String selectedDate;
    int selectedHr;
    String selectedMin;


    public void setData(String[] dates, String[] minuteDisplayValues, boolean isStartTime) {
        this.dates = dates;
        this.minuteDisplayValues = minuteDisplayValues;
        this.isStartTime = isStartTime;
    }

    public void setSelectedData(String selectedDate, int selectedHr, String selectedMin) {
        this.selectedDate = selectedDate;
        this.selectedHr = selectedHr;
        this.selectedMin = selectedMin;
    }

    public void updateData(String[] dates, String[] minuteDisplayValues) {
        this.dates = dates;
        this.minuteDisplayValues = minuteDisplayValues;
        setupDates();
        setupMinutes();
    }


    public boolean isStartTime() {
        return isStartTime;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_time_picker;
    }

    @Override
    public void initializeComponent() {
        tv_btn = getView().findViewById(R.id.tv_btn);
        tv_btn.setOnClickListener(this);

        np_date = getView().findViewById(R.id.np_date);
        np_date.setWrapSelectorWheel(false);

        EditText numberPickerChild = (EditText) np_date.getChildAt(0);
        numberPickerChild.setFocusable(false);
        numberPickerChild.setInputType(InputType.TYPE_NULL);

        time_picker = getView().findViewById(R.id.time_picker);
        setupDates();
        setupMinutes();
        updateBtnUi();

    }

    private void updateBtnUi() {
        if (getParentFragment() != null) {
            int openFor = ((OutstationTimeDialog) getParentFragment()).getOpenFor();

            if (openFor == 1) {
                tv_btn.setText("DONE");
            } else {
                if (isStartTime) {
                    tv_btn.setText("NEXT");
                } else {
                    tv_btn.setText("DONE");
                }
            }
        }
    }

    private void setupDates() {
        np_date.setOnValueChangedListener(null);
        np_date.setMinValue(0);
        np_date.setMaxValue(dates.length - 1);
        np_date.setOnLongPressUpdateInterval(10000);
        np_date.setDisplayedValues(dates);
        updateDateAccordingToSelectedDate();
        np_date.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (getParentFragment() != null) {
                    if (!isStartTime) {
                        DateTimePickerTabFragment.this.selectedDate = dates[newVal];
                    }
                    ((OutstationTimeDialog) getParentFragment()).onTimeChange(isStartTime);
                }
            }
        });
    }

    public void updateDateAccordingToSelectedDate() {
        int matchedDate = 0;
        for (int i = 0; i < dates.length; i++) {
            if (dates[i].equals(selectedDate)) {
                matchedDate = i;
                break;
            }
        }
        this.selectedDate = dates[matchedDate];
        int value = np_date.getValue();
        if (value == matchedDate) {
            np_date.invalidate();
        } else {
            np_date.setValue(matchedDate);
        }


    }

    public void updateTimeAccordingToSelectedTime() {
        if (time_picker.getCurrentHour() != selectedHr)
            time_picker.setCurrentHour(selectedHr);
        int matchedMin = 0;
        for (int i = 0; i < minuteDisplayValues.length; i++) {
            if (minuteDisplayValues[i].equals(selectedMin)) {
                matchedMin = i;
                break;
            }
        }
        this.selectedMin = minuteDisplayValues[matchedMin];
        NumberPicker numberPicker = time_picker.getmMinuteSpinner();
        if (numberPicker.getValue() == matchedMin) {
            numberPicker.invalidate();
        } else {
            time_picker.setCurrentMinute(matchedMin);
        }


    }

    private void setupMinutes() {
        time_picker.setOnTimeChangedListener(null);
        time_picker.setupMinuteData(0, minuteDisplayValues.length - 1, minuteDisplayValues);
        updateTimeAccordingToSelectedTime();
        time_picker.setOnTimeChangedListener(this);
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        if (getParentFragment() != null) {
            ((OutstationTimeDialog) getParentFragment()).onTimeChange(isStartTime);
        }
    }

    public Calendar getSelectedDate() {
        AppBaseModel appBaseModel = new AppBaseModel();
        int hourOfDay = 0;
        int minute = 0;

        String date = dates[np_date.getValue()];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hourOfDay = time_picker.getHour();
        } else {
            hourOfDay = time_picker.getCurrentHour();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            minute = time_picker.getMinute();
        } else {
            minute = time_picker.getCurrentMinute();
        }
        Calendar instance = appBaseModel.getCalenderFromDate(date, AppBaseModel.DATE_THREE);
        instance.set(Calendar.HOUR_OF_DAY, hourOfDay);
        instance.set(Calendar.MINUTE, Integer.parseInt(minuteDisplayValues[minute]));
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isStartTime && isVisibleToUser) {
            // ((OutstationTimeDialog) getParentFragment()).updateReturnCalender();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (getParentFragment() != null) {
            ((OutstationTimeDialog) getParentFragment()).onBtnClick(this);
        }
    }
}
