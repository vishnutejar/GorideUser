package com.quickzetuser.ui.utilities;


import com.addressfetching.LocationModelFull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaceJSONParser {

    /**
     * Receives a JSONObject and returns a list
     *
     * @return
     */
    public List<LocationModelFull.LocationModel> parse(JSONObject jObject) {

        JSONArray jPlaces = null;
        try {
            /** Retrieves all the elements in the 'places' array */
            String status = jObject.getString("status");
            if (status.equalsIgnoreCase("OK")) {
                jPlaces = jObject.getJSONArray("predictions");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /** Invoking getPlaces with the array of json object
         * where each json object represent a place
         */
        return getPlaces(jPlaces);
    }

    private List<LocationModelFull.LocationModel> getPlaces(JSONArray jPlaces) {
        List<LocationModelFull.LocationModel> mResultList = new ArrayList<>();

        if (jPlaces != null) {
            try {
                for (int i = 0; i < jPlaces.length(); i++) {
                    JSONObject jsonObject = jPlaces.getJSONObject(i);
//                    JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");

                    String description = jsonObject.getString("description");
                    String id = jsonObject.getString("place_id");
                    JSONObject structured_formatting = jsonObject.getJSONObject("structured_formatting");
                    String main_text = structured_formatting.getString("main_text");
                    String secondary_text = structured_formatting.getString("secondary_text");

                    LocationModelFull.LocationModel locationModel = new LocationModelFull.LocationModel(id, description);
                    locationModel.setPrimaryText(main_text);
                    locationModel.setSecondaryText(secondary_text);
                    locationModel.setDescription(description);
//                    locationModel.setLatitude(location.getDouble("lat"));
//                    locationModel.setLongitude(location.getDouble("lng"));
                    mResultList.add(locationModel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mResultList;
    }

    /**
     * Receives a JSONObject and returns a list
     *
     * @return
     */
 /*   public List<LocationModelFull.LocationModel> parse(JSONObject jObject) {

        JSONArray jPlaces = null;
        try {
            *//** Retrieves all the elements in the 'places' array *//*
            String status = jObject.getString("status");
            if (status.equalsIgnoreCase("OK")) {
                jPlaces = jObject.getJSONArray("results");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        *//** Invoking getPlaces with the array of json object
         * where each json object represent a place
         *//*
        return getPlaces(jPlaces);
    }*/

   /* private List<LocationModelFull.LocationModel> getPlaces(JSONArray jPlaces) {
        List<LocationModelFull.LocationModel> mResultList = new ArrayList<>();

        if (jPlaces != null) {
            try {
                for (int i = 0; i < jPlaces.length(); i++) {
                    JSONObject jsonObject = jPlaces.getJSONObject(i);
                    JSONObject location = jsonObject.getJSONObject("geometry")
                            .getJSONObject("location");

                    String description = jsonObject.getString("name");
                    String id = jsonObject.getString("place_id");
                    String reference = jsonObject.getString("vicinity");

                    LocationModelFull.LocationModel locationModel = new LocationModelFull.LocationModel(id, reference);
                    locationModel.setPrimaryText(description);
                    locationModel.setSecondaryText(reference);
                    locationModel.setDescription(reference);
                    locationModel.setLatitude(location.getDouble("lat"));
                    locationModel.setLongitude(location.getDouble("lng"));
                    mResultList.add(locationModel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mResultList;
    }*/
}
