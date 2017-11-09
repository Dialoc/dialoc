package com.example.owner.dialoc;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by thomas on 11/4/17.
 */

public class ImagePagerAdapter extends PagerAdapter {

    Context context;
    String[] images;
    LayoutInflater layoutInflater;


    public ImagePagerAdapter(Context context, String[] images) {
        this.context = context;
        this.images = images;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.single_image, container, false);

        ImageView imageView = itemView.findViewById(R.id.image);
        String url;
        if (position < images.length - 1) {
            url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                    + images[position] + "&key=" + context.getString(R.string.google_api_key);
        } else {
            url = images[position];
        }
        Picasso.with(context).load(url).into(imageView);
        container.addView(itemView);

        //listening to image click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "you clicked image " + (position + 1), Toast.LENGTH_LONG).show();
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
