package com.quickzetuser.model;

import com.models.BaseModel;


/**
 * @author Sunil kumar Yadav
 * @Since 24/5/18
 */
public class  NotificationModel extends BaseModel {

    String title;
    String notification;
    String thumb_image;
    String large_image;
    long created;
    boolean readMore = true;

    public String getTitle () {
        return getValidString(title);
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getNotification () {
        return getValidString(notification);
    }

    public void setNotification (String notification) {
        this.notification = notification;
    }

    public long getCreated () {
        return created;
    }

    public void setCreated (long created) {
        this.created = created;
    }

    public boolean isReadMore () {
        return readMore;
    }

    public void setReadMore (boolean readMore) {
        this.readMore = readMore;
    }

    public String getThumb_image () {
        return getValidString(thumb_image);

    }

    public String getLarge_image () {
        return getValidString(large_image);

    }

    public String getFormattedBookingStartTime (int tag) {
        if (tag == TAG_FIVE) {
            return getFormattedCalendar(TIME_FIVE, getCreated());
        }
        return getFormattedCalendar(TIME_FOUR, getCreated());
    }
}
