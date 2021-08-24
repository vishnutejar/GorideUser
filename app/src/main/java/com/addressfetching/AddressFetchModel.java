package com.addressfetching;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.medy.retrofitwrapper.BaseModel;


/**
 * @author Manish Kumar
 * @since 6/9/17
 */


public class AddressFetchModel extends BaseModel implements Parcelable {

    public static final Creator<AddressFetchModel> CREATOR = new Creator<AddressFetchModel>() {
        @Override
        public AddressFetchModel createFromParcel(Parcel in) {
            return new AddressFetchModel(in);
        }

        @Override
        public AddressFetchModel[] newArray(int size) {
            return new AddressFetchModel[size];
        }
    };
    Location location;
    String address;
    String errorMessage;
    boolean success;
    LocationModelFull.LocationModel locationModel;


    public AddressFetchModel() {

    }

    protected AddressFetchModel(Parcel in) {
        location = in.readParcelable(Location.class.getClassLoader());
        address = in.readString();
        errorMessage = in.readString();
        success = in.readByte() != 0;
        locationModel = in.readParcelable(LocationModelFull.LocationModel.class.getClassLoader());
    }

    public LatLng getLatLng() {
        if (location == null) return null;
        LatLng latLng = new LatLng(this.location.getLatitude(), this.location.getLongitude());
        return latLng;
    }

    public LocationModelFull.LocationModel getLocationModel() {
        return locationModel;
    }

    public void setLocationModel(LocationModelFull.LocationModel locationModel) {
        this.locationModel = locationModel;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeString(address);
        dest.writeString(errorMessage);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeParcelable(locationModel, flags);
    }
}
