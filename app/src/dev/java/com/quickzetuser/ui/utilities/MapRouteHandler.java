package com.quickzetuser.ui.utilities;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebServiceResponseListener;
import com.quickzetuser.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

/**
 * Created by ubuntu on 21/5/16.
 */
public class MapRouteHandler {

    public static final String URL_FOR_ROUTE = "https://maps.googleapis.com/maps/api/directions/";
    private static final String TAG = MapRouteHandler.class.getSimpleName();
    private final double EARTH_RADIUS = 6371009;
    private final double PATH_NEAR_BY_METER = 30;
    Context context;
    LinkedHashMap<PolylineOptions, Polyline> polylineList;
    List<Object[]> durationDistance;
    boolean isFatching = false;
    String API_KEY;

    LatLngBounds bound;
    WebRequest webRequest;
    MapRouteListener mapRouteListener;

    Handler routeDrawAnimation = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            if (polylineList != null && polylineList.size() - 1 >= msg.what) {

                Polyline polyline = (Polyline) polylineList.values().toArray()[ msg.what ];
                if (polyline != null)
                    polyline.setVisible(true);
            }
            if (msg.what < polylineList.size()) {
                routeDrawAnimation.sendEmptyMessageDelayed(msg.what + 1, 1000 / polylineList.size());
            }
        }
    };


    public MapRouteHandler (Context context) {
        this.context = context;
        this.API_KEY = context.getString(R.string.android_key);
        this.polylineList = new LinkedHashMap<>();
        this.durationDistance = new ArrayList<>();
    }

    public LinkedHashMap<PolylineOptions, Polyline> getPolylineList () {
        return polylineList;
    }

    public List<Object[]> getDurationDistance () {
        return durationDistance;
    }

    public void drawRoute (final LatLng from, final LatLng to, final int startColor, final int endColor,
                           MapRouteListener mapRouteListener, boolean needLoader) {
        isFatching = true;
        if (needLoader)
            removePreviousPolyLine();
        if (webRequest != null && !webRequest.isCancelled()) {
            webRequest.cancel();
        }
        this.mapRouteListener = mapRouteListener;
        String url = getDirectionsUrl(from, to);
        webRequest = new WebRequest(110, url, WebRequest.GET_REQ);
        if (this.mapRouteListener != null)
            this.mapRouteListener.onRouteFatchStart(needLoader);
        webRequest.send(context, new WebServiceResponseListener() {
            @Override
            public void onWebRequestCall (WebRequest webRequest) {

            }

            @Override
            public void onWebRequestResponse (WebRequest webRequest) {
                String res = webRequest.getResponseString();
                if (isValidString(res)) {
                    ParserTask parserTask = new ParserTask();
                    parserTask.setStartColor(startColor);
                    parserTask.setEndColor(endColor);
                    parserTask.setOrigionLatLng(from);
                    parserTask.setDestLatLng(to);
                    parserTask.execute(res);

                    return;
                }
                isFatching = false;
                removePreviousPolyLine();
                if (MapRouteHandler.this.mapRouteListener != null)
                    MapRouteHandler.this.mapRouteListener.onRouteFatchEnd();
            }

            @Override
            public void onWebRequestPreResponse(WebRequest webRequest) {

            }
        });
    }

    private boolean isValidString (String data) {
        return data != null && !data.trim().isEmpty();
    }

    public LatLngBounds getBound () {
        return bound;
    }

    public void removePreviousPolyLine () {
        durationDistance.clear();
        if (polylineList != null) {
            for (Polyline polyline :
                    polylineList.values()) {
                if (polyline == null) continue;
                polyline.remove();
            }
            polylineList.clear();
        }
        bound = null;
    }

    public void setupPolyLineVisibility (boolean visible) {
        if (polylineList != null) {
            for (Polyline polyline :
                    polylineList.values()) {
                if (polyline == null) continue;
                polyline.setVisible(visible);
            }
        }
    }


    private String getDirectionsUrl (LatLng origin, LatLng dest) {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(URL_FOR_ROUTE)
                .append("json")
                .append("?")
                .append("origin=").
                append(origin.latitude)
                .append(",")
                .append(origin.longitude)
                .append("&destination=")
                .append(dest.latitude)
                .append(",")
                .append(dest.longitude);
        if (API_KEY != null && !API_KEY.trim().isEmpty()) {
            urlBuilder.append("&key=").append(API_KEY);
        }
        urlBuilder.append("&sendor=false");
        return urlBuilder.toString();
    }

    private List<PolylineOptions> getPolylineOptionsFromPath (List<LatLng> points, int startColor,
                                                              int endColor) {
        List<PolylineOptions> polylineOptionsList = new ArrayList<>();
        if (points != null && points.size() > 1) {

            boolean isMultiColor = startColor != -1 && endColor != -1;

            PolylineOptions polylineOptions = getNewPolylineOption();
            if (!isMultiColor) {
                if (startColor != -1) {
                    polylineOptions.color(startColor);
                } else if (endColor != -1) {
                    polylineOptions.color(endColor);
                }
            }

            int size = points.size();
            for (int i = 0; i < size - 1; i++) {
                LatLng pointD = points.get(i);
                LatLng pointA = points.get(i + 1);
                polylineOptions.add(pointD, pointA);
                if (isMultiColor) {
                    polylineOptions.color(interpolateRGB(startColor, endColor, ((float) (i) / size)));
                    polylineOptionsList.add(polylineOptions);
                    polylineOptions = getNewPolylineOption();
                }
            }
            if (!isMultiColor) {
                polylineOptionsList.add(polylineOptions);
            }
        }
        return polylineOptionsList;
    }

    private PolylineOptions getNewPolylineOption () {
        PolylineOptions optline = new PolylineOptions();
        optline.geodesic(true);
        // optline.width(routeWidth);
        optline.jointType(JointType.ROUND);
        optline.endCap(new RoundCap());
        optline.startCap(new RoundCap());
        return optline;
    }

    private int interpolateRGB (final int colorA, final int colorB, final float bAmount) {
        final float aAmount = 1.0f - bAmount;
        final int red = (int) (Color.red(colorA) * aAmount + Color.red(colorB) * bAmount);
        final int green = (int) (Color.green(colorA) * aAmount + Color.green(colorB) * bAmount);
        final int blue = (int) (Color.blue(colorA) * aAmount + Color.blue(colorB) * bAmount);
        return Color.rgb(red, green, blue);
    }

    public void startAnim () {
        routeDrawAnimation.sendEmptyMessageDelayed(0, 500);
    }

    public boolean isNeedRouteFatchAgain (LatLng point) {
        if (!isFatching) {
            for (Polyline polyline : polylineList.values()) {
                if (polyline != null && polyline.getTag() != null) {
                    List<LatLng> updatedPoints = new ArrayList<>();
                    updatedPoints.addAll((Collection<? extends LatLng>) polyline.getTag());
                    int index = locationIndexOnEdgeOrPath(point, updatedPoints,
                            false, true, PATH_NEAR_BY_METER);
                    if (index >= 0) {
                        int removedindex = 0;
                        for (int i = 0; i < updatedPoints.size(); i++) {
                            if (removedindex <= index + 1) {
                                updatedPoints.remove(i);
                                i--;
                                removedindex++;
                            } else {
                                break;
                            }

                        }
                        updatedPoints.add(0, point);
                        polyline.setPoints(updatedPoints);
                        return false;
                    }

                }
            }
            return true;
        }
        return false;
    }

    private int locationIndexOnEdgeOrPath (LatLng point, List<LatLng> poly, boolean closed,
                                           boolean geodesic, double toleranceEarth) {
        int size = poly.size();
        if (size == 0) {
            return -1;
        }
        double tolerance = toleranceEarth / EARTH_RADIUS;
        double havTolerance = hav(tolerance);
        double lat3 = toRadians(point.latitude);
        double lng3 = toRadians(point.longitude);
        LatLng prev = poly.get(closed ? size - 1 : 0);
        double lat1 = toRadians(prev.latitude);
        double lng1 = toRadians(prev.longitude);
        int idx = 0;
        if (geodesic) {
            for (LatLng point2 : poly) {
                double lat2 = toRadians(point2.latitude);
                double lng2 = toRadians(point2.longitude);
                if (isOnSegmentGC(lat1, lng1, lat2, lng2, lat3, lng3, havTolerance)) {
                    return Math.max(0, idx - 1);
                }
                lat1 = lat2;
                lng1 = lng2;
                idx++;
            }
        } else {
            // We project the points to mercator space, where the Rhumb segment is a straight line,
            // and compute the geodesic distance between point3 and the closest point on the
            // segment. This method is an approximation, because it uses "closest" in mercator
            // space which is not "closest" on the sphere -- but the error is small because
            // "tolerance" is small.
            double minAcceptable = lat3 - tolerance;
            double maxAcceptable = lat3 + tolerance;
            double y1 = mercator(lat1);
            double y3 = mercator(lat3);
            double[] xTry = new double[ 3 ];
            for (LatLng point2 : poly) {
                double lat2 = toRadians(point2.latitude);
                double y2 = mercator(lat2);
                double lng2 = toRadians(point2.longitude);
                if (max(lat1, lat2) >= minAcceptable && min(lat1, lat2) <= maxAcceptable) {
                    // We offset longitudes by -lng1; the implicit x1 is 0.
                    double x2 = wrap(lng2 - lng1, -PI, PI);
                    double x3Base = wrap(lng3 - lng1, -PI, PI);
                    xTry[ 0 ] = x3Base;
                    // Also explore wrapping of x3Base around the world in both directions.
                    xTry[ 1 ] = x3Base + 2 * PI;
                    xTry[ 2 ] = x3Base - 2 * PI;
                    for (double x3 : xTry) {
                        double dy = y2 - y1;
                        double len2 = x2 * x2 + dy * dy;
                        double t = len2 <= 0 ? 0 : clamp((x3 * x2 + (y3 - y1) * dy) / len2, 0, 1);
                        double xClosest = t * x2;
                        double yClosest = y1 + t * dy;
                        double latClosest = inverseMercator(yClosest);
                        double havDist = havDistance(lat3, latClosest, x3 - xClosest);
                        if (havDist < havTolerance) {
                            return Math.max(0, idx - 1);
                        }
                    }
                }
                lat1 = lat2;
                lng1 = lng2;
                y1 = y2;
                idx++;
            }
        }
        return -1;
    }

    private double wrap (double n, double min, double max) {
        return (n >= min && n < max) ? n : (mod(n - min, max - min) + min);
    }

    private double mod (double x, double m) {
        return ((x % m) + m) % m;
    }

    private double clamp (double x, double low, double high) {
        return x < low ? low : (x > high ? high : x);
    }

    private double mercator (double lat) {

        return log(tan(lat * 0.5 + PI / 4));
    }

    private double hav (double x) {
        double sinHalf = sin(x * 0.5);
        return sinHalf * sinHalf;
    }

    private double inverseMercator (double y) {
        return 2 * atan(exp(y)) - PI / 2;
    }

    double havDistance (double lat1, double lat2, double dLng) {
        return hav(lat1 - lat2) + hav(dLng) * cos(lat1) * cos(lat2);
    }

    private boolean isOnSegmentGC (double lat1, double lng1, double lat2, double lng2,
                                   double lat3, double lng3, double havTolerance) {
        double havDist13 = havDistance(lat1, lat3, lng1 - lng3);
        if (havDist13 <= havTolerance) {
            return true;
        }
        double havDist23 = havDistance(lat2, lat3, lng2 - lng3);
        if (havDist23 <= havTolerance) {
            return true;
        }
        double sinBearing = sinDeltaBearing(lat1, lng1, lat2, lng2, lat3, lng3);
        double sinDist13 = sinFromHav(havDist13);
        double havCrossTrack = havFromSin(sinDist13 * sinBearing);
        if (havCrossTrack > havTolerance) {
            return false;
        }
        double havDist12 = havDistance(lat1, lat2, lng1 - lng2);
        double term = havDist12 + havCrossTrack * (1 - 2 * havDist12);
        if (havDist13 > term || havDist23 > term) {
            return false;
        }
        if (havDist12 < 0.74) {
            return true;
        }
        double cosCrossTrack = 1 - 2 * havCrossTrack;
        double havAlongTrack13 = (havDist13 - havCrossTrack) / cosCrossTrack;
        double havAlongTrack23 = (havDist23 - havCrossTrack) / cosCrossTrack;
        double sinSumAlongTrack = sinSumFromHav(havAlongTrack13, havAlongTrack23);
        return sinSumAlongTrack > 0;  // Compare with half-circle == PI using sign of sin().
    }

    private double sinFromHav (double h) {
        return 2 * sqrt(h * (1 - h));
    }

    private double havFromSin (double x) {
        double x2 = x * x;
        return x2 / (1 + sqrt(1 - x2)) * .5;
    }

    private double sinDeltaBearing (double lat1, double lng1, double lat2, double lng2,
                                    double lat3, double lng3) {
        double sinLat1 = sin(lat1);
        double cosLat2 = cos(lat2);
        double cosLat3 = cos(lat3);
        double lat31 = lat3 - lat1;
        double lng31 = lng3 - lng1;
        double lat21 = lat2 - lat1;
        double lng21 = lng2 - lng1;
        double a = sin(lng31) * cosLat3;
        double c = sin(lng21) * cosLat2;
        double b = sin(lat31) + 2 * sinLat1 * cosLat3 * hav(lng31);
        double d = sin(lat21) + 2 * sinLat1 * cosLat2 * hav(lng21);
        double denom = (a * a + b * b) * (c * c + d * d);
        return denom <= 0 ? 1 : (a * d - b * c) / sqrt(denom);
    }

    double sinSumFromHav (double x, double y) {
        double a = sqrt(x * (1 - x));
        double b = sqrt(y * (1 - y));
        return 2 * (a + b - 2 * (a * y + b * x));
    }

    public interface MapRouteListener {
        void onRouteFatchStart (boolean needLoader);

        void onRouteFatchEnd ();

    }

    private class ParserTask extends AsyncTask<String, Integer, List<PolylineOptions>> {

        int startColor = -1;
        int endColor = -1;
        LatLng origionLatLng;
        LatLng destLatLng;
        List<Object[]> durationDistance = null;

        public void setStartColor (int startColor) {
            this.startColor = startColor;
        }

        public void setEndColor (int endColor) {
            this.endColor = endColor;
        }

        public void setOrigionLatLng (LatLng origionLatLng) {
            this.origionLatLng = origionLatLng;
        }

        public void setDestLatLng (LatLng destLatLng) {
            this.destLatLng = destLatLng;
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<PolylineOptions> doInBackground (String... jsonData) {

            String data = jsonData[ 0 ];
            if (data != null && !data.trim().isEmpty()) {
                try {
                    JSONObject jObject = new JSONObject(jsonData[ 0 ]);
                    DirectionsJSONParser parser = new DirectionsJSONParser();
                    List<List<LatLng>> routes = parser.parse(jObject);
                    durationDistance = parser.parseTravelTimeAndDistance(jObject);
                    if (routes != null && routes.size() > 0) {
                        List<LatLng> firstRoute = routes.get(0);
                        if (firstRoute != null && firstRoute.size() > 0) {
                            return getPolylineOptionsFromPath(firstRoute, startColor, endColor);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute (List<PolylineOptions> result) {
            removePreviousPolyLine();
            if (result != null && result.size() > 0) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < result.size(); i++) {
                    PolylineOptions polylineOptions = result.get(i);
                    if (polylineOptions != null) {
                        polylineOptions.visible(false);
                        if (i == 0) {
                            polylineOptions.getPoints().add(0, origionLatLng);
                        }
                        if (i == result.size() - 1) {
                            polylineOptions.getPoints().add(destLatLng);
                        }
                        for (LatLng latLng : polylineOptions.getPoints()) {
                            builder.include(latLng);
                        }
                        polylineList.put(polylineOptions, null);
                    }
                }
                MapRouteHandler.this.durationDistance.add(durationDistance.get(0));
                try {
                    bound = builder.build();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
            isFatching = false;
            if (MapRouteHandler.this.mapRouteListener != null)
                MapRouteHandler.this.mapRouteListener.onRouteFatchEnd();
        }

    }

    private class DirectionsJSONParser {
        // Receives a JSONObject and returns a list of lists containing latitude and longitude

        public List<List<LatLng>> parse (JSONObject jObject) {
            try {
                List<List<LatLng>> routes = new ArrayList<>();
                JSONArray jRoutes = jObject.getJSONArray("routes");

                //  * Traversing all routes
                for (int i = 0; i < jRoutes.length(); i++) {
                    JSONArray jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List<LatLng> singleRoute = new ArrayList<>();

                    //  * Traversing all legs
                    for (int j = 0; j < jLegs.length(); j++) {
                        JSONArray jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        //      * Traversing all steps
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);
                            if (list != null && list.size() > 0) {
                                singleRoute.addAll(list);
                            }
                        }
                    }
                    routes.add(singleRoute);
                }
                return routes;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
            return null;
        }

        public List<Object[]> parseTravelTimeAndDistance (JSONObject jObject) {

            try {
                List<Object[]> routes = new ArrayList<>();
                JSONArray jRoutes = jObject.getJSONArray("routes");
                //  * Traversing all routes
                for (int i = 0; i < jRoutes.length(); i++) {
                    Object[] data = new Object[]{-1, -1};
                    JSONArray jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    //  * Traversing all legs
                    for (int j = 0; j < jLegs.length(); j++) {
                        JSONObject distance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                        JSONObject duration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                        data[ 0 ] = distance.getLong("value");
                        data[ 1 ] = duration.getLong("value");
                    }
                    routes.add(data);
                }
                return routes;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
            return null;
        }

        // Method to decode polyline points

        private List<LatLng> decodePoly (String encoded) {

            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }
}
