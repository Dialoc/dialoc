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
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

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

    // Objects on screen
    private TextView dialysisClinicName;
    private TextView dialysisClinicRating;
    private TextView dialysisClinicPhone;
    private TextView dialysisClinicPhoneNumber;
    private TextView dialysisClinicWebsiteNA;
    private TextView dialysisClinicAddress;
    private ViewPager viewPager;
    private static final String TAG = "ClinicFragment";


    private LinearLayout btnShare;
    private Intent shareIntent;

    public ClinicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate fragment and assign views
        View view = inflater.inflate(R.layout.clinic, container, false);
//        dialysisClinicName = view.findViewById(R.id.dialysis_clinic_name);
        dialysisClinicPhoneNumber =  view.findViewById(R.id.dialysis_clinic_phone_number);
        dialysisClinicAddress =  view.findViewById(R.id.dialysis_clinic_address);
        btnShare = view.findViewById(R.id.share_button);

        // Navigation to listed address
        dialysisClinicAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="
                        + dialysisClinicAddress.getText().toString()));
                startActivity(geoIntent);
            }
        });

        // Share status
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I am currently at: "
                        + dialysisClinicName.getText());
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        // Find proper dimensions of the view to request picture of street
//        ViewTreeObserver vto = clinicImage.getViewTreeObserver();
//        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            public boolean onPreDraw() {
//                // Remove after the first run
//                clinicImage.getViewTreeObserver().removeOnPreDrawListener(this);
//                int height = clinicImage.getMeasuredHeight();
//                int width = clinicImage.getMeasuredWidth();
//                String request = "https://maps.googleapis.com/maps/api/streetview?size=" + width +
//                        "x" + height + "&location=33.771683,-84.406718&heading=230&fov=80&key=" +
//                        getString(R.string.google_api_key);
//                Picasso.with(getContext()).load(request).fit().into(clinicImage);
//                return true;
//            }
//        });

        viewPager = view.findViewById(R.id.gallery);
        viewPager.setAdapter(new ImagePagerAdapter(getContext(), new String[0]));
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitleEnabled(false);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

        // Get details of clinic
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GooglePlaceAPI gpa = retrofit.create(GooglePlaceAPI.class);
        Call<ResponseBody> call = gpa.getDetails(getArguments().getString("place"),
                                                getString(R.string.google_api_key));
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(GooglePlace.class, new GooglePlace.GooglePlaceDeserializer());
        final Gson gson = builder.create();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   retrofit2.Response<ResponseBody> response) {
                GooglePlace clinic = gson.fromJson(response.body().charStream(), GooglePlace.class);
                populateClinicInfo(clinic);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("HTTP Request", t.getMessage());
            }
        });

        Toolbar toolbar =  view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        DrawerLayout mDrawerLayout = ((HomeScreenActivity)getActivity()).getDrawerLayout();
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        return view;
    }



    private void populateClinicInfo(GooglePlace clinic) {
//        dialysisClinicName.setText(clinic.getName());
        ((ImagePagerAdapter)viewPager.getAdapter()).images = clinic.getPhotoArray();
        viewPager.getAdapter().notifyDataSetChanged();
        dialysisClinicPhoneNumber.setText(clinic.getInternational_phone_number());
        // Setting the address to look and behave like a link
//        SpannableString dialysisClinicAddressSpannable = new SpannableString(clinic
//                .getAddress());
//        dialysisClinicAddressSpannable
//                .setSpan(new UnderlineSpan(), 0,
//                        dialysisClinicAddressSpannable.length(), 0);
        dialysisClinicAddress.setText(clinic.getAddress());
    }

}
