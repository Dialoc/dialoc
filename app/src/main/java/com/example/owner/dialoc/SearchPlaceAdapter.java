package com.example.owner.dialoc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.owner.dialoc.R;
import com.example.owner.dialoc.SearchPlace;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class SearchPlaceAdapter extends RecyclerView.Adapter<SearchPlaceAdapter.MyViewHolder>{

    private List<GooglePlace> searchPlaceList;
    private String name;
//    private RequestQueue queue;
    private Context context;
//    private SearchPlace current;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ViewPager avatar;
        public TextView address;

        public MyViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.search_clinic_name);
            avatar = (ViewPager) view.findViewById(R.id.search_clinic_avatar);
            address = (TextView) view.findViewById(R.id.search_clinic_address);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, ClinicActivity.class);
                    intent.putExtra("PLACE_ID", searchPlaceList.get(position).getPlaceId());
                    context.startActivity(intent);
                }
            });
        }

    }

    public SearchPlaceAdapter(List<GooglePlace> searchPlaceList, Context context) {
        this.searchPlaceList = searchPlaceList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_clinic_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GooglePlace result = searchPlaceList.get(position);
        holder.avatar.setAdapter(new ImagePagerAdapter(holder.itemView.getContext(), result.getPhotoArray()));

        holder.name.setText(result.getName());
        holder.address.setText(result.getDistance());
    }

    @Override
    public int getItemCount() {
        return searchPlaceList.size();
    }
}