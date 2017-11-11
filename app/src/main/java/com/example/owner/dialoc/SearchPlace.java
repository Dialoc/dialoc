package com.example.owner.dialoc;

import java.net.URL;

/**
* Holds information from clinics that have been searched
*/

public class SearchPlace {
    private final String name;
    private final String address;
    private final String placeId;
    private final String icon;

    public SearchPlace(String name, String address, String placeId, String icon) {
        this.name = name;
        this.address = address;
        this.placeId = placeId;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getIcon() {
        return icon;
    }
}
