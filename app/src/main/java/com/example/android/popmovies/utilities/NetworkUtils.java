package com.example.android.popmovies.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by I Kadek Aditya on 6/13/2017.
 */

public final class NetworkUtils {

    final static String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";
    final static String API                 = "api_key";
    final static String API_KEY             = "7816046b04d877538939fefba8a79125";

    final static String TRAILER = "videos";
    final static String REVIEWS = "reviews";

    public static URL buildURL(String searchQuery){
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(searchQuery)
                .appendQueryParameter(API,API_KEY)
                .build();
        URL url = null;
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildURLforTrailer(String filmId){
        URL url = null;
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(filmId)
                .appendPath(TRAILER)
                .appendQueryParameter(API,API_KEY)
                .build();
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildURLforReview(String filmId){
        URL url = null;
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(filmId)
                .appendPath(REVIEWS)
                .appendQueryParameter(API,API_KEY)
                .build();
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
