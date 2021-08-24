package com.utilities;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addressfetching.AddressFetchModel;
import com.addressfetching.AddressResultReceiver;
import com.addressfetching.LocationModelFull;
import com.base.BaseRecycleAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.rider.R;
import com.rider.ui.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ubuntu on 12/4/18.
 */

public class AutoCompleteAdapter extends BaseRecycleAdapter
        implements Filterable, TextWatcher, ItemClickSupport.OnItemClickListener {

    private Context context;
    private GoogleApiClientHelper googleApiClientHelper;
    private EditText editText;
    private RecyclerView recycler_view;
    private AutoCompleteListener autoCompleteListener;
    private List<LocationModelFull.LocationModel> mResultList = new ArrayList<>();
    private PlacesClient placesClient;
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering (CharSequence constraint) {
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
        protected void publishResults (CharSequence constraint, FilterResults results) {
            List<LocationModelFull.LocationModel> data =
                    (List<LocationModelFull.LocationModel>) results.values;
            mResultList.clear();
            if (data != null) {
                mResultList.addAll(data);
            }
            AutoCompleteAdapter.this.notifyDataSetChanged();
            if (autoCompleteListener != null)
                autoCompleteListener.onPredictionResult(data);
        }
    };

    public AutoCompleteAdapter (Context context, EditText editText,
                                RecyclerView recycler_view, GoogleApiClientHelper googleApiClientHelper) {
        isForDesign = false;
        this.context = context;
        this.googleApiClientHelper = googleApiClientHelper;
        this.editText = editText;
        this.recycler_view = recycler_view;
        this.editText.addTextChangedListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        this.recycler_view.setLayoutManager(layoutManager);
        this.recycler_view.setAdapter(this);
        ItemClickSupport.addTo(this.recycler_view).setOnItemClickListener(this);
        if (!Places.isInitialized()) {
            Places.initialize(context, context.getResources().getString(R.string.android_key));
        }
        placesClient = Places.createClient (context);
    }

    public void setAutoCompleteListener (AutoCompleteListener autoCompleteListener) {
        this.autoCompleteListener = autoCompleteListener;
    }

    @Override
    public int getDataCount () {
        return mResultList == null ? 0 : mResultList.size();
    }

    @Override
    public BaseViewHolder getViewHolder () {
        return new ViewHolder(inflateLayout(R.layout.item_selected_address));
    }


    public LocationModelFull.LocationModel getItem (int position) {
        return (mResultList == null || (mResultList.size() - 1 < position)) ?
                null : mResultList.get(position);
    }


    @Override
    public Filter getFilter () {
        return filter;
    }


    @Override
    public void beforeTextChanged (CharSequence s, int start, int count, int after) {

    }


    @Override
    public void onTextChanged (CharSequence s, int start, int before, int count) {

    }


    @Override
    public void afterTextChanged (Editable s) {
        if (autoCompleteListener != null)
            autoCompleteListener.getPrediction(s.toString());
        getFilter().filter(s);
    }

    @Override
    public void onItemClicked (RecyclerView recyclerView, int position, View v) {
        fetchPlaceDetail(mResultList.get(position));

    }

    private void getPredictions (CharSequence constraint) {
        if (placesClient != null) {
            RectangularBounds latLngBounds = ((MyApplication) context.getApplicationContext()).
                    getAutoCompleteBound();
            String autocompleteCountry = ((MyApplication) context.getApplicationContext()).
                    getAutoCompleteCountry();

            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
            FindAutocompletePredictionsRequest.Builder requestBuilder = FindAutocompletePredictionsRequest.builder();

            if (latLngBounds != null) {
                requestBuilder.setLocationBias(latLngBounds);
            }
            if (autocompleteCountry != null) {
                requestBuilder.setCountry(autocompleteCountry);
            }
            FindAutocompletePredictionsRequest request = requestBuilder.setTypeFilter(TypeFilter.GEOCODE)
                    .setSessionToken(token)
                    .setQuery(constraint.toString())
                    .build();

            placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
                @Override
                public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
                    List<AutocompletePrediction> autocompletePredictions = findAutocompletePredictionsResponse.getAutocompletePredictions();
                    final List<LocationModelFull.LocationModel> resultList = new ArrayList<>();
                    for (AutocompletePrediction autocompletePrediction : autocompletePredictions) {
                        LocationModelFull.LocationModel locationModel =
                                new LocationModelFull.LocationModel(autocompletePrediction.getPlaceId(),
                                        autocompletePrediction.getFullText(null).toString());
                        locationModel.setPrimaryText(autocompletePrediction.getPrimaryText(null).toString());
                        locationModel.setSecondaryText(autocompletePrediction.getSecondaryText(null).toString());
                        locationModel.setDescription(autocompletePrediction.getFullText(null).toString());
                        resultList.add(locationModel);
                    }

                    mResultList.clear();
                    mResultList.addAll(resultList);

                    AutoCompleteAdapter.this.notifyDataSetChanged();
                    if (autoCompleteListener != null)
                        autoCompleteListener.onPredictionResult(resultList);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    List<LocationModelFull.LocationModel> resultList = new ArrayList<>();
                    mResultList.clear();
                    mResultList.addAll(resultList);

                    AutoCompleteAdapter.this.notifyDataSetChanged();
                    if (autoCompleteListener != null)
                        autoCompleteListener.onPredictionResult(resultList);
                }
            });
        }
    }

    public void fetchPlaceDetail (LocationModelFull.LocationModel locationModel) {
        if (locationModel == null) return;
        boolean fetch = googleApiClientHelper.fetchPlaceDetail(locationModel,
                new AddressResultReceiver(context, new Handler()) {
                    @Override
                    public void onAddressFetch (boolean success, AddressFetchModel addressFetchModel) {
                        if (autoCompleteListener != null)
                            autoCompleteListener.onPredicationDetailResult(addressFetchModel.getLocationModel());
                    }
                });
        if (fetch) {
            if (autoCompleteListener != null)
                autoCompleteListener.getPredicationDetail(locationModel);
        }

    }

    public interface AutoCompleteListener {

        void getPrediction (String text);

        void onPredictionResult (List<LocationModelFull.LocationModel> list);

        void getPredicationDetail (LocationModelFull.LocationModel locationModel);

        void onPredicationDetailResult (LocationModelFull.LocationModel locationModel);
    }

    private class ViewHolder extends BaseViewHolder {
        private TextView tv_header;
        private TextView tv_sub_header;

        public ViewHolder (View itemView) {
            super(itemView);
            tv_header = itemView.findViewById(R.id.tv_header);
            tv_sub_header = itemView.findViewById(R.id.tv_sub_header);
        }

        @Override
        public void setData (int position) {
            LocationModelFull.LocationModel locationModel = getItem(position);
            if (locationModel == null) return;
            tv_header.setText(locationModel.getPrimaryText());
            tv_sub_header.setText(locationModel.getSecondaryText());
        }

        @Override
        public void onClick (View v) {
            performItemClick((Integer) v.getTag(), v);
        }
    }


}


