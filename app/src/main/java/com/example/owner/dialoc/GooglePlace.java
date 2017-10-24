package com.example.owner.dialoc;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by thomas on 10/23/17.
 */

public class GooglePlace {
    private String place_id;
    private String name;
    private double rating;
    private String address;
    private String international_phone_number;
    private double lat;
    private double lng;

    public static class GooglePlaceDeserializer implements JsonDeserializer<GooglePlace> {
        public GooglePlace deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject().get("result").getAsJsonObject();
            GooglePlace place = new GooglePlace();
            place.setName(obj.get("name").getAsString());
            place.setRating(obj.get("rating").getAsDouble());
            place.setInternational_phone_number(obj.get("international_phone_number").getAsString());
            place.setAddress(obj.get("formatted_address").getAsString());
            return place;
        }
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }


    public String getInternational_phone_number() {
        return international_phone_number;
    }

    public void setInternational_phone_number(String international_phone_number) {
        this.international_phone_number = international_phone_number;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}
