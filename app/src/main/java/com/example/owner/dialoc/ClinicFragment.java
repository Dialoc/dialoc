package com.example.owner.dialoc;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    public GooglePlace getClinic() {
        return clinic;
    }

    private GooglePlace clinic;

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
    private LinearLayout favoriteButton;
    private ImageView favorite;
    private LinearLayout reportButton;
    private Intent shareIntent;

    GooglePlaceAPI googlePlaceAPI;
    Gson gson;
    View view;

    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String curPlaceId;


    private RecyclerView recyclerView;
    private UserReportAdapter userReportAdapter;
    private List<UserReport> userReportList;


    public ClinicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Set up request to Places Web Service using retrofit
        super.onCreateView(inflater, container, savedInstanceState);

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
        reportButton = view.findViewById(R.id.report_button);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        favoriteButton = view.findViewById(R.id.favorite_button);
        favorite = view.findViewById(R.id.home_favorites);

        userReportList = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.user_reports_recycler_view);
        userReportAdapter = new UserReportAdapter(userReportList);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManger);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(userReportAdapter);


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

        user = FirebaseAuth.getInstance().getCurrentUser();
        curPlaceId = getArguments().getString("place-id");

        if (user != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference ref = mDatabase.child("/users/" + user.getUid() + "/favorites/" + getArguments().getString("place-id"));
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Drawable d = getContext().getDrawable(R.drawable.ic_favorite_black_24dp);
                        favorite.setImageDrawable(d);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });


            ref = mDatabase.child("/clinics/"+ getArguments().getString("place-id") + "/status");
            ref.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        int count = dataSnapshot.child("count").getValue(int.class);
                        if (count >= 1) {
                            Intent intent = new Intent(getContext(), ClinicActivity.class);
                            intent.putExtra("PLACE_ID", getArguments().getString("place-id"));
                            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);
                            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Notification.Builder builder = new Notification.Builder(getContext());
                            builder.setContentTitle("Closure Alert");
                            builder.setContentText(clinic.getName() + " may be closed. Tap for more info");
                            builder.setContentIntent(pendingIntent);
                            builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
                            builder.setAutoCancel(true);
                            builder.setPriority(Notification.PRIORITY_HIGH);
                            builder.setSound(defaultSoundUri);
                            Notification notification = builder.build();
                            NotificationManager notificationManger =
                                    (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManger.notify(1, notification);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    final DatabaseReference ref = mDatabase.child("/users/" + currentUser.getUid() + "/favorites/" + getArguments().getString("place-id"));
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                Drawable d = getContext().getDrawable(R.drawable.ic_favorite_black_24dp);
                                d.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                                favorite.setImageDrawable(d);
                                ref.setValue(true);
                            } else {
                                Drawable d = getContext().getDrawable(R.drawable.ic_favorite_border_black_24dp);
                                favorite.setImageDrawable(d);
                                ref.removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                } else {
                    Snackbar mySnackbar = Snackbar.make(view, "Log in required!", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }

            }
        });


        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(getContext(), view);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getItemId()) {
                            case R.id.open_status:
                                mDatabase.child("/clinics/" + curPlaceId + "/status/" + user.getUid()).setValue(menuItem.getTitle());
                                Toast.makeText(getActivity(), "Open", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.natural_disaster_status:
                                mDatabase.child("/clinics/" + curPlaceId + "/status/" + user.getUid()).setValue(menuItem.getTitle());
                                Toast.makeText(getActivity(), "Natural Disaster", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.power_outage_status:
                                mDatabase.child("/clinics/" + curPlaceId + "/status/" + user.getUid()).setValue(menuItem.getTitle());
                                Toast.makeText(getActivity(), "Power Outage", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.holiday_status:
                                mDatabase.child("/clinics/" + curPlaceId + "/status/" + user.getUid()).setValue(menuItem.getTitle());
                                Toast.makeText(getActivity(), "Holiday", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.permanently_closed_status:
                                mDatabase.child("/clinics/" + curPlaceId + "/status/" + user.getUid()).setValue(menuItem.getTitle());
                                Toast.makeText(getActivity(), "Permanent Close", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.other_status:
                                mDatabase.child("/clinics/" + curPlaceId + "/status/" + user.getUid()).setValue(menuItem.getTitle());
                                Toast.makeText(getActivity(), "Other", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });

                menu.inflate(R.menu.report_menu);
                menu.show();
            }
        });


        // Set up image gallery
        viewPager = view.findViewById(R.id.gallery);
        viewPager.setAdapter(new ImagePagerAdapter(getContext(), new String[0]));
        TabLayout tabLayout = view.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

        // Set toolbar

        setClinic();
        getUserReports();
        return view;
    }

    /**
     * Method to populate Fragment with relevant information
     */
    void populateClinicInfo() {
        getActivity().setTitle(clinic.getName());
        viewPager.setAdapter(new ImagePagerAdapter(view.getContext(), clinic.getPhotoArray()));
        dialysisClinicPhoneNumber.setText(clinic.getInternational_phone_number());
        Calendar calendar = Calendar.getInstance();
        if (clinic.getOpenHours() != null) {
            dialysisClinicHours.setText(clinic.getOpenHours()[(calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7]);
        } else {
            dialysisClinicHours.setText("Add Hours");
        }
        dialysisCinicUrl.setText(clinic.getWebsite());
        dialysisClinicAddress.setText(clinic.getAddress());
    }

    public void getUserReports() {
        DatabaseReference ref = mDatabase.child("/clinics/" + curPlaceId + "/status");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userReportList.clear();
                Iterable<DataSnapshot> statuses = dataSnapshot.getChildren();
                for (DataSnapshot status : statuses) {
                    if (status.getValue() instanceof String) {
                        String value = status.getValue(String.class);
                        boolean changedCount = false;
                        for (UserReport report : userReportList) {
                            if (report.getReportType().equals(value)) {
                                int curCount = report.getNumberOfReports();
                                curCount++;
                                report.setNumberOfReports(curCount);
                                changedCount = true;
                            }
                        }
                        if (!changedCount) {
                            userReportList.add(new UserReport((String) status.getValue(), 1));
                        }
                        System.out.println("Status: " + value);
                        userReportAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
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
                    JsonParser parser = new JsonParser();
                    JsonObject place = parser.parse(response.body().charStream()).getAsJsonObject().get("result").getAsJsonObject();
                    clinic = gson.fromJson(place, GooglePlace.class);
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
