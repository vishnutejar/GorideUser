package com.distancematrix;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebServiceResponseListener;
import com.quickzetuser.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * Created by ubuntu on 21/5/16.
 */
public class  DistanceMatrixHandler {

    /*public static final String URL = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
            "origins=%s&destinations=%s&key=%s";
    */


    public static final String URL = "https://maps.distancematrixapi.com/maps/api/distancematrix/json?" +
            "origins=%s&destinations=%s&key=%s";



    private static final String TAG = DistanceMatrixHandler.class.getSimpleName();
    Context context;
    public static String APIKEY = "";
    WebRequest webRequest;
    DistanceMatrixListener distanceMatrixListener;
    Executor executor;


    public DistanceMatrixHandler (Context context) {
        this.context = context;
        executor = Executors.newSingleThreadExecutor();
        APIKEY = context.getResources().getString(R.string.distancematrix_apis_exernal);
    }

    public void getDistanceAndTime (final LatLng[] from, final LatLng[] to, DistanceMatrixListener distanceMatrixListener) {
        getDistanceAndTime(from, to, null, APIKEY, distanceMatrixListener);
    }

    public void cancelPreviousRequest () {
        if (webRequest != null) {
            webRequest.cancel();
        }
    }

    public void getDistanceAndTime (final LatLng[] from, final LatLng[] to, String mode,
                                    String key, DistanceMatrixListener distanceMatrixListener) {
        if (from == null || to == null || from.length == 0 || to.length == 0) {
            throw new IllegalArgumentException("From and To invalid.");
        }
        String origins = "";
        for (LatLng latLng : from) {
            if (origins.isEmpty()) {
                if (latLng == null) {
                    origins = "0";

                } else {
                    origins = latLng.latitude + "," + latLng.longitude;

                }
            } else {
                if (latLng == null) {
                    origins += "|0";

                } else {
                    origins += "|" + latLng.latitude + "," + latLng.longitude;

                }
            }
        }

        String destinations = "";
        for (LatLng latLng : to) {
            if (destinations.isEmpty()) {
                if (latLng == null) {
                    destinations = "0";

                } else {
                    destinations = latLng.latitude + "," + latLng.longitude;

                }
            } else {
                if (latLng == null) {
                    destinations += "|0";

                } else {
                    destinations += "|" + latLng.latitude + "," + latLng.longitude;

                }
            }
        }
        String finalUrl = String.format(URL, origins, destinations, key);
        if (mode != null && !mode.trim().isEmpty()) {
            finalUrl += "&mode=" + mode;
        }
        this.distanceMatrixListener = distanceMatrixListener;
        cancelPreviousRequest();
        webRequest = new WebRequest(111, finalUrl, WebRequest.GET_REQ);
        if (this.distanceMatrixListener != null)
            this.distanceMatrixListener.onDistanceMatrixRequestStart();
        webRequest.send(context, new WebServiceResponseListener() {
            @Override
            public void onWebRequestCall (WebRequest webRequest) {

            }

            @Override
            public void onWebRequestResponse (WebRequest webRequest) {
                String res = webRequest.getResponseString();
                if (isValidString(res)) {
                    ParserTask parserTask = new ParserTask();
                    parserTask.setOrigionLatLng(from);
                    parserTask.setDestLatLng(to);
                    parserTask.executeOnExecutor(executor, res);
                    return;
                }
                if (DistanceMatrixHandler.this.distanceMatrixListener != null)
                    DistanceMatrixHandler.this.distanceMatrixListener.onDistanceMatrixRequestEnd(null);
            }

            @Override
            public void onWebRequestPreResponse(WebRequest webRequest) {

            }
        });
    }

    private boolean isValidString (String data) {
        return data != null && !data.trim().isEmpty();
    }


    public interface DistanceMatrixListener {
        void onDistanceMatrixRequestStart ();

        void onDistanceMatrixRequestEnd (List<DistanceMatrixModel> rows);
    }

    private class ParserTask extends AsyncTask<String, Integer, List<DistanceMatrixModel>> {

        LatLng[] origionLatLng;
        LatLng[] destLatLng;

        public void setOrigionLatLng (LatLng[] origionLatLng) {
            this.origionLatLng = origionLatLng;
        }

        public void setDestLatLng (LatLng[] destLatLng) {
            this.destLatLng = destLatLng;
        }

        @Override
        protected List<DistanceMatrixModel> doInBackground (String... jsonData) {

            String data = jsonData[ 0 ];
            if (isValidString(data)) {
                try {
                    JSONObject jObject = new JSONObject(data);

                    DistanceMatrixJSONParser parser = new DistanceMatrixJSONParser();
                    List<DistanceMatrixModel> routes = parser.parse(origionLatLng, destLatLng,
                            jObject);
                    return routes;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute (List<DistanceMatrixModel> result) {
            if (DistanceMatrixHandler.this.distanceMatrixListener != null)
                DistanceMatrixHandler.this.distanceMatrixListener.onDistanceMatrixRequestEnd(result);
        }

    }

    private class DistanceMatrixJSONParser {
        public List<DistanceMatrixModel> parse (LatLng[] from, LatLng[] to, JSONObject jObject) {
            try {
                if (jObject.getString("status").equalsIgnoreCase("ok")) {
                    List<DistanceMatrixModel> routes = new ArrayList<>();
                    JSONArray destination_addresses = jObject.getJSONArray("destination_addresses");
                    JSONArray origin_addresses = jObject.getJSONArray("origin_addresses");
                    JSONArray rowsArray = jObject.getJSONArray("rows");

                    for (int i = 0; i < rowsArray.length(); i++) {
                        JSONObject rowData = rowsArray.getJSONObject(i);
                        DistanceMatrixModel distanceMatrixModel = new DistanceMatrixModel();
                        distanceMatrixModel.setAddress(origin_addresses.getString(i));
                        distanceMatrixModel.setLatLng(from[ i ]);

                        JSONArray elements = rowData.getJSONArray("elements");
                        for (int j = 0; j < elements.length(); j++) {
                            JSONObject elementData = elements.getJSONObject(j);
                            DistanceMatrixModel.DestinationModel destinationModel = new DistanceMatrixModel.DestinationModel();
                            destinationModel.setAddress(destination_addresses.getString(j));
                            destinationModel.setLatLng(to[ j ]);
                            destinationModel.setStatus(elementData.getString("status"));
                            if (destinationModel.isStatusOk()) {
                                JSONObject distanceData = elementData.getJSONObject("distance");
                                JSONObject durationData = elementData.getJSONObject("duration");
                                destinationModel.setDistance_text(distanceData.getString("text"));
                                destinationModel.setDistance_value(distanceData.getLong("value"));
                                destinationModel.setDuration_text(durationData.getString("text"));
                                destinationModel.setDuration_value(durationData.getLong("value"));
                            }
                            distanceMatrixModel.addDestination(destinationModel);
                        }
                        routes.add(distanceMatrixModel);
                    }
                    return routes;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
            return null;
        }
    }
}
