package com.example.owner.dialoc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
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
    private GooglePlace homeClinic;

    // Objects on screen
    private TextView dialysisClinicName;
    private TextView dialysisClinicRating;
    private TextView dialysisClinicPhone;
    private TextView dialysisClinicPhoneNumber;
    private TextView dialysisClinicWebsiteNA;
    private TextView dialysisClinicAddress;
    private ImageView profileAvatar;
    private static final String TAG = "ClinicFragment";


    private Button btnShare;
    private Intent shareIntent;

    public ClinicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate fragment and assign views
        View view = inflater.inflate(R.layout.clinic_fragment, container, false);
        dialysisClinicName = view.findViewById(R.id.dialysis_clinic_name);
        dialysisClinicRating = view.findViewById(R.id.dialysis_clinic_rating);
        dialysisClinicPhone =  view.findViewById(R.id.dialysis_clinic_phone);
        dialysisClinicPhoneNumber =  view.findViewById(R.id.dialysis_clinic_phone_number);
        dialysisClinicWebsiteNA =  view.findViewById(R.id.dialysis_clinic_website_na);
        dialysisClinicAddress =  view.findViewById(R.id.dialysis_clinic_address);
        clinicImage = view.findViewById(R.id.clinic_image);
        btnShare = view.findViewById(R.id.share_button);
        profileAvatar = view.findViewById(R.id.profile_avatar);

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

        // Navigate to User Profile Screen
        profileAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserProfileScreen.class);
                startActivity(intent);
            }
        });

        // Find proper dimensions of the view to request picture of street
        ViewTreeObserver vto = clinicImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                // Remove after the first run
                clinicImage.getViewTreeObserver().removeOnPreDrawListener(this);
                int height = clinicImage.getMeasuredHeight();
                int width = clinicImage.getMeasuredWidth();
                String request = "https://maps.googleapis.com/maps/api/streetview?size=" + width +
                        "x" + height + "&location=33.771683,-84.406718&heading=230&fov=80&key=" +
                        getString(R.string.google_api_key);
                Picasso.with(getContext()).load(request).fit().into(clinicImage);
                return true;
            }
        });

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
                homeClinic = gson.fromJson(response.body().charStream(), GooglePlace.class);
                dialysisClinicName.setText(homeClinic.getName());
                dialysisClinicRating.setText(homeClinic.getRating() + "/5");
                dialysisClinicPhoneNumber.setText(homeClinic.getInternational_phone_number());
                // Setting the address to look and behave like a link
                SpannableString dialysisClinicAddressSpannable = new SpannableString(homeClinic
                        .getAddress());
                dialysisClinicAddressSpannable
                        .setSpan(new UnderlineSpan(), 0,
                                dialysisClinicAddressSpannable.length(), 0);
                dialysisClinicAddress.setText(dialysisClinicAddressSpannable);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("HTTP Request", t.getMessage());
            }
        });
        return view;
    }

}
