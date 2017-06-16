package com.example.android.popmovies.adapter;

import android.content.Context;
import android.view.View.OnClickListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.film_class.DataDetailFilm;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by I Kadek Aditya on 6/14/2017.
 */

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.TrailerViewHolder> {

    private ArrayList<DataDetailFilm> mDataTrailer;
    private final RecycleAdapterOnClickHandler mClickHandler;

    public interface RecycleAdapterOnClickHandler {
        void onClick(DataDetailFilm data);
    }

    public RecycleAdapter(RecycleAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.layout_rv_trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        String mNameTrailer = mDataTrailer.get(position).getmTrailerName();
        String mTypeTrailer = mDataTrailer.get(position).getmTrailerType();
        holder.tvNameTrailer.setText(mNameTrailer);
        holder.tvTypeTrailer.setText(mTypeTrailer);
    }

    @Override
    public int getItemCount() {
        if (null == mDataTrailer) return 0;
        return mDataTrailer.size();
    }

    public void setTrailerData(ArrayList<DataDetailFilm> trailerData) {
        mDataTrailer = trailerData;
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        @BindView(R.id.tv_name_trailer) TextView tvNameTrailer;
        @BindView(R.id.tv_type_trailer) TextView tvTypeTrailer;

        public TrailerViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            DataDetailFilm data = mDataTrailer.get(adapterPosition);
            mClickHandler.onClick(data);
        }
    }
}
