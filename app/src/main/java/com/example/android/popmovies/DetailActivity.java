package com.example.android.popmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popmovies.adapter.RecycleAdapter;
import com.example.android.popmovies.adapter.RecycleAdapterReview;
import com.example.android.popmovies.data.FilmContract;
import com.example.android.popmovies.film_class.DataDetailFilm;
import com.example.android.popmovies.film_class.DataDetailFilmReview;
import com.example.android.popmovies.film_class.DataFilm;
import com.example.android.popmovies.utilities.JsonUtils;
import com.example.android.popmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.widget.Toast.makeText;
import static com.example.android.popmovies.MainActivity.SEND_DATA;

public class DetailActivity extends AppCompatActivity implements
        RecycleAdapter.RecycleAdapterOnClickHandler{

    @BindView(R.id.poster_detail) ImageView mPoster;
    @BindView(R.id.relase_detail_view) TextView mRelaseDate;
    @BindView(R.id.rating_detail_view) TextView mRating;
    @BindView(R.id.overview_detail_view) TextView mOverview;
    @BindView(R.id.favorite_detail_view) TextView mFavorite;
    @BindView(R.id.scroll) NestedScrollView mScrollView;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rv_trailer) RecyclerView mRecyclerView;
    @BindView(R.id.rv_review) RecyclerView mRecyclerViewReview;


    private DataFilm dataFilm;

    private String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";
    private String BASE_TRAILER_URL = "http://www.youtube.com/watch?v=";

    private ArrayList<DataDetailFilm> dataDetailFilmArrayList;
    private ArrayList<DataDetailFilmReview> dataDetailFilmReviewArrayList;


    private RecycleAdapter mRecycleAdapter;
    private RecycleAdapterReview mRecycleAdapterReview;

    private static final String LIFECYCLE_CALLBACKS_TEXT_KEY = "callbacks";
    private static final String ARTICLE_SCROLL_POSITION = "ARTICLE_SCROLL_POSITION";
    private static final String TRAILER_SAVE_INSTANCE = "TRAILER_SAVE_INSTANCE";
    private static final String REVIEW_SAVE_INSTANCE = "REVIEW_SAVE_INSTANCE";

    private static final int TRAILER_SEARCH_LOADER = 1;
    private static final String TRAILER_URL_EXTRA = "query-trailer";
    private static final int REVIEW_SEARCH_LOADER = 2;
    private static final String REVIEW_URL_EXTRA = "query-review";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(SEND_DATA)) {
            dataFilm = intent.getParcelableExtra(SEND_DATA);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar.setTitle(dataFilm.getmTitle());

        Picasso.with(this)
                .load(BASE_IMAGE_URL+dataFilm.getmPoster())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(mPoster);
        String[] date = dataFilm.getmRelaseDate().split("-");
        mRelaseDate.setText(date[0]);
        mRating.setText(String.valueOf(dataFilm.getmRating()));
        mOverview.setText(dataFilm.getmOverview());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecycleAdapter = new RecycleAdapter(this);
        mRecyclerView.setAdapter(mRecycleAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManagerReview = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewReview.setLayoutManager(layoutManagerReview);
        mRecyclerViewReview.setHasFixedSize(true);
        mRecycleAdapterReview = new RecycleAdapterReview();
        mRecyclerViewReview.setAdapter(mRecycleAdapterReview);
        mRecyclerViewReview.setNestedScrollingEnabled(false);

        if (checkFilm()){
            mFavorite.setTextColor(ResourcesCompat.getColor(getResources(), R.color.yelow_favorite, null));
        }else{
            mFavorite.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        }

        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(LIFECYCLE_CALLBACKS_TEXT_KEY)) {
                int allPreviousLifecycleCallbacks = savedInstanceState
                        .getInt(LIFECYCLE_CALLBACKS_TEXT_KEY);
                mFavorite.setTextColor(allPreviousLifecycleCallbacks);
            }

            dataDetailFilmArrayList = savedInstanceState.getParcelableArrayList(TRAILER_SAVE_INSTANCE);
            mRecycleAdapter.setTrailerData(dataDetailFilmArrayList);
            dataDetailFilmReviewArrayList = savedInstanceState.getParcelableArrayList(REVIEW_SAVE_INSTANCE);
            mRecycleAdapterReview.setReviewData(dataDetailFilmReviewArrayList);

            final int[] position = savedInstanceState.getIntArray(ARTICLE_SCROLL_POSITION);
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });
        }else{
            makeSearchQuery(String.valueOf(dataFilm.getmFilmId()));
        }
    }

    private void makeSearchQuery(String filmId){
        URL searchTrailer = NetworkUtils.buildURLforTrailer(filmId);
        Log.i("trailer",searchTrailer.toString());
        URL searchReview = NetworkUtils.buildURLforReview(filmId);
        Log.i("review",searchReview.toString());

        new CallbackTrailer().makeSearchQuery(searchTrailer.toString());
        new CallbackReview().makeSearchQuery(searchReview.toString());
    }

    public void openTrailer(String idYoutube) {
        Uri webpage = Uri.parse(BASE_TRAILER_URL+idYoutube);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onClick(DataDetailFilm data) {
        openTrailer(data.getmTrailerKey());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int color = mFavorite.getCurrentTextColor();
        outState.putInt(LIFECYCLE_CALLBACKS_TEXT_KEY, color);

        outState.putParcelableArrayList(TRAILER_SAVE_INSTANCE,dataDetailFilmArrayList);
        outState.putParcelableArrayList(REVIEW_SAVE_INSTANCE,dataDetailFilmReviewArrayList);

        outState.putIntArray(ARTICLE_SCROLL_POSITION, new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});

        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.favorite_detail_view)
    public void onClick() {
        String messageFavorite = "Save This to Favorite";
        Toast toastFavorite = Toast.makeText(this,messageFavorite,Toast.LENGTH_SHORT);
        String messageNotFavorite = "Delete from Favorite";
        Toast toastNotFavorite = Toast. makeText(this,messageNotFavorite,Toast.LENGTH_SHORT);

        if (mFavorite.getCurrentTextColor() == ResourcesCompat.getColor(getResources(), R.color.white, null)) {
            mFavorite.setTextColor(ResourcesCompat.getColor(getResources(), R.color.yelow_favorite, null));
            addFavoriteFilm(dataFilm.getmTitle(),dataFilm.getmPoster(),dataFilm.getmOverview(),dataFilm.getmRating(),dataFilm.getmRelaseDate(),dataFilm.getmFilmId());
            toastNotFavorite.cancel();
            toastFavorite.show();
        }else{
            mFavorite.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            deleteFavoriteFilm(dataFilm.getmFilmId());
            toastFavorite.cancel();
            toastNotFavorite.show();
        }
    }

    private class CallbackTrailer implements LoaderManager.LoaderCallbacks<List<DataDetailFilm>> {

        public void makeSearchQuery(String searchTrailer){
            Bundle queryBundle = new Bundle();
            queryBundle.putString(TRAILER_URL_EXTRA, searchTrailer);
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<List<DataDetailFilm>> searchTrailerLoader = loaderManager.getLoader(TRAILER_SEARCH_LOADER);
            if (searchTrailerLoader == null) {
                loaderManager.initLoader(TRAILER_SEARCH_LOADER, queryBundle, this);
            } else {
                loaderManager.restartLoader(TRAILER_SEARCH_LOADER, queryBundle, this);
            }
        }

        @Override
        public Loader<List<DataDetailFilm>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<DataDetailFilm>>(DetailActivity.this) {
                List<DataDetailFilm> dataFilms;
                @Override
                protected void onStartLoading() {
                    if (args == null) {
                        return;
                    }
                    if (dataFilms != null) {
                        deliverResult(dataFilms);
                    } else {
                        forceLoad();
                    }
                }
                @Override
                public List<DataDetailFilm> loadInBackground() {
                    String searchQueryUrlString = args.getString(TRAILER_URL_EXTRA);
                    String searchJSONResultTrailer;
                    List<DataDetailFilm> searchResultTrailer = null;
                    try {
                        URL searchUrl = new URL(searchQueryUrlString);
                        searchJSONResultTrailer = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                        searchResultTrailer = JsonUtils.getStringsFromJsonTrailer(DetailActivity.this,searchJSONResultTrailer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return searchResultTrailer;
                }
                @Override
                public void deliverResult(List<DataDetailFilm> data) {
                    dataFilms = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<DataDetailFilm>> loader, List<DataDetailFilm> dataDetailFilms) {
            if (dataDetailFilms != null) {
                dataDetailFilmArrayList = (ArrayList) dataDetailFilms;
                mRecycleAdapter.setTrailerData(dataDetailFilmArrayList);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<DataDetailFilm>> loader) {

        }

    }

    private class CallbackReview implements LoaderManager.LoaderCallbacks<List<DataDetailFilmReview>> {

        public void makeSearchQuery(String searchReview){
            Bundle queryBundle = new Bundle();
            queryBundle.putString(REVIEW_URL_EXTRA, searchReview);
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<List<DataDetailFilmReview>> searchReviewLoader = loaderManager.getLoader(REVIEW_SEARCH_LOADER);
            if (searchReviewLoader == null) {
                loaderManager.initLoader(REVIEW_SEARCH_LOADER, queryBundle, this);
            } else {
                loaderManager.restartLoader(REVIEW_SEARCH_LOADER, queryBundle, this);
            }
        }

        @Override
        public Loader<List<DataDetailFilmReview>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<DataDetailFilmReview>>(DetailActivity.this) {
                List<DataDetailFilmReview> dataFilms;
                @Override
                protected void onStartLoading() {
                    if (args == null) {
                        return;
                    }
                    if (dataFilms != null) {
                        deliverResult(dataFilms);
                    } else {
                        forceLoad();
                    }
                }
                @Override
                public List<DataDetailFilmReview> loadInBackground() {
                    String searchQueryUrlString = args.getString(REVIEW_URL_EXTRA);
                    String searchJSONResultReview;
                    List<DataDetailFilmReview> searchResultReview = null;
                    try {
                        URL searchReview = new URL(searchQueryUrlString);
                        searchJSONResultReview = NetworkUtils.getResponseFromHttpUrl(searchReview);
                        searchResultReview = JsonUtils.getStringsFromJsonReview(DetailActivity.this,searchJSONResultReview);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return searchResultReview;
                }
                @Override
                public void deliverResult(List<DataDetailFilmReview> data) {
                    dataFilms = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<DataDetailFilmReview>> loader, List<DataDetailFilmReview> data) {
            if (data != null) {
                dataDetailFilmReviewArrayList = (ArrayList) data;
                mRecycleAdapterReview.setReviewData(dataDetailFilmReviewArrayList);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<DataDetailFilmReview>> loader) {

        }

    }

    private Uri addFavoriteFilm(String title, String poster, String overview, double rating, String date, long filmid) {
        ContentValues cv = new ContentValues();
        cv.put(FilmContract.FavoriteEntry.COLUMN_TITLE, title);
        cv.put(FilmContract.FavoriteEntry.COLUMN_POSTER, poster);
        cv.put(FilmContract.FavoriteEntry.COLUMN_OVERVIEW, overview);
        cv.put(FilmContract.FavoriteEntry.COLUMN_RATING, rating);
        cv.put(FilmContract.FavoriteEntry.COLUMN_DATE, date);
        cv.put(FilmContract.FavoriteEntry.COLUMN_FILMID, filmid);
        Uri uri = getContentResolver().insert(FilmContract.FavoriteEntry.CONTENT_URI, cv);
        return uri;
    }

    private boolean checkFilm(){
        boolean favoriteYes = false;
        Cursor c = getContentResolver().query(FilmContract.FavoriteEntry.CONTENT_URI,
                null,
                FilmContract.FavoriteEntry.COLUMN_FILMID+"=?",
                new String[]{String.valueOf(dataFilm.getmFilmId())},
                null);
        if (c.getCount() > 0) {
            favoriteYes = true;
        }
        return favoriteYes;
    }

    private int deleteFavoriteFilm(long filmid){
        return getContentResolver().delete(FilmContract.FavoriteEntry.CONTENT_URI,
                FilmContract.FavoriteEntry.COLUMN_FILMID+"=?",
                new String[]{String.valueOf(filmid)});
    }

}
