package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MoviePosterFragment extends Fragment {

    private MovieAdapter movieAdapter;
    public static int deviceHeight;

    public MoviePosterFragment() {
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

        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movies>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movies movie = movieAdapter.getItem(position);
                Intent detailIntent = new Intent(MoviePosterFragment.this.getActivity(), DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, movie);
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    private void updateMovieData(){
        FetchArtistData artistTask = new FetchArtistData();
        artistTask.execute();
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovieData();
    }


    public class FetchArtistData extends AsyncTask<Void, Void, Movies[]> {
        private final String LOG_TAG = FetchArtistData.class.getSimpleName();


        @Override
        protected Movies[] doInBackground(Void... params) {
            MovieJsonReader jsonReader = new MovieJsonReader(getActivity());
            return jsonReader.setUpConnection();
        } //end of doInBackground

        @Override
        protected void onPostExecute(Movies[] results) {
            if(results != null) {
                movieAdapter.clear();
                for (Movies movies : results) {
                    movieAdapter.add(movies);
                }
                //mForecastAdapter.addAll(results);
            }
        } //end of onPostExecute


    } //end of class FetchArtistData

} //end MoviePosterFragment
