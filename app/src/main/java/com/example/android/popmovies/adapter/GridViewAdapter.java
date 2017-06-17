package com.example.android.popmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

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

    private String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";

    public GridViewAdapter(Context context, ArrayList<DataFilm> data){
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {return mData.size();}

    @Override
    public Object getItem(int position) {return null;}

    @Override
    public long getItemId(int position) {return 0;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;

        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.layout_gridview, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageview_in_gridview_main_activity);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        Picasso.with(mContext)
                .load(BASE_IMAGE_URL+mData.get(position).getmPoster())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(viewHolder.imageView);

        return convertView;
    }

    private static class ViewHolderItem{
        ImageView imageView;
    }
}
