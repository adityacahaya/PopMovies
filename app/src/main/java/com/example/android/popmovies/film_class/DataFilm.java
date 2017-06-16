package com.example.android.popmovies.film_class;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by I Kadek Aditya on 6/13/2017.
 */

public class DataFilm implements Parcelable {

    private String mTitle;
    private String mPoster;
    private String mOverview;
    private double mRating;
    private String mRelaseDate;
    private long mFilmId;

    public String getmTitle() {
        return mTitle;
    }

    public String getmPoster() {
        return mPoster;
    }

    public String getmOverview() {
        return mOverview;
    }

    public double getmRating() {
        return mRating;
    }

    public String getmRelaseDate() {
        return mRelaseDate;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmPoster(String mPoster) {
        this.mPoster = mPoster;
    }

    public void setmOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public void setmRating(double mRating) {
        this.mRating = mRating;
    }

    public void setmRelaseDate(String mRelaseDate) {
        this.mRelaseDate = mRelaseDate;
    }

    public long getmFilmId() {
        return mFilmId;
    }

    public void setmFilmId(long mFilmId) {
        this.mFilmId = mFilmId;
    }

    public static final Parcelable.Creator<DataFilm> CREATOR = new Creator<DataFilm>() {

        public DataFilm createFromParcel(Parcel source) {
            DataFilm dataFilm = new DataFilm();
            dataFilm.mTitle = source.readString();
            dataFilm.mPoster = source.readString();
            dataFilm.mOverview = source.readString();
            dataFilm.mRating = source.readDouble();
            dataFilm.mRelaseDate = source.readString();
            dataFilm.mFilmId = source.readLong();
            return dataFilm;
        }

        @Override
        public DataFilm[] newArray(int size) {
            return new DataFilm[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mPoster);
        dest.writeString(mOverview);
        dest.writeDouble(mRating);
        dest.writeString(mRelaseDate);
        dest.writeLong(mFilmId);
    }

}
