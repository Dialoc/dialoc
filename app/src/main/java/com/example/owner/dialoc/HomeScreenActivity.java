package com.example.owner.dialoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeScreenActivity extends AppCompatActivity {


    // Tabs and Toolbar stuff
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 141;



    // NEWEST NAVIGATION DRAWER STUFF WITH TABS
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private BottomNavigationView bottomNavigationView;
    private String userId;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private ImageView placeImageView;
    private Fragment currentTab;
    private ClinicFragment homeClinic;
    private ClinicFragment backupClinic;
    private String homePlaceId = "";
    private String backupPlaceId = "";
    private NearbyClinicsFragment nearbyClinics;

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

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = "";
        if (currentUser != null) {
            userId = currentUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            DatabaseReference ref = mDatabase.child("/users/" + userId + "/first-last-name");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    View header= mNavigationView.getHeaderView(0);
                    TextView nameText = header.findViewById(R.id.nav_header_name);
                    nameText.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        System.out.println("Home place id: " + homePlaceId);
        if (homePlaceId.equals("")) {
            homePlaceId = "ChIJ5btcA5AE9YgRFAYcKNHxumU";
        }
        if (backupPlaceId.equals("")) {
            backupPlaceId = "ChIJBfUi1W8E9YgR8OaV1LSrqLs";
        }
        System.out.println("Home place id: " + homePlaceId);
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.executePendingTransactions();
        if (savedInstanceState == null) {
            // create clinic_fragment fragments
            homeClinic = new ClinicFragment();
            Bundle homeBundle = new Bundle();
            homeBundle.putBoolean("homeBool", true);
            homeBundle.putString("place-id", homePlaceId);
            homeClinic.setArguments(homeBundle);

            backupClinic= new ClinicFragment();
            final Bundle backupBundle = new Bundle();
            backupBundle.putString("place-id", backupPlaceId);
            backupClinic.setArguments(backupBundle);

            nearbyClinics = new NearbyClinicsFragment();


            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.add(R.id.containerView, homeClinic);
            transaction.add(R.id.containerView, backupClinic);
            transaction.add(R.id.containerView, nearbyClinics);
            transaction.hide(backupClinic);
            transaction.hide(nearbyClinics);
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
                } else if(fragment instanceof NearbyClinicsFragment) {
                    nearbyClinics = (NearbyClinicsFragment) fragment;
                    if (currentPlaceId.equals("nearby")) {
                        currentTab = fragment;
                    }
                }
            }
        }

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
                    FragmentTransaction ftx = mFragmentManager.beginTransaction();
                    ftx.hide(currentTab).show(nearbyClinics).commit();
                    currentTab = nearbyClinics;
                    currentPlaceId = "nearby";
                    nearbyClinics.getNearbyClinics();
                    setTitle("Nearby Clinics");
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

                } else if (item.getItemId() == R.id.nav_favorites) {
                    Intent favoritesScreen = new Intent(HomeScreenActivity.this, FavoritesActivity.class);
                    startActivity(favoritesScreen);
                } else if (item.getItemId() == R.id.nav_search) {
                    Intent searchScreen = new Intent(HomeScreenActivity.this, SearchClinics.class);
                    startActivity(searchScreen);
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

    public void getHomeClinic() {
        DatabaseReference ref = mDatabase.child("/users/" + currentUser.getUid() + "/home-clinic");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String homeClinic = dataSnapshot.getValue(String.class);
                System.out.println("Home Clinic ID: " + homeClinic);
                homePlaceId =  homeClinic;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                homePlaceId = "";
            }
        });
    }

    public void getBackupClinic() {
        DatabaseReference ref = mDatabase.child("/users/" + currentUser.getUid() + "/backup-clinic");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String backupClinic = dataSnapshot.getValue(String.class);
                System.out.println("Backup Clinic ID: " + backupClinic);
                backupPlaceId =  backupClinic;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                backupPlaceId = "";
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
                Log.i("AutoComplete", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }






}
