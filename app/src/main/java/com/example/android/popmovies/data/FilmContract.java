package com.example.android.popmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by I Kadek Aditya on 6/15/2017.
 */

public class FilmContract {

    public static final String  AUTHORITY        = "com.example.android.popmovies";
    public static final Uri     BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String  PATH_TASKS       = "movielist";

    public static final class FavoriteEntry implements BaseColumns{
        public static final Uri    CONTENT_URI      = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String TABLE_NAME       = "movielist";
        public static final String COLUMN_TITLE     = "title";
        public static final String COLUMN_POSTER    = "poster";
        public static final String COLUMN_OVERVIEW  = "overview";
        public static final String COLUMN_RATING    = "rating";
        public static final String COLUMN_DATE      = "date";
        public static final String COLUMN_FILMID    = "filmid";
    }

}
