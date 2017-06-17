package com.example.android.popmovies.film_class;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by I Kadek Aditya on 6/14/2017.
 */

public class DataDetailFilm implements Parcelable {

    private String mTrailerKey;
    private String mTrailerName;
    private String mTrailerType;

    public DataDetailFilm(){}

    protected DataDetailFilm(Parcel in) {
        mTrailerKey = in.readString();
        mTrailerName = in.readString();
        mTrailerType = in.readString();
    }

    public String getmTrailerKey() {
        return mTrailerKey;
    }

    public void setmTrailerKey(String mTrailerKey) {
        this.mTrailerKey = mTrailerKey;
    }

    public String getmTrailerName() {
        return mTrailerName;
    }

    public void setmTrailerName(String mTrailerName) {
        this.mTrailerName = mTrailerName;
    }

    public String getmTrailerType() {
        return mTrailerType;
    }

    public void setmTrailerType(String mTrailerType) {
        this.mTrailerType = mTrailerType;
    }

    public static final Creator<DataDetailFilm> CREATOR = new Creator<DataDetailFilm>() {
        @Override
        public DataDetailFilm createFromParcel(Parcel in) {
            return new DataDetailFilm(in);
        }

        @Override
        public DataDetailFilm[] newArray(int size) {
            return new DataDetailFilm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTrailerKey);
        dest.writeString(mTrailerName);
        dest.writeString(mTrailerType);
    }

}
