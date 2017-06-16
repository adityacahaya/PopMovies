package com.example.android.popmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.film_class.DataDetailFilmReview;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by I Kadek Aditya on 6/14/2017.
 */

public class RecycleAdapterReview  extends RecyclerView.Adapter<RecycleAdapterReview.ReviewViewHolder> {

    private ArrayList<DataDetailFilmReview> mDataReview;

    public RecycleAdapterReview() {}

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.layout_rv_review;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        String mAuthor = mDataReview.get(position).getmAuthor();
        String mContent = mDataReview.get(position).getmContent();
        holder.tvAuthor.setText(mAuthor);
        holder.tvContent.setText(mContent);
    }

    @Override
    public int getItemCount() {
        if (null == mDataReview) return 0;
        return mDataReview.size();
    }

    public void setReviewData(ArrayList<DataDetailFilmReview> reviewData) {
        mDataReview = reviewData;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_author_review) TextView tvAuthor;
        @BindView(R.id.tv_content_review) TextView tvContent;

        public ReviewViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
