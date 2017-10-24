package com.example.owner.dialoc;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by thomas on 10/23/17.
 */

public interface GooglePlaceAPI {
    @GET("maps/api/place/details/json?")
    Call<ResponseBody> getDetails(@Query("placeid") String id, @Query("key") String apiKey);
}
