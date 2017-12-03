package com.example.owner.dialoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.security.AccessController.getContext;


/**
 * used to search for clinics in a particular area
 */
public class SearchClinics extends AppCompatActivity {

    private String TAG = "SearchClinics";
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private RequestQueue queue;
    private ArrayList<GooglePlace> searchPlacesList;
    private RecyclerView recyclerView;
    private SearchPlaceAdapter searchPlaceAdapter;


    private GooglePlace clinic;

    GooglePlaceAPI googlePlaceAPI;
    Gson gson;
    View view;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_clinics_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        toolbar.setTitle("Search in Area");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        queue = Volley.newRequestQueue(this);

        searchPlacesList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.search_clinics_recycler_view);
        searchPlaceAdapter = new SearchPlaceAdapter(searchPlacesList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(searchPlaceAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        googlePlaceAPI = retrofit.create(GooglePlaceAPI.class);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(GooglePlace.class, new GooglePlace.GooglePlaceDeserializer());
        gson = builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_screen_icon) {

            try {
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .build(this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Intent intent = new Intent(this, ClinicActivity.class);
                intent.putExtra("PLACE_ID", place.getId());
                startActivity(intent);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void getClinicsNearPlace(Place place) {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + place.getLatLng().latitude + ","
                + place.getLatLng().longitude
                + "&rankby=distance&keyword=dialysis&key="
                + getString(R.string.google_api_key);

        JsonObjectRequest obj = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("Response: " + response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    System.out.println("Full Results: " + jsonArray);
                    getPlacesInfo(jsonArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error: " + error.toString());
            }
        });
        queue.add(obj);
    }

    public void getPlacesInfo(JSONArray placesArray) throws JSONException {
        searchPlacesList.clear();
        for (int i=0; i < placesArray.length(); i++) {
            final JSONObject currentPlace = placesArray.getJSONObject(i);
//            searchPlacesList.add(new SearchPlace((String) currentPlace.get("name"), (String) currentPlace.get("vicinity"), (String) currentPlace.get("place_id"), (String) currentPlace.get("icon")));


            Call<ResponseBody> call = googlePlaceAPI.getDetails((String) currentPlace.get("place_id"), getString(R.string.google_api_key));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       retrofit2.Response<ResponseBody> response) {
                    Log.d("HTTP Response", response.toString());
                    if (response.isSuccessful()) {
                        GooglePlace curClinic = gson.fromJson(response.body().charStream(), GooglePlace.class);
                        searchPlacesList.add(curClinic);
                        searchPlaceAdapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("HTTP Request", t.getMessage());
                }
            });

        }

        TextView searchText = (TextView) findViewById(R.id.search_place_text);
        if (searchPlacesList.size() >= 0) {
            searchText.setVisibility(View.GONE);
        } else {
            searchText.setVisibility(View.VISIBLE);
        }

    }
}
