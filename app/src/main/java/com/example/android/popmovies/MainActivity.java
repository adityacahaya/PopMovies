package com.example.android.popmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.popmovies.adapter.GridViewAdapter;
import com.example.android.popmovies.data.FilmContract;
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

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.spinner_main_activity) Spinner mSpinner;
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.gridView_main_activity) GridView mGridView;

    private ArrayList<DataFilm> dataFilmArrayList;

    public final static String SEND_DATA = "send-data-to-detail";
    public final static String GRIDVIEW_POSITION = "gridviewPosition";

    private static final int FILM_SEARCH_LOADER = 1;
    private static final int DB_SEARCH_LOADER = 2;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    private Bundle bundle;
    private int flag = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setupSpinner();

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("ccc", "onSpinnerSelected");
                String selection = (String) mSpinner.getSelectedItem();

                if (selection.equals(getString(R.string.popular))) {
                    Log.i("ccc", "onPopular");
                    if (savedInstanceState != null){
                        Log.i("ccc","Popular saveInstance");
                        flag = savedInstanceState.getInt("FLAG");
                        if (flag == 1){
                            dataFilmArrayList = savedInstanceState.getParcelableArrayList("DATA_GRIDVIEW_NOW");
                            int positionGrid = savedInstanceState.getInt(GRIDVIEW_POSITION);
                            mGridView.setAdapter(new GridViewAdapter(MainActivity.this, dataFilmArrayList));
                            mGridView.setSelection(positionGrid);
                        }else{
                            new CallbackPopularAndTopRated().makeSearchQuery(getString(R.string.popular_param));
                            flag = 1;
                        }
                    }else {
                        new CallbackPopularAndTopRated().makeSearchQuery(getString(R.string.popular_param));
                        flag = 1;
                    }
                }

                if (selection.equals(getString(R.string.top_rated))) {
                    Log.i("ccc", "onTopRate");
                    if (savedInstanceState != null){
                        Log.i("ccc","TopRated saveInstance");
                        flag = savedInstanceState.getInt("FLAG");
                        if (flag == 2){
                            dataFilmArrayList = savedInstanceState.getParcelableArrayList("DATA_GRIDVIEW_NOW");
                            int positionGrid = savedInstanceState.getInt(GRIDVIEW_POSITION);
                            mGridView.setAdapter(new GridViewAdapter(MainActivity.this, dataFilmArrayList));
                            mGridView.setSelection(positionGrid);
                        }else{
                            new CallbackPopularAndTopRated().makeSearchQuery(getString(R.string.top_rated_param));
                            flag = 2;
                        }
                    }else {
                        new CallbackPopularAndTopRated().makeSearchQuery(getString(R.string.top_rated_param));
                        flag = 2;
                    }
                }

                if (selection.equals(getString(R.string.favorite))) {
                    Log.i("ccc", "onFavorite");
                    if (savedInstanceState != null){
                        Log.i("ccc","Favorite saveInstance");
                        flag = savedInstanceState.getInt("FLAG");
                        if (flag == 3){
                            dataFilmArrayList = savedInstanceState.getParcelableArrayList("DATA_GRIDVIEW_NOW");
                            int positionGrid = savedInstanceState.getInt(GRIDVIEW_POSITION);
                            mGridView.setAdapter(new GridViewAdapter(MainActivity.this, dataFilmArrayList));
                            mGridView.setSelection(positionGrid);
                        }else{
                            new CallbackFavorite().makeSearchQuery();
                            flag = 3;
                        }
                    }else {
                        new CallbackFavorite().makeSearchQuery();
                        flag = 3;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        Log.i("ccc","save MainActivity");

        state.putParcelableArrayList("DATA_GRIDVIEW_NOW",dataFilmArrayList);
        state.putInt(GRIDVIEW_POSITION, mGridView.getFirstVisiblePosition());
        state.putInt("FLAG",flag);
        bundle = state;

        super.onSaveInstanceState(state);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ccc","resume");
        ifFavoriteSelection();
    }

    public void ifFavoriteSelection(){
        String selection = (String) mSpinner.getSelectedItem();
        if (!TextUtils.isEmpty(selection)) {
            if (selection.equals(getString(R.string.favorite))) {
                Log.i("ccc", "onFavorite");
                new CallbackFavorite().makeSearchQuery();
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

    private void setupSpinner() {
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_mainactivity_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner.setAdapter(spinnerAdapter);
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

    private class CallbackFavorite implements LoaderManager.LoaderCallbacks<List<DataFilm>>{

        public void makeSearchQuery() {
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<List<DataFilm>> searchLoader = loaderManager.getLoader(DB_SEARCH_LOADER);
            if (searchLoader == null) {
                Log.i("ccc","init");
                loaderManager.initLoader(DB_SEARCH_LOADER, null, this);
            } else {
                loaderManager.restartLoader(DB_SEARCH_LOADER, null, this);
            }
        }

        @Override
        public Loader<List<DataFilm>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<DataFilm>>(MainActivity.this){
                List<DataFilm> dataFilms;
                @Override
                protected void onStartLoading() {
                    if (dataFilms != null) {
                        Log.i("ccc","deliver2");
                        deliverResult(dataFilms);
                    } else {
                        forceLoad();
                    }
                }
                @Override
                public List<DataFilm> loadInBackground() {
                    Log.i("ccc","load");
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
                @Override
                public void deliverResult(List<DataFilm> data) {
                    Log.i("ccc","deliverResult2");
                    dataFilms = data;
                    super.deliverResult(data);
                    if (bundle != null){
                        int position = bundle.getInt(GRIDVIEW_POSITION);
                        mGridView.setSelection(position);
                    }
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
    }

    private class CallbackPopularAndTopRated implements LoaderCallbacks<List<DataFilm>>{

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

        @Override
        public Loader<List<DataFilm>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<DataFilm>>(MainActivity.this) {
                List<DataFilm> dataFilms;
                @Override
                protected void onStartLoading() {
                    if (args == null) {
                        return;
                    }
                    if (dataFilms != null) {
                        Log.i("ccc","DeliverResult");
                        deliverResult(dataFilms);
                    } else {
                        Log.i("ccc","LoadNew");
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
                Log.i("ccc","onLoadFinish");
                dataFilmArrayList = (ArrayList) dataFilms;
                mGridView.setAdapter(new GridViewAdapter(MainActivity.this, dataFilmArrayList));
            } else {
                showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<DataFilm>> loader) {

        }

    }
}
