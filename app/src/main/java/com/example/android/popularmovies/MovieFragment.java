package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.support.v4.app.LoaderManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import java.util.ArrayList;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.android.popularmovies.data.MovieContract;

/**
 * The fragment for the home screen page displaying all the movies
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieAdapter movieAdapter; //the adapter to populate the gridview in fragment_main
    public static int deviceHeight; //the deviceheight in order to set the minimum height for each movie poster image(look at movieadapter.java)
    private final String LOG_TAG = MovieFragment.class.getSimpleName(); //for logging/debugging purposes

    private static final int MOVIE_LOADER = 0;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] MOVIE_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.MovieGeneral._ID,
            MovieContract.MovieGeneral.COLUMN_MOVIE_ID,
            MovieContract.MovieGeneral.COLUMN_MOVIE_TITLE,
            MovieContract.MovieGeneral.COLUMN_POSTER_PATH,
            MovieContract.MovieGeneral.COLUMN_RELEASE_DATE,
            MovieContract.MovieGeneral.COLUMN_USER_RATING,
            MovieContract.MovieGeneral.COLUMN_MOVIE_SYNOPSIS,
            MovieContract.MovieGeneral.COLUMN_USER_FAVORITES
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_TABLE_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_POSTER_PATH = 3;
    static final int COL_RELEASE_DATE = 4;
    static final int COL_USER_RATING = 5;
    static final int COL_MOVIE_SYNOPSIS = 6;
    static final int COL_USER_FAVORITES = 7;

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        deviceHeight = display.getHeight();

        movieAdapter = new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(movieAdapter);

        // We'll call our DetailActivity
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    long movie_id = cursor.getLong(COL_MOVIE_ID);
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MovieContract.MovieDetail.buildDetailWithId(movie_id));
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    private void updateMovieData(){
        FetchMovieTask artistTask = new FetchMovieTask(getActivity(), movieAdapter);
        artistTask.execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovieData();
    }

    // since we read the location when we create the loader, all we need to do is restart things
    void onSortChanged() {
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = null;
        if(Utility.sortByRatings(getActivity()))
            sortOrder = MovieContract.MovieGeneral.COLUMN_USER_RATING + " DESC";
        Uri movieUri = MovieContract.MovieGeneral.CONTENT_URI;
        return new CursorLoader(getActivity(),
                movieUri,
                MOVIE_COLUMNS,
                null,
                null,
                sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }

} //end MovieFragment
