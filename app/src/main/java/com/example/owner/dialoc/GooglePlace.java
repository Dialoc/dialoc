package com.example.owner.dialoc;

import android.util.Log;

import com.google.gson.JsonArray;
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
    private String placeId;
    private String name;
    private double rating;
    private String address;
    private String international_phone_number;
    private double lat;
    private double lng;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    private String distance;
    private String[] photoArray;

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    private String website;

    public String[] getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String[] openHours) {
        this.openHours = openHours;
    }

    private String[] openHours;

    public String[] getPhotoArray() {
        return photoArray;
    }

    public void setPhotoArray(String[] photoArray) {
        this.photoArray = photoArray;
    }

    public static class GooglePlaceDeserializer implements JsonDeserializer<GooglePlace> {
        public GooglePlace deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            GooglePlace place = new GooglePlace();
            place.setPlaceId(obj.get("place_id").getAsString());
            place.setName(obj.get("name").getAsString());
            if (obj.get("rating") != null) {
                place.setRating(obj.get("rating").getAsDouble());
            }
            if (obj.get("international_phone_number") != null) {
                place.setInternational_phone_number(obj.get("international_phone_number").getAsString());
            }
            if (obj.get("formatted_address") != null) {
                place.setAddress(obj.get("formatted_address").getAsString());
            }
            String[] photoRefs;
            if (obj.get("photos") != null) {
                JsonArray array = obj.get("photos").getAsJsonArray();
                photoRefs = new String[array.size() + 1];
                for (int i = 0; i < array.size(); i++) {
                    photoRefs[i] = array.get(0).getAsJsonObject().get("photo_reference").getAsString();
                }
            } else {
                photoRefs = new String[1];
            }
            photoRefs[photoRefs.length - 1] = "http://i.imgur.com/DvpvklR.png";
            place.setPhotoArray(photoRefs);
            if (obj.get("opening_hours") != null) {
                JsonArray hours = obj.get("opening_hours").getAsJsonObject().get("weekday_text").getAsJsonArray();
                String[] op_hours = new String[hours.size()];
                for (int i = 0; i < hours.size(); i++) {
                    op_hours[i] = hours.get(i).getAsString();
                }
                place.setOpenHours(op_hours);
            }
            if (obj.get("website") != null) {
                place.setWebsite(obj.get("website").getAsString());
            }
            if (obj.get("geometry") != null && obj.get("geometry").getAsJsonObject().get("location") != null) {
                JsonObject locObj = obj.get("geometry").getAsJsonObject().get("location").getAsJsonObject();
                place.setLat(locObj.get("lat").getAsDouble());
                place.setLng(locObj.get("lng").getAsDouble());
            }
            return place;
        }
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
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
