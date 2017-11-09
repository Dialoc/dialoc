package com.example.owner.dialoc;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class HomeScreenActivity extends AppCompatActivity {


    // Tabs and Toolbar stuff
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;


    // NEWEST NAVIGATION DRAWER STUFF WITH TABS
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;

    private ImageView placeImageView;
    private Fragment currentTab;
    private ClinicFragment homeClinic;
    private ClinicFragment backupClinic;

    private String currentPlaceId;

    public DrawerLayout getDrawerLayout() {
        return  mDrawerLayout;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        final String homePlaceId = "ChIJ5btcA5AE9YgRFAYcKNHxumU";
        final String backupPlaceId = "ChIJBfUi1W8E9YgR8OaV1LSrqLs";

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.executePendingTransactions();
        if (savedInstanceState == null) {
            // create clinic_fragment fragments
            homeClinic = new ClinicFragment();
            Bundle homeBundle = new Bundle();
            homeBundle.putString("place-id", homePlaceId);
            homeClinic.setArguments(homeBundle);

            backupClinic= new ClinicFragment();
            final Bundle backupBundle = new Bundle();
            backupBundle.putString("place-id", backupPlaceId);
            backupClinic.setArguments(backupBundle);

            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.add(R.id.containerView, homeClinic);
            transaction.add(R.id.containerView, backupClinic);
            transaction.hide(backupClinic);
            transaction.commit();
            currentTab = homeClinic;
        } else {
            currentPlaceId = savedInstanceState.getString("currentPlaceId");
            for (int i = 0; i < mFragmentManager.getFragments().size(); i++) {
                Fragment fragment = mFragmentManager.getFragments().get(i);
                if (fragment instanceof ClinicFragment) {
                    String fragmentPlace = fragment.getArguments().getString("place-id");
                    if (fragmentPlace.equals(homePlaceId)) {
                        homeClinic = (ClinicFragment)fragment;
                    } else if (fragmentPlace.equals(backupPlaceId)) {
                        backupClinic = (ClinicFragment) fragment;
                    }
                    if (fragmentPlace.equals(currentPlaceId)) {
                        currentTab = fragment;
                    }
                }
            }
        }
//        Toolbar toolbar =  findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
//        mDrawerLayout.addDrawerListener(mDrawerToggle);
//        mDrawerToggle.syncState();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home Clinic");
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home_clinic) {
                    FragmentTransaction ftx = mFragmentManager.beginTransaction();
                    ftx.hide(currentTab).show(homeClinic).commit();
                    currentTab = homeClinic;
                    currentPlaceId = homePlaceId;
                    if (homeClinic.getClinic() != null) {
                        setTitle(homeClinic.getClinic().getName());
                    }
                } else if(item.getItemId() == R.id.backup_clinic) {
                    FragmentTransaction ftx = mFragmentManager.beginTransaction();
                    ftx.hide(currentTab).show(backupClinic).commit();
                    currentTab = backupClinic;
                    currentPlaceId = backupPlaceId;
                    if (backupClinic.getClinic() != null) {
                        setTitle(backupClinic.getClinic().getName());
                    }
                } else if(item.getItemId() == R.id.nearby_clinics) {
                }
                return true;

            }
        });
        if (savedInstanceState == null) {
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
            bottomNavigationView.setSelectedItemId(R.id.home_clinic);
        }


        // Setup click events on the Navigation View Items
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawerLayout.closeDrawers();

                if (item.getItemId() == R.id.nav_home) {

                } else if (item.getItemId() == R.id.nav_faq) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                } else if (item.getItemId() == R.id.nav_favorites) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                } else if (item.getItemId() == R.id.nav_search) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                } else if (item.getItemId() == R.id.nav_notifications) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                } else if (item.getItemId() == R.id.profile) {
                    Intent intent = new Intent(HomeScreenActivity.this, UserProfileScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent loginScreen = new Intent(HomeScreenActivity.this, LoginScreen.class);
                    startActivity(loginScreen);
                    finish();
                }
                return false;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("currentPlaceId", currentPlaceId);
    }



    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


}
