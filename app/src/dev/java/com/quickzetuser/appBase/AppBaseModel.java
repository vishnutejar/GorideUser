package com.quickzetuser.appBase;

import com.medy.retrofitwrapper.BaseModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by ubuntu on 5/4/18.
 */

public class AppBaseModel extends BaseModel {

    public final String PER_KM = "/KM";
    public final String KM = " KM";
    public final String PER_MIN = "/Min";
    public static final int TAG_ONE = 1;
    public static final int TAG_TWO = 2;
    public static final int TAG_THREE = 3;
    public static final int TAG_FOUR = 4;
    public static final int TAG_FIVE = 5;

    public static final String DATE_TIME_ONE = "dd-MM-yyyy hh:mm a";
    public static final String DATE_TIME_TWO = "E, MMM dd, hh:mm a";
    public static final String DATE_TIME_THREE = "dd MMM hh:mm a";
    public static final String DATE_THREE = "dd-MM-yyyy";
    public static final String TIME_FOUR = "hh:mm a";
    public static final String TIME_FIVE = "dd MMM yyyy";

    public String getFormattedCalendar(String format, long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        return simpleDateFormat.format(calendar.getTime());
    }

    public Calendar getCalenderFromDate(String date, String dateFormate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormate,Locale.getDefault());
        try {
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(simpleDateFormat.parse(date).getTime());
            return instance;
        } catch (ParseException e) {


        }

        return null;

    }

    public String getValidTimeText(long values) {
        String date;
        long value = (int) values;
        long hours = value / 60;
        long minutes = value % 60;
        if (hours >= 1) {
            date = String.format("%02d:%02d", hours, minutes) + " Hr";
        } else {
            String text = " Min";
            if (minutes > 1)
                text = " Mins";
            date = String.format("%02d", minutes) + text;
        }
        return date;
    }

    public String getValidDistanceText(double distance) {
        if (distance < 1000)
            return Math.ceil(distance) + " M";
        distance = distance / 1000;
        return getValidDecimalFormat(distance) + " KM";
    }

}
