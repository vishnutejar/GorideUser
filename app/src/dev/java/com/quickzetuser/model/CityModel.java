package com.quickzetuser.model;

import com.models.BaseModel;

import java.util.List;


/**
 * Created by ubuntu on 29/3/18.
 */

public class  CityModel extends BaseModel {

    private long city_id;
    private String city_name;
    private long utc_offset;
    private StateModel state;
    private CountryModel country;
    private List<PathBean> path;

    public long getCity_id() {
        return city_id;
    }

    public void setCity_id(long city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return getValidString(city_name);
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public long getUtc_offset() {
        return utc_offset;
    }

    public void setUtc_offset(long utc_offset) {
        this.utc_offset = utc_offset;
    }

    public StateModel getState_id() {
        return state;
    }

    public void setState_id(StateModel state) {
        this.state = state;
    }

    public CountryModel getCountry_id() {
        return country;
    }

    public void setCountry_id(CountryModel country) {
        this.country = country;
    }

    public List<PathBean> getPath() {
        return path;
    }

    public void setPath(List<PathBean> path) {
        this.path = path;
    }

    public static class PathBean {
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

    @Override
    public String toString() {
        return city_name;
    }

    public Object[] getPathArray(){
        if (path==null || path.size()==0) return null;
        double[] latitude_array=new double[path.size()];
        double[] longitude_array=new double[path.size()];
        for (int i = 0; i <path.size() ; i++) {
            latitude_array[i]=path.get(i).getLat();
            longitude_array[i]=path.get(i).getLng();
        }
        return new Object[]{latitude_array, longitude_array};
    }
}
