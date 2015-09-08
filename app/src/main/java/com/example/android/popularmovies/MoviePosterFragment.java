package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import java.util.ArrayList;

/**
 * The fragment for the home screen page displaying all the movies
 */
public class MoviePosterFragment extends Fragment {

    private MovieAdapter movieAdapter; //the adapter to populate the gridview in fragment_main
    public static int deviceHeight; //the deviceheight in order to set the minimum height for each movie poster image(look at movieadapter.java)
    private final String LOG_TAG = MoviePosterFragment.class.getSimpleName(); //for logging/debugging purposes

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

        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movies movie = movieAdapter.getItem(position);
                Intent detailIntent = new Intent(MoviePosterFragment.this.getActivity(), DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, movie); //movies class uses the parcelable interface to pass objects in an intent
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
        //Log.v(LOG_TAG, "onStart method is called");
        super.onStart();
        updateMovieData();
    }


    public class FetchArtistData extends AsyncTask<Void, Void, Movies[]> {
        private final String LOG_TAG = FetchArtistData.class.getSimpleName();


        @Override
        protected Movies[] doInBackground(Void... params) {
            MovieJsonReader jsonReader = new MovieJsonReader(getActivity());
            return jsonReader.setUpConnection(); //returns an array of movies from the moviedb api (refer to Movies.java and MovieJsonReader.java)
        } //end of doInBackground

        @Override
        protected void onPostExecute(Movies[] results) {
            if(results != null) {
                movieAdapter.clear();
                for (Movies movies : results) {
                    movieAdapter.add(movies);
                }
                //movieAdapter.addAll(results);
            }
        } //end of onPostExecute


    } //end of class FetchArtistData

} //end MoviePosterFragment
