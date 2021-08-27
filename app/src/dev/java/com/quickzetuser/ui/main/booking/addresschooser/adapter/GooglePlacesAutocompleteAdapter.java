package com.quickzetuser.ui.main.booking.addresschooser.adapter;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addressfetching.AddressFetchModel;
import com.addressfetching.AddressResultReceiver;
import com.addressfetching.LocationModelFull;
import com.base.BaseRecycleAdapter;
import com.quickzetuser.R;
import com.quickzetuser.ui.MyApplication;
import com.quickzetuser.ui.utilities.PlaceJSONParser;
import com.utilities.GoogleApiClientHelper;
import com.utilities.ItemClickSupport;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sunil kumar yadav on 18/12/19
 */
public class GooglePlacesAutocompleteAdapter extends BaseRecycleAdapter
        implements Filterable, TextWatcher, ItemClickSupport.OnItemClickListener {
    private static final String LOG_TAG = "GooglePlacesAutocompleteAdapter";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private  String API_KEY = "";

    private AutoCompleteListener autoCompleteListener;
    private EditText editText;
    private RecyclerView recycler_view;
    private List<LocationModelFull.LocationModel> mResultList = new ArrayList<>();
    GoogleApiClientHelper googleApiClientHelper;
    private boolean isBound;
    private double pickup_latitude;
    private double pickup_longitude;
    Context context;

    // private List<LocationModelFull.LocationModel> mSearchResultList = new ArrayList<>();

  // private Context context;
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null) {
                getPredictions(constraint);
                if (mResultList != null) {
                    results.values = mResultList;
                    results.count = mResultList.size();
                }
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<LocationModelFull.LocationModel> data =
                    (List<LocationModelFull.LocationModel>) results.values;
            mResultList.clear();
            if (data != null) {
                mResultList.addAll(data);
            }
            GooglePlacesAutocompleteAdapter.this.notifyDataSetChanged();
            if (autoCompleteListener != null)
                autoCompleteListener.onPredictionResult(data);
        }
    };

    /*public GooglePlacesAutocompleteAdapter(Context context, EditText editText, RecyclerView recycler_view) {*/
    /*    this.context = context;*/
    /*    this.editText = editText;*/
    /*    this.recycler_view = recycler_view;*/
    /*    this.editText.addTextChangedListener(this);*/
    /*    LinearLayoutManager layoutManager = new LinearLayoutManager(context);*/
    /*    this.recycler_view.setLayoutManager(layoutManager);*/
    /*    this.recycler_view.setAdapter(this);*/
    /*    ItemClickSupport.addTo(this.recycler_view).setOnItemClickListener(this);*/
    /*}*/


    public GooglePlacesAutocompleteAdapter(Context ctx,List<LocationModelFull.LocationModel> mResultList, EditText editText,
                                           RecyclerView recycler_view, GoogleApiClientHelper googleApiClientHelper,
                                           boolean isBound, double pickup_latitude, double pickup_longitude)
    {
        isForDesign = false;
        this.mResultList = mResultList;
        this.editText = editText;
        this.recycler_view = recycler_view;
        this.googleApiClientHelper = googleApiClientHelper;
        this.isBound = isBound;
        this.pickup_latitude = pickup_latitude;
        this.pickup_longitude = pickup_longitude;
        this.context=ctx;
        this.editText.addTextChangedListener(this);
        //mSearchResultList.addAll(mResultList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        this.recycler_view.setLayoutManager(layoutManager);
        this.recycler_view.setAdapter(this);
        ItemClickSupport.addTo(this.recycler_view).setOnItemClickListener(this);
        API_KEY=context.getResources().getString(R.string.android_key);
    }

    public void setAutoCompleteListener(AutoCompleteListener autoCompleteListener) {
        this.autoCompleteListener = autoCompleteListener;
    }

    @Override
    public BaseRecycleAdapter.BaseViewHolder getViewHolder() {
        return new ViewHolder(inflateLayout(R.layout.item_selected_address));
    }

    @Override
    public int getDataCount() {
        return mResultList == null ? 0 : mResultList.size();
    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }


    @Override
    public void afterTextChanged(Editable editable) {
        String s = editable.toString();
        if (s.length() > 0) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000);
            return;
        }
        handler.removeCallbacks(runnable);
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String value = editText.getText().toString().trim();
            if (autoCompleteListener != null)
                autoCompleteListener.getPrediction(value);
            getFilter().filter(value);
        }
    };

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        fetchPlaceDetail(mResultList.get(position));
    }




    public interface AutoCompleteListener {

        void getPrediction(String text);

        void onPredictionResult(List<LocationModelFull.LocationModel> list);

        void getPredicationDetail(LocationModelFull.LocationModel locationModel);

        void onPredicationDetailResult(LocationModelFull.LocationModel locationModel);
    }

    public LocationModelFull.LocationModel getItem(int position) {
        return (mResultList == null || (mResultList.size() - 1 < position)) ?
                null : mResultList.get(position);
    }

    class ViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        private TextView tv_header;
        private TextView tv_sub_header;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_header = itemView.findViewById(R.id.tv_header);
            tv_sub_header = itemView.findViewById(R.id.tv_sub_header);
        }

        public void setData(int position) {
            LocationModelFull.LocationModel locationModel = getItem(position);
            if (locationModel == null) return;
            tv_header.setText(locationModel.getPrimaryText());
            tv_sub_header.setText(locationModel.getSecondaryText());
        }


       /* public ViewHolder(View itemView) {
            super(itemView);
            tv_header = itemView.findViewById(R.id.tv_header);
            tv_sub_header = itemView.findViewById(R.id.tv_sub_header);
        }

        @Override
        public void setData(int position) {
            LocationModelFull.LocationModel locationModel = getItem(position);
            if (locationModel == null) return;
            tv_header.setText(locationModel.getPrimaryText());
            tv_sub_header.setText(locationModel.getSecondaryText());
        }*/
    }

    private void getPredictions(CharSequence constraint) {
        try {

            StringBuilder stringBuilder = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            stringBuilder.append("?key=" + API_KEY);
            stringBuilder.append("&rankby=distance");
            stringBuilder.append("&language=en");
//            stringBuilder.append("&types=establishment"); //26.869309335594977,75.77594552189112
            if(isBound) {
                if(pickup_latitude==0 || pickup_longitude==0){
                    Location latestLocation = googleApiClientHelper.getLatestLocation();
                    pickup_latitude = latestLocation.getLatitude();
                    pickup_longitude = latestLocation.getLongitude();
                }
                stringBuilder.append("&location=").append(pickup_latitude).append(",").append(pickup_longitude);
                stringBuilder.append("&radius=50000");
                stringBuilder.append("&strictbounds");
            }
            stringBuilder.append("&components=country:in");
            stringBuilder.append("&input=").append(URLEncoder.encode(constraint.toString(), "utf8"));
            MyApplication.getInstance().printLog(getContext(), stringBuilder.toString());
            ParserTask parserTask = new ParserTask();
            String nearByPlace = getNearByPlace(stringBuilder.toString());
            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(nearByPlace);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getNearByPlace(String urlPlace) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlPlace);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    class ParserTask extends AsyncTask<String, Integer, List<LocationModelFull.LocationModel>> {

        JSONObject jObject;

        @Override
        protected List<LocationModelFull.LocationModel> doInBackground(String... jsonData) {

            List<LocationModelFull.LocationModel> places = new ArrayList<>();

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);
                // Getting the parsed data as a List construct
                places.addAll(placeJsonParser.parse(jObject));
            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<LocationModelFull.LocationModel> result) {
            mResultList.clear();
            mResultList.addAll(result);

            GooglePlacesAutocompleteAdapter.this.notifyDataSetChanged();
            if (autoCompleteListener != null)
                autoCompleteListener.onPredictionResult(result);
        }
    }


     public void fetchPlaceDetail(LocationModelFull.LocationModel locationModel) {

        if (locationModel == null) return;
          boolean fetch = googleApiClientHelper.fetchPlaceDetail(locationModel,
                   new AddressResultReceiver(getContext(), new Handler()) {
                 public void onAddressFetch(boolean success, AddressFetchModel addressFetchModel)
                 {
                       if (autoCompleteListener != null)
                           autoCompleteListener.onPredicationDetailResult(addressFetchModel.getLocationModel());
                 }
           });
           if (fetch) {
                 if (autoCompleteListener != null)
                      autoCompleteListener.getPredicationDetail(locationModel);
           }

     }


}