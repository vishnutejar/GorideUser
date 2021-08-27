package com.addressfetching;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.utilities.Utils;

import java.util.ArrayList;

/**
 * @author Manish Kumar
 * @since 16/9/17
 */



public class LocationModelFull {

    int code = 0;
    boolean error = false;
    String message = "";
    ArrayList<LocationModel> autoCompletePredications = new ArrayList<>();
    LocationModel data;

    public int getCode () {
        return code;
    }

    public void setCode (int code) {
        this.code = code;
    }

    public boolean isError () {
        return error;
    }

    public void setError (boolean error) {
        this.error = error;
    }

    public String getMessage () {
        return message;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public ArrayList<LocationModel> getAutoCompletePredications () {
        return autoCompletePredications;
    }

    public void setAutoCompletePredications (ArrayList<LocationModel> autoCompletePredications) {
        this.autoCompletePredications = autoCompletePredications;
    }

    public LocationModel getData () {
        return data;
    }

    public void setData (LocationModel data) {
        this.data = data;
    }

    public static class LocationModel implements Parcelable {
        public static final Creator<LocationModel> CREATOR = new Creator<LocationModel>() {
            @Override
            public LocationModel createFromParcel (Parcel in) {
                return new LocationModel(in);
            }

            @Override
            public LocationModel[] newArray (int size) {
                return new LocationModel[ size ];
            }
        };
        String addressType = "";
        String primaryText = "";
        String secondaryText = "";
        String description = "";
        String fulladdress = "";
        double latitude;
        double longitude;
        String city = "";
        String street = "";
        String township = "";
        String building = "";
        String postalCode = "";
        String state = "";
        String stateFull = "";
        String country = "";
        String countryFull = "";
        String placeId = "";
        String type = "";
        long utc_offset = -1;

        public LocationModel () {

        }

        public LocationModel (String placeId, String description) {
            this.placeId = placeId;
            this.description = description;
        }

        protected LocationModel (Parcel in) {
            primaryText = in.readString();
            secondaryText = in.readString();
            description = in.readString();
            fulladdress = in.readString();
            latitude = in.readDouble();
            longitude = in.readDouble();
            city = in.readString();
            street = in.readString();
            township = in.readString();
            building = in.readString();
            postalCode = in.readString();
            state = in.readString();
            stateFull = in.readString();
            country = in.readString();
            countryFull = in.readString();
            placeId = in.readString();
            type = in.readString();
            utc_offset = in.readLong();
        }

        public String getAddressType () {
            return addressType;
        }

        public void setAddressType (String addressType) {
            this.addressType = addressType;
        }

        public String getStreet () {
            return street;
        }

        public void setStreet (String street) {
            this.street = street;
        }

        public String getTownship () {
            return township;
        }

        public void setTownship (String township) {
            this.township = township;
        }

        public String getBuilding () {
            return building;
        }

        public void setBuilding (String building) {
            this.building = building;
        }

        public String getPostalCode () {
            return postalCode;
        }

        public void setPostalCode (String postalCode) {
            this.postalCode = postalCode;
        }

        public String getPlaceId () {
            return placeId;
        }

        public void setPlaceId (String placeId) {
            this.placeId = placeId;
        }

        public String getDescription () {
            return description;
        }

        public void setDescription (String description) {
            this.description = description;
        }

        public String getFulladdress () {
            return fulladdress;
        }

        public void setFulladdress (String fulladdress) {
            this.fulladdress = fulladdress;
        }

        public double getLatitude () {
            return latitude;
        }

        public void setLatitude (double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude () {
            return longitude;
        }

        public void setLongitude (double longitude) {
            this.longitude = longitude;
        }

        public String getCity () {
            return city;
        }

        public void setCity (String city) {
            this.city = city;
        }

        public String getState () {
            return state;
        }

        public void setState (String state) {
            this.state = state;
        }

        public String getStateFull () {
            return stateFull;
        }

        public void setStateFull (String stateFull) {
            this.stateFull = stateFull;
        }

        public String getCountry () {
            return country;
        }

        public void setCountry (String country) {
            this.country = country;
        }

        public String getCountryFull () {
            return countryFull;
        }

        public void setCountryFull (String countryFull) {
            this.countryFull = countryFull;
        }

        public String getType () {
            return type;
        }

        public void setType (String type) {
            this.type = type;
        }

        public long getUtc_offset () {
            return utc_offset;
        }

        public void setUtc_offset (long utc_offset) {
            this.utc_offset = utc_offset;
        }

        public String getPrimaryText () {
            return primaryText;
        }

        public void setPrimaryText (String primaryText) {
            this.primaryText = primaryText;
        }

        public String getSecondaryText () {
            return secondaryText;
        }

        public void setSecondaryText (String secondaryText) {
            this.secondaryText = secondaryText;
        }

        @Override
        public String toString () {
            return getDescription();
        }

        @Override
        public int describeContents () {
            return 0;
        }

        public LatLng getLatLng () {
            return new LatLng(latitude, longitude);
        }

        @Override
        public void writeToParcel (Parcel dest, int flags) {
            dest.writeString(primaryText);
            dest.writeString(secondaryText);
            dest.writeString(description);
            dest.writeString(fulladdress);
            dest.writeDouble(latitude);
            dest.writeDouble(longitude);
            dest.writeString(city);
            dest.writeString(street);
            dest.writeString(township);
            dest.writeString(building);
            dest.writeString(postalCode);
            dest.writeString(state);
            dest.writeString(stateFull);
            dest.writeString(country);
            dest.writeString(countryFull);
            dest.writeString(placeId);
            dest.writeString(type);
            dest.writeLong(utc_offset);
        }

        public LatLngBounds getBoundArround (double boundDistanceInKM) {
            return new LatLngBounds.Builder().
                    include(Utils.computeOffset(getLatLng(), boundDistanceInKM * 1000, 0)).
                    include(Utils.computeOffset(getLatLng(), boundDistanceInKM * 1000, 90)).
                    include(Utils.computeOffset(getLatLng(), boundDistanceInKM * 1000, 180)).
                    include(Utils.computeOffset(getLatLng(), boundDistanceInKM * 1000, 270)).build();
        }

        @Override
        public boolean equals (Object obj) {
            if (obj != null && obj instanceof LocationModel) {
                return ((LocationModel) obj).getAddressType() == getAddressType();
            }
            return false;
        }
    }


}
