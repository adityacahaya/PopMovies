package com.example.android.popmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popmovies.adapter.GridViewAdapter;
import com.example.android.popmovies.data.FilmContract;
import com.example.android.popmovies.data.FilmDbHelper;
import com.example.android.popmovies.film_class.DataDetailFilmReview;
import com.example.android.popmovies.film_class.DataFilm;
import com.example.android.popmovies.utilities.JsonUtils;
import com.example.android.popmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

import static android.util.Log.i;

public class MainActivity extends AppCompatActivity implements
        LoaderCallbacks<List<DataFilm>> {

    @BindView(R.id.spinner_main_activity) Spinner mSpinner;
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.gridView_main_activity) GridView mGridView;

    private ArrayList<DataFilm> dataFilmArrayList;

    public final static String SEND_DATA = "send-data-to-detail";
    public final static String GRIDVIEW_POSITION = "gridviewPosition";

    private static final int FILM_SEARCH_LOADER = 1;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setupSpinner();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        Log.i("ccc","save");
        state.putInt(GRIDVIEW_POSITION, mGridView.getFirstVisiblePosition());
        super.onSaveInstanceState(state);
    }

    // in my device this method can keep the position of GridView for 'Most Popular' or
    // 'High Rated' selection but this methoc can't keep the position of GridView for 'Favorite'
    // selection, because the metho ifSelectionFavorite() make set a new Adapter when the spinner
    // get select 'Favorite'. I must to use ifSelectionFavorite() in onResume() because if i click
    // favorite button in DetailActivity, the 'Favorite' selection must update with new Adapter
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("ccc","restore");
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            final int positionGrid = savedInstanceState.getInt(GRIDVIEW_POSITION);
            mGridView.post(new Runnable() {
                @Override
                public void run() {
                    mGridView.setSelection(positionGrid);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ccc","resume");
        ifSelectionFavorite();
    }

    public void ifSelectionFavorite(){
        String selection = (String) mSpinner.getSelectedItem();
        if (selection.equals(getString(R.string.favorite))) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            dataFilmArrayList = getFavoriteFilm();
            if (getFavoriteFilm().size() > 0) {
                showJsonDataView();
                mGridView.setAdapter(new GridViewAdapter(MainActivity.this, dataFilmArrayList));
            } else {
                String message = "You haven't add Favorite Film yet :(";
                mErrorMessageDisplay.setText(message);
                mGridView.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnItemClick(R.id.gridView_main_activity)
    void onItemClick(int position) {
        DataFilm dataFilm = dataFilmArrayList.get(position);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable(SEND_DATA, dataFilm);
        intent.putExtras(mBundle);
        startActivity(intent);
    }

    @OnItemSelected(R.id.spinner_main_activity)
    void onItemSelected() {
        String selection = (String) mSpinner.getSelectedItem();
        if (!TextUtils.isEmpty(selection)) {
            if (selection.equals(getString(R.string.popular))) {
                makeSearchQuery(getString(R.string.popular_param));
            } else if (selection.equals(getString(R.string.top_rated))) {
                makeSearchQuery(getString(R.string.top_rated_param));
            } else if (selection.equals(getString(R.string.favorite))) {
                ifSelectionFavorite();
            }
        }
    }

    private void setupSpinner() {
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_mainactivity_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner.setAdapter(spinnerAdapter);
    }

    private void makeSearchQuery(String serachQuery) {
        URL searchUrl = NetworkUtils.buildURL(serachQuery);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, searchUrl.toString());
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<DataFilm>> searchLoader = loaderManager.getLoader(FILM_SEARCH_LOADER);
        if (searchLoader == null) {
            loaderManager.initLoader(FILM_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(FILM_SEARCH_LOADER, queryBundle, this);
        }
    }

    private void showJsonDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mGridView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mGridView.setVisibility(View.INVISIBLE);
        String message = getString(R.string.error_message);
        mErrorMessageDisplay.setText(message);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<List<DataFilm>> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<List<DataFilm>>(this) {

            List<DataFilm> dataFilms;

            @Override
            protected void onStartLoading() {

                if (args == null) {
                    return;
                }

                if (dataFilms != null) {
                    deliverResult(dataFilms);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.INVISIBLE);
                    forceLoad();
                }

            }

            @Override
            public List<DataFilm> loadInBackground() {

                String searchQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);
                String searchJSONResult = null;
                List<DataFilm> searchResult = null;

                try {
                    URL searchUrl = new URL(searchQueryUrlString);
                    searchJSONResult = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                    searchResult = JsonUtils.getStringsFromJson(MainActivity.this, searchJSONResult);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return searchResult;

            }

            @Override
            public void deliverResult(List<DataFilm> data) {

                dataFilms = data;
                super.deliverResult(data);

            }

        };

    }

    @Override
    public void onLoadFinished(Loader<List<DataFilm>> loader, List<DataFilm> dataFilms) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (dataFilms != null) {
            showJsonDataView();
            dataFilmArrayList = (ArrayList) dataFilms;
            mGridView.setAdapter(new GridViewAdapter(MainActivity.this, dataFilmArrayList));
        } else {
            showErrorMessage();
        }

    }

    @Override
    public void onLoaderReset(Loader<List<DataFilm>> loader) {

    }

    public ArrayList<DataFilm> getFavoriteFilm() {

        ArrayList<DataFilm> dataFilms = new ArrayList<>();

        Cursor c = getContentResolver().query(FilmContract.FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (c.moveToFirst()) {
            do {
                DataFilm dataFilm = new DataFilm();
                dataFilm.setmTitle(c.getString(c.getColumnIndex(FilmContract.FavoriteEntry.COLUMN_TITLE)));
                dataFilm.setmPoster(c.getString(c.getColumnIndex(FilmContract.FavoriteEntry.COLUMN_POSTER)));
                dataFilm.setmOverview(c.getString(c.getColumnIndex(FilmContract.FavoriteEntry.COLUMN_OVERVIEW)));
                dataFilm.setmRating(c.getDouble(c.getColumnIndex(FilmContract.FavoriteEntry.COLUMN_RATING)));
                dataFilm.setmRelaseDate(c.getString(c.getColumnIndex(FilmContract.FavoriteEntry.COLUMN_DATE)));
                dataFilm.setmFilmId(c.getLong(c.getColumnIndex(FilmContract.FavoriteEntry.COLUMN_FILMID)));
                dataFilms.add(dataFilm);
            } while (c.moveToNext());
        }
        c.close();

        return dataFilms;

    }

}
