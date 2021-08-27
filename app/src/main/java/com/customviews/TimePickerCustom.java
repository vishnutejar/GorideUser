package com.customviews;

import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;


public class TimePickerCustom extends TimePicker {

    NumberPicker mMinuteSpinner;
    NumberPicker mHourSpinner;
    NumberPicker mAmPmSpinner;

    public NumberPicker getmMinuteSpinner() {
        return mMinuteSpinner;
    }

    public TimePickerCustom(Context context) {
        super(context);
        accessMinuteControl();
    }

    public TimePickerCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        accessMinuteControl();
    }

    public TimePickerCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        accessMinuteControl();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TimePickerCustom(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        accessMinuteControl();
    }

    private void accessMinuteControl() {
        try {
            Class<?> classForId = Class.forName("com.android.internal.R$id");
            Field field = classForId.getField("minute");
            this.mMinuteSpinner = this.findViewById(field.getInt(null));

            field = classForId.getField("hour");
            this.mHourSpinner = this.findViewById(field.getInt(null));

            field = classForId.getField("amPm");
            this.mAmPmSpinner = this.findViewById(field.getInt(null));


            EditText numberPickerChild = (EditText) this.mMinuteSpinner.getChildAt(0);
            numberPickerChild.setFocusable(false);
            numberPickerChild.setInputType(InputType.TYPE_NULL);

            numberPickerChild = (EditText) this.mHourSpinner.getChildAt(0);
            numberPickerChild.setFocusable(false);
            numberPickerChild.setInputType(InputType.TYPE_NULL);

            numberPickerChild = (EditText) this.mAmPmSpinner.getChildAt(0);
            numberPickerChild.setFocusable(false);
            numberPickerChild.setInputType(InputType.TYPE_NULL);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setupMinuteData(int minValue, int maxValue, String displayValues[]) {
        mMinuteSpinner.setMinValue(minValue);
        mMinuteSpinner.setMaxValue(maxValue);
        mMinuteSpinner.setDisplayedValues(displayValues);
    }

}
