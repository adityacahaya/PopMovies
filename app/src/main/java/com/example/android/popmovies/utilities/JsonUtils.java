package com.example.android.popmovies.utilities;

import android.content.Context;

import com.example.android.popmovies.film_class.DataDetailFilm;
import com.example.android.popmovies.film_class.DataDetailFilmReview;
import com.example.android.popmovies.film_class.DataFilm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by I Kadek Aditya on 6/13/2017.
 */

public final class JsonUtils {

    public static List<DataFilm> getStringsFromJson(Context context, String jsonStr)
            throws JSONException {

        final String RESULT = "results";
        final String TITLE = "title";
        final String POSTER = "poster_path";
        final String OVERVIEW = "overview";
        final String RATING = "vote_average";
        final String RELASE_DATE = "release_date";
        final String FILM_ID = "id";

        List<DataFilm> parsedData = new ArrayList<>();
        JSONObject dataJson = new JSONObject(jsonStr);
        JSONArray dataResultArray = dataJson.getJSONArray(RESULT);

        for (int i = 0; i < dataResultArray.length(); i++) {
            String mTitle;
            String mPoster;
            String mOverview;
            double mRating;
            String mRelaseDate;
            long mFilmId;

            JSONObject dataFilm = dataResultArray.getJSONObject(i);
            mTitle = dataFilm.getString(TITLE);
            mPoster = dataFilm.getString(POSTER);
            mOverview = dataFilm.getString(OVERVIEW);
            mRating = dataFilm.getDouble(RATING);
            mRelaseDate = dataFilm.getString(RELASE_DATE);
            mFilmId = dataFilm.getLong(FILM_ID);

            DataFilm film = new DataFilm();
            film.setmTitle(mTitle);
            film.setmPoster(mPoster);
            film.setmOverview(mOverview);
            film.setmRating(mRating);
            film.setmRelaseDate(mRelaseDate);
            film.setmFilmId(mFilmId);
            parsedData.add(film);
        }

        return parsedData;

    }

    public static List<DataDetailFilm> getStringsFromJsonTrailer(Context context, String jsonStrTrailer)
            throws JSONException {

        final String RESULT = "results";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";
        final String TRAILER_TYPE = "type";

        List<DataDetailFilm> parsedData = new ArrayList<>();
        JSONObject dataJsonTrailer = new JSONObject(jsonStrTrailer);
        JSONArray dataResultArrayTrailer = dataJsonTrailer.getJSONArray(RESULT);

        for (int i = 0; i < dataResultArrayTrailer.length(); i++) {
            String mTrailerKey;
            String mTraileName;
            String mTrailerType;

            JSONObject dataFilm = dataResultArrayTrailer.getJSONObject(i);
            mTrailerKey = dataFilm.getString(TRAILER_KEY);
            mTraileName = dataFilm.getString(TRAILER_NAME);
            mTrailerType = dataFilm.getString(TRAILER_TYPE);

            DataDetailFilm detailFilm = new DataDetailFilm();
            detailFilm.setmTrailerKey(mTrailerKey);
            detailFilm.setmTrailerName(mTraileName);
            detailFilm.setmTrailerType(mTrailerType);
            parsedData.add(detailFilm);
        }

        return parsedData;

    }

    public static List<DataDetailFilmReview> getStringsFromJsonReview(Context context, String jsonStrReview)
            throws JSONException {

        final String RESULT = "results";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";

        List<DataDetailFilmReview> parsedData = new ArrayList<>();
        JSONObject dataJsonReview = new JSONObject(jsonStrReview);
        JSONArray dataResultArrayReview = dataJsonReview.getJSONArray(RESULT);

        for (int i = 0; i < dataResultArrayReview.length(); i++) {
            String mReviewAuthor;
            String mReviewContent;

            JSONObject dataFilm = dataResultArrayReview.getJSONObject(i);
            mReviewAuthor = dataFilm.getString(REVIEW_AUTHOR);
            mReviewContent = dataFilm.getString(REVIEW_CONTENT);

            DataDetailFilmReview detailFilm = new DataDetailFilmReview();
            detailFilm.setmAuthor(mReviewAuthor);
            detailFilm.setmContent(mReviewContent);
            parsedData.add(detailFilm);
        }

        return parsedData;

    }

}
