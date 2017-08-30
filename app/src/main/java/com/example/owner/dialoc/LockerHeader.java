package com.example.owner.dialoc;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Owner on 12/14/2016.
 */

public class LockerHeader {
    private Uri image;

    public LockerHeader(Uri image) {
        this.image = image;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}
