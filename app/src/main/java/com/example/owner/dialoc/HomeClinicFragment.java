package com.example.owner.dialoc;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Owner on 4/12/2017.
 */

public class HomeClinicFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private StreetViewPanoramaFragment streetViewPanoramaFragment;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private RequestQueue queue;
    private StreetViewPanoramaView streetViewPanorama;
    private StreetViewPanorama mStreetViewPanoramaMap;
    private StreetViewPanoramaView mStreetViewPanoramaView;

    // Objects on screen
    private TextView dialysisClinicName;
    private TextView dialysisClinicRating;
    private TextView dialysisClinicPhone;
    private TextView dialysisClinicPhoneNumber;
    private TextView dialysisClinicWebsiteNA;
    private static final String TAG = "HomeClinicFragment";
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final String STREETVIEW_BUNDLE_KEY = "StreetViewBundleKey";
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;


    public HomeClinicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_clinic_fragment, container, false);
        mStreetViewPanoramaView = (StreetViewPanoramaView) view.findViewById(R.id.street_view_of_home_clinic);
        // *** IMPORTANT ***
        // StreetViewPanoramaView requires that the Bundle you pass contain _ONLY_
        // StreetViewPanoramaView SDK objects or sub-Bundles.
        Bundle mStreetViewBundle = null;
        if (savedInstanceState != null) {
            mStreetViewBundle = savedInstanceState.getBundle(STREETVIEW_BUNDLE_KEY);
        }
        mStreetViewPanoramaView.onCreate(mStreetViewBundle);
        mStreetViewPanoramaView.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                panorama.setPosition(new LatLng(33.771683, -84.406718));
                mStreetViewPanoramaMap = panorama;
            }
        });
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getContext(), null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext(), null);


        dialysisClinicName = (TextView) view.findViewById(R.id.dialysis_clinic_name);
        dialysisClinicRating = (TextView) view.findViewById(R.id.dialysis_clinic_rating);
        dialysisClinicPhone = (TextView) view.findViewById(R.id.dialysis_clinic_phone);
        dialysisClinicPhoneNumber = (TextView) view.findViewById(R.id.dialysis_clinic_phone_number);
        dialysisClinicWebsiteNA = (TextView) view.findViewById(R.id.dialysis_clinic_website_na);

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        }
        mGeoDataClient.getPlaceById("ChIJ5btcA5AE9YgRFAYcKNHxumU").addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    Place myPlace = places.get(0);
                    dialysisClinicRating.setText(myPlace.getRating() + "/5");
                    dialysisClinicPhoneNumber.setText(myPlace.getPhoneNumber());
                    Log.i(TAG, "Place found: " + myPlace.getLatLng());
                    places.release();
                } else {
                    Log.e(TAG, "Place not found.");
                }
            }
        });



        return view;
    }

    @Override
    public void onResume() {
        mStreetViewPanoramaView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mStreetViewPanoramaView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mStreetViewPanoramaView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mStreetViewBundle = outState.getBundle(STREETVIEW_BUNDLE_KEY);
        if (mStreetViewBundle == null) {
            mStreetViewBundle = new Bundle();
            outState.putBundle(STREETVIEW_BUNDLE_KEY, mStreetViewBundle);
        }

        mStreetViewPanoramaView.onSaveInstanceState(mStreetViewBundle);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//            System.out.println("Last Location: " + mLastLocation);
//        }
        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    System.out.println("Last Location: " + mLastLocation);
                    System.out.println("Last lat: " + mLastLocation.getLatitude());
                    System.out.println("Last long: " + mLastLocation.getLongitude());
//                    requestNearbyDialysisClinic();
                }
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("Allow Location Services")
                        .setMessage("Would you like to utilize location services?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if (mLastLocation != null) {
                            System.out.println("Last Location: " + mLastLocation);
                            System.out.println("Last lat: " + mLastLocation.getLatitude());
                            System.out.println("Last long: " + mLastLocation.getLongitude());
//                            requestNearbyDialysisClinic();
                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

//    public void requestNearbyDialysisClinic() {
////        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=dialysis&location=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&radius=50000&key=AIzaSyDsErmQAN0ssNz2BZZs2nhL2SalqR_IM7o";
//        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&rankby=distance&type=doctor&keyword=dialysis&key=" + getString(R.string.google_api_key);
//        System.out.println("Url: " + url);
//        JsonObjectRequest obj = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                System.out.println("Response: " + response.toString());
//                try {
//                    JSONArray jsonArray = response.getJSONArray("results");
//                    JSONObject firstPlace = (JSONObject) jsonArray.get(1);
//                    System.out.println("First Place: " + firstPlace);
//                    String placeId = (String) firstPlace.get("place_id");
////                    JSONArray photos = (JSONArray) firstPlace.get("photos");
////                    String firstPhotoReference = (String) ((JSONObject) photos.get(0)).get("photo_reference");
//                    System.out.println("Place ID: " + placeId);
//                    getPlaceInfo(placeId);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                System.out.println("Error: " + error.toString());
//            }
//        });
//        queue.add(obj);
//    }

//    public void getPlaceInfo(String placeId) {
//
//
//        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + getString(R.string.google_api_key);
//
//
//        JsonObjectRequest obj = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                System.out.println("Specific Place Info: " + response.toString());
//                try {
//                    JSONObject result = (JSONObject) response.get("result");
//
//                    String name = (String) result.get("name");
//                    dialysisClinicName.setText(name);
//
//                    if (result.has("website")) {
//                        String website = (String)result.get("website");
//                        dialysisClinicWebsiteUrl.setText(website);
//                        dialysisClinicWebsiteUrl.setVisibility(View.VISIBLE);
//                        dialysisClinicWebsiteNA.setVisibility(View.INVISIBLE);
//                    } else {
//                        dialysisClinicWebsiteUrl.setVisibility(View.INVISIBLE);
//                        dialysisClinicWebsiteNA.setVisibility(View.VISIBLE);
//                    }
//
//                    if (result.has("rating")) {
//                        double rating = (double) result.get("rating");
//                        dialysisClinicRating.setText("Rating: " + rating);
//                    } else {
//                        dialysisClinicRating.setText("Rating: N/A");
//                    }
//
//
//                    JSONObject geometry = (JSONObject) result.get("geometry");
//                    JSONObject location = (JSONObject) geometry.get("location");
//                    LatLng destLatLng = new LatLng((double) location.get("lat"), (double) location.get("lng"));
//                    if (mStreetViewPanoramaMap != null) {
//                        mStreetViewPanoramaMap.setPosition(destLatLng, 500);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                System.out.println("Error: " + error.toString());
//            }
//        });
//        queue.add(obj);
//    }


}
