package com.quickzetuser.model;

import com.models.BaseModel;


/**
 * @author Sunil kumar Yadav
 * @Since 25/5/18
 */
public class RatingModel extends BaseModel {

    long booking_id;
    float rating;

    public long getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(long booking_id) {
        this.booking_id = booking_id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
