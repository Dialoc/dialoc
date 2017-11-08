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
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private ImageView placeImageView;

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


        //Inflating TabFragment as first fragment

        // create clinic fragments
        final ClinicFragment homeClinic = new ClinicFragment();
        Bundle homeBundle = new Bundle();
        homeBundle.putString("place-id", "ChIJ5btcA5AE9YgRFAYcKNHxumU");
        homeClinic.setArguments(homeBundle);
        final ClinicFragment backupClinic= new ClinicFragment();
        final Bundle backupBundle = new Bundle();
        backupBundle.putString("place-id", "ChIJBfUi1W8E9YgR8OaV1LSrqLs");
        backupClinic.setArguments(backupBundle);

        mFragmentManager = getSupportFragmentManager();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                GooglePlace clinic;
                if (item.getItemId() == R.id.home_clinic) {
                    String fragTag = homeClinic.getClass().getSimpleName();
                    if (mFragmentManager.findFragmentByTag(fragTag) == null) {
                        FragmentTransaction ftx = mFragmentManager.beginTransaction();
                        ftx.addToBackStack(homeClinic.getClass().getSimpleName());
                        ftx.add(R.id.containerView, homeClinic);
                        ftx.commit();
                    } else {
                        boolean fragmentPopped = mFragmentManager
                                .popBackStackImmediate(fragTag, 0);
                        if (!fragmentPopped && mFragmentManager.findFragmentByTag(fragTag) == null) {

                            FragmentTransaction ftx = mFragmentManager.beginTransaction();
                            ftx.addToBackStack(homeClinic.getClass().getSimpleName());
                            ftx.hide(backupClinic);
                            ftx.show(homeClinic);
                            ftx.commit();
                        }
                    }
                } else if(item.getItemId() == R.id.backup_clinic) {
                    String fragTag = backupClinic.getClass().getSimpleName();
                    if (mFragmentManager.findFragmentByTag(fragTag) == null) {
                        FragmentTransaction ftx = mFragmentManager.beginTransaction();
                        ftx.addToBackStack(backupClinic.getClass().getSimpleName());
                        ftx.hide(homeClinic);
                        ftx.add(R.id.containerView, backupClinic);
                        ftx.commit();
                    } else {
                        boolean fragmentPopped = mFragmentManager
                                .popBackStackImmediate(fragTag, 0);
                        if (!fragmentPopped && mFragmentManager.findFragmentByTag(fragTag) == null) {

                            FragmentTransaction ftx = mFragmentManager.beginTransaction();
                            ftx.addToBackStack(backupClinic.getClass().getSimpleName());
                            ftx.hide(homeClinic);
                            ftx.show(backupClinic);
                            ftx.commit();
                        }
                    }
                } else if(item.getItemId() == R.id.nearby_clinics) {

                }
                return true;

            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.home_clinic);



        // Setup click events on the Navigation View Items

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawerLayout.closeDrawers();

                if (item.getItemId() == R.id.nav_home) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                }

                if (item.getItemId() == R.id.nav_faq) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                }

                if (item.getItemId() == R.id.nav_favorites) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                }

                if (item.getItemId() == R.id.nav_search) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                }

                // TODO MAKE SURE TO ADD A TEAMS FRAGMENT
                if (item.getItemId() == R.id.nav_notifications) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                }
                if (item.getItemId() == R.id.nav_settings) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                }
                if (item.getItemId() == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent loginScreen = new Intent(HomeScreenActivity.this, LoginScreen.class);
                    startActivity(loginScreen);
                    finish();
                }

                return false;
            }
        });

        // Setup Drawer Toggle of the Toolbar
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
//        mDrawerLayout.addDrawerListener(mDrawerToggle);
//        mDrawerToggle.syncState();


    }



    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


}
