package com.example.android.popmovies.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.film_class.DataFilm;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by I Kadek Aditya on 6/13/2017.
 */

public class GridViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<DataFilm> mData;
    SparseBooleanArray booleanArray = new SparseBooleanArray();
    private final LayoutInflater mInflater;

    private String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";

    public GridViewAdapter(Context context, ArrayList<DataFilm> data){
        mContext = context;
        mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {return mData.size();}

    @Override
    public Object getItem(int position) {return null;}

    @Override
    public long getItemId(int position) {return 0;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        if (convertView == null) {
            grid = mInflater.inflate(R.layout.layout_gridview, parent, false);
            grid.setTag(R.id.imageview_in_gridview_main_activity, grid.findViewById(R.id.imageview_in_gridview_main_activity));
        } else {
            grid = (View) convertView;
        }
        ImageView imageView = (ImageView)grid.findViewById(R.id.imageview_in_gridview_main_activity);
        Picasso.with(mContext)
                .load(BASE_IMAGE_URL+mData.get(position).getmPoster())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imageView);
        if(booleanArray.get(position)){
            grid.setActivated(true);
        } else {
            grid.setActivated(false);
        }
        return grid;
    }
}
