package com.example.android.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by I Kadek Aditya on 6/15/2017.
 */

public class FilmDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME       = "movielist.db";
    private static final int    DATABASE_VERSION    = 1;

    public FilmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIELIST_TABLE = "CREATE TABLE " + FilmContract.FavoriteEntry.TABLE_NAME + " (" +
                FilmContract.FavoriteEntry._ID              + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FilmContract.FavoriteEntry.COLUMN_TITLE     + " TEXT NOT NULL, " +
                FilmContract.FavoriteEntry.COLUMN_POSTER    + " TEXT NOT NULL, " +
                FilmContract.FavoriteEntry.COLUMN_OVERVIEW  + " TEXT NOT NULL, " +
                FilmContract.FavoriteEntry.COLUMN_RATING    + " REAL NOT NULL, " +
                FilmContract.FavoriteEntry.COLUMN_DATE      + " TEXT NOT NULL, " +
                FilmContract.FavoriteEntry.COLUMN_FILMID    + " INTEGER NOT NULL" +
                "); ";
        db.execSQL(SQL_CREATE_MOVIELIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FilmContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }

}
