package com.example.owner.dialoc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.LinkAddress;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
 * Created by rahulraina on 11/11/17.
 */

public class ClinicActivity extends AppCompatActivity {

    private GooglePlace clinic;

    GooglePlaceAPI googlePlaceAPI;
    Gson gson;
    View view;


    private TextView dialysisClinicName;
    private TextView dialysisClinicRating;
    private TextView dialysisClinicPhoneNumber;
    private TextView dialysisClinicWebsiteNA;
    private TextView dialysisClinicAddress;
    private ImageView profileAvatar;
    private TextView dialysisClinicHours;
    private TextView dialysisCinicUrl;
    private ViewPager viewPager;

    private LinearLayout shareButton;
    private LinearLayout mapButton;
    private LinearLayout addressLayout;
    private LinearLayout phoneButton;
    private LinearLayout phoneLayout;
    private LinearLayout web_layout;
    private LinearLayout reportButton;
    private LinearLayout favoriteButton;
    private ImageView favorite;
    private Intent shareIntent;

    private DatabaseReference mDatabase;
    private String placeID;

    private RecyclerView recyclerView;
    private UserReportAdapter userReportAdapter;
    private List<UserReport> userReportList;

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_activity);

        toolbar = (Toolbar) findViewById(R.id.clinic_activity_toolbar);
        toolbar.setTitle("Clinic");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        googlePlaceAPI = retrofit.create(GooglePlaceAPI.class);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(GooglePlace.class, new GooglePlace.GooglePlaceDeserializer());
        gson = builder.create();

        dialysisClinicPhoneNumber =  findViewById(R.id.dialysis_clinic_phone_number);
        dialysisClinicAddress =  findViewById(R.id.dialysis_clinic_address);
        dialysisClinicHours = findViewById(R.id.open_hours);
        dialysisCinicUrl = findViewById(R.id.website_url);
        shareButton = findViewById(R.id.share_button);
        mapButton = findViewById(R.id.map_button);
        addressLayout = findViewById(R.id.address_layout);
        phoneButton = findViewById(R.id.call_button);
        web_layout = findViewById(R.id.web_layout);
        reportButton = findViewById(R.id.report_button);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        favoriteButton = findViewById(R.id.favorite_button);
        favorite = findViewById(R.id.home_favorites);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            placeID = extras.getString("PLACE_ID");
        }

        userReportList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.user_reports_recycler_view);
        userReportAdapter = new UserReportAdapter(userReportList);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(this);
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

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference ref = mDatabase.child("/users/" + user.getUid() + "/favorites/" + placeID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Drawable d = getDrawable(R.drawable.ic_favorite_black_24dp);
                        favorite.setImageDrawable(d);
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
                    final DatabaseReference ref = mDatabase.child("/users/" + currentUser.getUid() + "/favorites/" + placeID);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                Drawable d = getDrawable(R.drawable.ic_favorite_black_24dp);
                                d.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                                favorite.setImageDrawable(d);
                                ref.setValue(true);
                            } else {
                                Drawable d = getDrawable(R.drawable.ic_favorite_border_black_24dp);
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
                PopupMenu menu = new PopupMenu(ClinicActivity.this, view);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getItemId()) {
                            case R.id.open_status:
                                mDatabase.child("/clinics/" + placeID + "/status/" + user.getUid()).setValue(menuItem.getTitle());
                                Toast.makeText(ClinicActivity.this, "Open", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.natural_disaster_status:
                                mDatabase.child("/clinics/" + placeID + "/status/" + user.getUid()).setValue(menuItem.getTitle());
                                Toast.makeText(ClinicActivity.this, "Natural Disaster", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.power_outage_status:
                                mDatabase.child("/clinics/" + placeID + "/status/" + user.getUid()).setValue(menuItem.getTitle());
                                Toast.makeText(ClinicActivity.this, "Power Outage", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.holiday_status:
                                mDatabase.child("/clinics/" + placeID + "/status/" + user.getUid()).setValue(menuItem.getTitle());
                                Toast.makeText(ClinicActivity.this, "Holiday", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.permanently_closed_status:
                                mDatabase.child("/clinics/" + placeID + "/status/" + user.getUid()).setValue(menuItem.getTitle());
                                Toast.makeText(ClinicActivity.this, "Permanent Close", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.other_status:
                                mDatabase.child("/clinics/" + placeID + "/status/" + user.getUid()).setValue(menuItem.getTitle());
                                Toast.makeText(ClinicActivity.this, "Other", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });

                menu.inflate(R.menu.report_menu);
                menu.show();
            }
        });

        viewPager = findViewById(R.id.gallery);
        viewPager.setAdapter(new ImagePagerAdapter(this, new String[0]));
        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

        if (extras != null) {
            getPlace(placeID);
        }

        getUserReports();


    }

    private void storeHomeClinic() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("/users/" + user.getUid() + "/home-clinic").setValue("PLACE_ID");
    }

    private void storeBackupClinic() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("/users/" + user.getUid() + "/backup-clinic").setValue("PLACE_ID");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.set_clinic_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.set_home_clinic) {
            storeHomeClinic();
            return true;
        } else if (id == R.id.set_backup_clinic) {
            storeBackupClinic();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void populateClinicInfo() {
        toolbar.setTitle(clinic.getName());
        viewPager.setAdapter(new ImagePagerAdapter(this, clinic.getPhotoArray()));
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
        DatabaseReference ref = mDatabase.child("/clinics/" + placeID + "/status");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userReportList.clear();
                Iterable<DataSnapshot> statuses = dataSnapshot.getChildren();
                for (DataSnapshot status : statuses) {
                    if (status.getValue() instanceof  String) {
                        String value = (String) status.getValue();
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

    public void getPlace(String placeId) {
        Log.d("ClinicActivity", "PlaceId: " + placeId);
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
