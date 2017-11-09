package com.example.owner.dialoc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Owner on 4/12/2017.
 */

public class ClinicFragment extends Fragment {

    private ImageView clinicImage;
    private GooglePlace homeClinic;

    // Objects on screen
    private TextView dialysisClinicName;
    private TextView dialysisClinicRating;
    private TextView dialysisClinicPhoneNumber;
    private TextView dialysisClinicWebsiteNA;
    private TextView dialysisClinicAddress;
    private ImageView profileAvatar;
    private TextView dialysisClinicHours;
    private TextView dialysisCinicUrl;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private static final String TAG = "ClinicFragment";


    private LinearLayout shareButton;
    private LinearLayout mapButton;
    private LinearLayout addressLayout;
    private LinearLayout phoneButton;
    private LinearLayout phoneLayout;
    private LinearLayout web_layout;
    private Intent shareIntent;

    GooglePlaceAPI googlePlaceAPI;
    Gson gson;
    View view;

    public ClinicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Set up request to Places Web Service using retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        googlePlaceAPI = retrofit.create(GooglePlaceAPI.class);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(GooglePlace.class, new GooglePlace.GooglePlaceDeserializer());
        gson = builder.create();

        // Inflate fragment and assign views
        view = inflater.inflate(R.layout.clinic_fragment, container, false);
        dialysisClinicPhoneNumber =  view.findViewById(R.id.dialysis_clinic_phone_number);
        dialysisClinicAddress =  view.findViewById(R.id.dialysis_clinic_address);
        dialysisClinicHours = view.findViewById(R.id.open_hours);
        dialysisCinicUrl = view.findViewById(R.id.website_url);
        shareButton = view.findViewById(R.id.share_button);
        mapButton = view.findViewById(R.id.map_button);
        addressLayout = view.findViewById(R.id.address_layout);
        phoneButton = view.findViewById(R.id.call_button);
        web_layout = view.findViewById(R.id.web_layout);


        View.OnClickListener navigationListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="
                        + dialysisClinicAddress.getText().toString()));
                startActivity(geoIntent);
            }
        };

        mapButton.setOnClickListener(navigationListener);
        addressLayout.setOnClickListener(navigationListener);

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", dialysisClinicPhoneNumber.getText().toString(), null));
                startActivity(intent);

            }
        });

        web_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(dialysisCinicUrl.getText().toString()));
                startActivity(intent);
            }
        });

        // Share status
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I am currently at: "
                        + clinic.getName());
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        // Set up image gallery
        viewPager = view.findViewById(R.id.gallery);
        viewPager.setAdapter(new ImagePagerAdapter(getContext(), new String[0]));
        TabLayout tabLayout = view.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

        // Set toolbar
        toolbar =  view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        DrawerLayout mDrawerLayout = ((HomeScreenActivity)getActivity()).getDrawerLayout();
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        setClinic();
        return view;
    }

    /**
     * Method to populate Fragment with relevant information
     */
    void populateClinicInfo() {
        toolbar.setTitle(clinic.getName());
        viewPager.setAdapter(new ImagePagerAdapter(view.getContext(), clinic.getPhotoArray()));
        dialysisClinicPhoneNumber.setText(clinic.getInternational_phone_number());
        Calendar calendar = Calendar.getInstance();
        dialysisClinicHours.setText(clinic.getOpenHours()[(calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7]);
        dialysisCinicUrl.setText(clinic.getWebsite());
        dialysisClinicAddress.setText(clinic.getAddress());
    }



    void setClinic() {
        String placeId = getArguments().getString("place-id");
        Call<ResponseBody> call = googlePlaceAPI.getDetails(placeId, getString(R.string.google_api_key));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   retrofit2.Response<ResponseBody> response) {
                Log.d("HTTP Response", response.toString());
                if (response.isSuccessful()) {
                    clinic = gson.fromJson(response.body().charStream(), GooglePlace.class);
                    populateClinicInfo();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("HTTP Request", t.getMessage());
            }
        });
    }

}
