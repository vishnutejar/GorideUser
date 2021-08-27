package com.distancematrix;

import com.google.android.gms.maps.model.LatLng;
import com.models.BaseModel;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Sunil kumar Yadav
 * @Since 13/6/18
 */
public class DistanceMatrixModel extends BaseModel {

    String address;
    LatLng latLng;
    List<DestinationModel> destinationModelList = new ArrayList<>();

    public List<DestinationModel> getDestinationModelList() {
        return destinationModelList;
    }

    public void addDestination(DestinationModel destinationModel) {
        destinationModelList.add(destinationModel);
    }

    public String getAddress() {
        return getValidString(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public static class DestinationModel extends BaseModel {
        String address;
        LatLng latLng;
        String distance_text;
        long distance_value;
        String duration_text;
        long duration_value;
        String status;

        public LatLng getLatLng() {
            return latLng;
        }

        public void setLatLng(LatLng latLng) {
            this.latLng = latLng;
        }

        public String getAddress() {
            return getValidString(address);
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDistance_text() {
            return getValidString(distance_text);
        }

        public void setDistance_text(String distance_text) {
            this.distance_text = distance_text;
        }

        public long getDistance_value() {
            return distance_value;
        }

        public void setDistance_value(long distance_value) {
            this.distance_value = distance_value;
        }

        public String getDuration_text() {
            return getValidString(duration_text);
        }

        public void setDuration_text(String duration_text) {
            this.duration_text = duration_text;
        }

        public long getDuration_value() {
            return duration_value;
        }

        public void setDuration_value(long duration_value) {
            this.duration_value = duration_value;
        }

        public String getStatus() {
            return getValidString(status);
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isStatusOk() {
            return getStatus().equalsIgnoreCase("ok");
        }
    }
}
