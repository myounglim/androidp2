package com.example.android.popularmovies;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;


/**
 * Details when the user clicks on a specific movie. A lot of the formatting will be changed for p2
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 0;
    Uri mUri;

    private static final String[] MOVIE_DETAIL_COLUMNS = {
            MovieContract.MovieGeneral.TABLE_NAME + "." + MovieContract.MovieGeneral._ID,
            MovieContract.MovieGeneral.COLUMN_MOVIE_ID,
            MovieContract.MovieGeneral.COLUMN_MOVIE_TITLE,
            MovieContract.MovieGeneral.COLUMN_POSTER_PATH,
            MovieContract.MovieGeneral.COLUMN_RELEASE_DATE,
            MovieContract.MovieGeneral.COLUMN_USER_RATING,
            MovieContract.MovieGeneral.COLUMN_MOVIE_SYNOPSIS,
            MovieContract.MovieGeneral.COLUMN_USER_FAVORITES,
            MovieContract.MovieDetail.COLUMN_TRAILER_PATH,
            MovieContract.MovieDetail.COLUMN_REVIEW_PATH
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    static final int COL_TABLE_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_POSTER_PATH = 3;
    static final int COL_RELEASE_DATE = 4;
    static final int COL_USER_RATING = 5;
    static final int COL_MOVIE_SYNOPSIS = 6;
    static final int COL_USER_FAVORITES = 7;
    static final int COL_TRAILER_PATH = 8;
    static final int COL_REVIEW_PATH = 9;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mUri = getActivity().getIntent().getData();
        if(mUri != null)
            updateDetailTable();
        return rootView;
    }

    private void updateDetailTable(){
        //Intent intent = getActivity().getIntent();
        long movieId = MovieContract.MovieDetail.getMovieIdFromUri(mUri);
        FetchDetailTask detailTask = new FetchDetailTask(getActivity());
        detailTask.execute(movieId);
    }

//    @Override
//    public void onStart(){
//        //Log.v(LOG_TAG, "onStart method is called");
//        super.onStart();
//        updateDetailTable();
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Intent intent = getActivity().getIntent();
        if (mUri == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                mUri,
                MOVIE_DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final String baseURL = "http://image.tmdb.org/t/p/w185";

        if (!data.moveToFirst()) { return; }

        TextView detailTextView = (TextView)getView().findViewById(R.id.movie_title);
        detailTextView.setText(data.getString(COL_MOVIE_TITLE));

        ImageView thumbnail = (ImageView) getView().findViewById(R.id.image_poster);
        Picasso.with(getActivity())
                        .load(baseURL + data.getString(COL_POSTER_PATH))
                        .placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.error_placeholder)
                        .into(thumbnail);

        ((TextView) getView().findViewById(R.id.release_year)).setText(data.getString(COL_TRAILER_PATH));
        ((TextView) getView().findViewById(R.id.user_rating)).setText(Double.toString(data.getDouble(COL_USER_RATING)));
        ((TextView) getView().findViewById(R.id.release_date)).setText(data.getString(COL_RELEASE_DATE));
        ((TextView) getView().findViewById(R.id.summary)).setText(data.getString(COL_MOVIE_SYNOPSIS));


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
