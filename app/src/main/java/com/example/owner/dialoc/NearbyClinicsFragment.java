package com.example.owner.dialoc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Owner on 4/12/2017.
 */

public class NearbyClinicsFragment extends Fragment {
    private RecyclerView recyclerView;
    private SearchPlaceAdapter nearbyPlaceAdapter;
    private ArrayList<GooglePlace> nearbyPlaces;
    private GooglePlaceAPI googlePlaceAPI;
    private Gson gson;

    public NearbyClinicsFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nearby_clinics_fragment, container, false);

        recyclerView = view.findViewById(R.id.nearby_clinics_rv);
        nearbyPlaces = new ArrayList<>();
        nearbyPlaceAdapter = new SearchPlaceAdapter(nearbyPlaces, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(nearbyPlaceAdapter);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        googlePlaceAPI = retrofit.create(GooglePlaceAPI.class);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(GooglePlace.class, new GooglePlace.GooglePlaceDeserializer());
        gson = builder.create();
        return view;
    }


    public void getNearbyClinics() {
        final ArrayList<String> list = new ArrayList<>();
        String destinations = "";
        final StringBuilder sb = new StringBuilder();
        Call<ResponseBody> call = googlePlaceAPI.getNearbyClinics("33.7490,-84.3880", "distance",
                "dialysis", getString(R.string.google_api_key));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   retrofit2.Response<ResponseBody> response) {
                Log.d("HTTP Response", response.toString());
                if (response.isSuccessful()) {
                    JsonParser parser = new JsonParser();
                    JsonArray places = parser.parse(response.body().charStream()).getAsJsonObject().get("results").getAsJsonArray();
                    for (int i = 0; i < places.size(); i++) {
                        GooglePlace googlePlace = gson.fromJson(places.get(i), GooglePlace.class);
                        nearbyPlaces.add(googlePlace);
                        sb.append(googlePlace.getLat() + "," + googlePlace.getLng() + "|");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    getDistances(nearbyPlaces, sb.toString());
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("HTTP Request", t.getMessage());
            }
        });
    }

    public void getDistances(final List<GooglePlace> places, String destinations) {
        Call<ResponseBody> call = googlePlaceAPI.getDistances("imperial", "33.7490,-84.3880",
                destinations, getString(R.string.google_api_key));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   retrofit2.Response<ResponseBody> response) {
                Log.d("HTTP Response", response.toString());
                if (response.isSuccessful()) {
                    JsonParser parser = new JsonParser();
                    JsonArray distances = parser.parse(response.body().charStream()).getAsJsonObject()
                            .get("rows").getAsJsonArray().get(0).getAsJsonObject().get("elements")
                            .getAsJsonArray();
                    for (int i = 0; i < distances.size(); i++) {
                        places.get(i).setDistance(distances.get(i).getAsJsonObject().get("distance")
                                .getAsJsonObject().get("text").getAsString());
                    }
                    nearbyPlaceAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("HTTP Request", t.getMessage());
            }
        });

    }


}
