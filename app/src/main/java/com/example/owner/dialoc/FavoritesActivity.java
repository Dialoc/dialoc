package com.example.owner.dialoc;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<GooglePlace> favorites;
    private SearchPlaceAdapter favoritesAdapter;
    private TextView emptytext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        recyclerView = findViewById(R.id.favorites_rv);
        favorites = new ArrayList<>();
        favoritesAdapter = new SearchPlaceAdapter(favorites, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(favoritesAdapter);
        emptytext = findViewById(R.id.empty_text);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference ref = mDatabase.child("/users/" + user.getUid() + "/favorites");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> placeIDs = new ArrayList<>();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        Log.d("Test", "Made it here");
                        placeIDs.add(snap.getKey());
                    }
                    populateFavorites(placeIDs);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

    }

    public void populateFavorites(List<String> placeIDs) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GooglePlaceAPI googlePlaceAPI = retrofit.create(GooglePlaceAPI.class);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(GooglePlace.class, new GooglePlace.GooglePlaceDeserializer());
        final Gson gson = builder.create();
        for (int i = 0; i < placeIDs.size(); i++) {
            Call<ResponseBody> call = googlePlaceAPI.getDetails(placeIDs.get(i), getString(R.string.google_api_key));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       retrofit2.Response<ResponseBody> response) {
                    Log.d("HTTP Response", response.toString());
                    if (response.isSuccessful()) {
                        JsonParser parser = new JsonParser();
                        JsonObject place = parser.parse(response.body().charStream()).getAsJsonObject().get("result").getAsJsonObject();
                        favorites.add(gson.fromJson(place, GooglePlace.class));
                        Collections.sort(favorites);
                        favoritesAdapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("HTTP Request", t.getMessage());
                }
            });
        }
        emptytext.setVisibility(View.GONE);
    }
}
