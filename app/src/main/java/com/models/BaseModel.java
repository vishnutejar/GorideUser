package com.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author Manish Kumar
 * @since 2/9/17
 */



public abstract class  BaseModel {

    public static final int TAG_ONE = 1;
    public static final int TAG_TWO = 2;
    public static final int TAG_THREE = 3;
    public static final int TAG_FOUR = 4;
    public static final int TAG_FIVE = 5;

    public static final String DATE_TIME_ONE = "dd-MM-yyyy hh:mm a";
    public static final String DATE_TIME_TWO = "E, MMM dd, hh:mm a";
    public static final String DATE_THREE = "dd-MM-yyyy";
    public static final String TIME_FOUR = "hh:mm a";
    public static final String TIME_FIVE = "dd MMM yyyy";

    public boolean isValidString(String data) {
        return data != null && !data.trim().isEmpty();
    }

    public String getValidString(String data) {
        return data == null ? "" : data;
    }

    public boolean isValidList(List list) {
        return list != null && list.size() > 0;
    }

    /**
     * get valid decimal format
     *
     * @param value
     * @return
     */
    public String getValidDecimalFormat(String value) {
        if (!isValidString(value)) {
            return "0.00";
        }
        double netValue = Double.parseDouble(value);
        return getValidDecimalFormat(netValue);
    }

    public String getValidDecimalFormat(double value) {
        return String.format(Locale.ENGLISH, "%.2f", value);
    }

    /**
     * getValid Rating
     *
     * @param value
     * @return
     */
    public String getValidRatingFormat(String value) {
        if (!isValidString(value)) {
            return "0.0";
        }
        double netValue = Double.parseDouble(value);
        return getValidFormat(netValue);
    }

    public String getValidFormat(double value) {
        return String.format(Locale.ENGLISH, "%.1f", value);
    }

    /**
     * get formated date and time by format
     *
     * @param format
     * @param timestamp
     * @return
     */

    public String getFormattedCalendar(String format, long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * get valid hh:mm
     *
     * @param value
     * @return
     */
    public String getValidTimeText(long value) {
        String date;
        long hours = value / 60;
        long minutes = value % 60;
        if (hours >= 1) {
            if (minutes == 0) {
                date = String.format("%d", hours) + " Hr";
            } else {
                date = String.format("%02d:%02d", hours, minutes) + " Hr";
            }
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
            return Math.ceil(distance) + " m";
        distance = distance / 1000;
        String s = getValidDecimalFormat(distance) + " km";
        return s.replaceAll(".00", "");
    }
}
