package com.example.android.popmovies.film_class;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by I Kadek Aditya on 6/14/2017.
 */

public class DataDetailFilmReview implements Parcelable{

    private String mAuthor;
    private String mContent;

    public DataDetailFilmReview(){}

    protected DataDetailFilmReview(Parcel in) {
        mAuthor = in.readString();
        mContent = in.readString();
    }

    public static final Creator<DataDetailFilmReview> CREATOR = new Creator<DataDetailFilmReview>() {

        @Override
        public DataDetailFilmReview createFromParcel(Parcel in) {
            return new DataDetailFilmReview(in);
        }

        @Override
        public DataDetailFilmReview[] newArray(int size) {
            return new DataDetailFilmReview[size];
        }

    };

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAuthor);
        dest.writeString(mContent);
    }
}
