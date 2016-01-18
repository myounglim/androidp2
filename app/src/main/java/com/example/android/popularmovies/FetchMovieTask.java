package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract;

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
import java.util.Vector;

/**
 * FetchMovieTask
 */
public class FetchMovieTask extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private final Context mContext;
    private MovieAdapter mAdapter;

    public FetchMovieTask(Context context, MovieAdapter movieAdapter){
        mContext = context;
        mAdapter = movieAdapter;
    }

    @Override
    protected Void doInBackground(Void... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        String myAPI_KEY = "e424af02c2bdff3412a3bcee47e71395";

        //sample api call
        //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[]

        //image poster path sample
        //http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
        try {
            final String MOVIEDB_BASE_URL =
                    "http://api.themoviedb.org/3";
            final String discoverPath = "discover";
            final String moviePath = "movie";
            final String sort_param = "sort_by";
            final String api_param = "api_key";

            Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                    .appendPath(discoverPath)
                    .appendPath(moviePath)
                    .appendQueryParameter(sort_param, "popularity.desc")
                    .appendQueryParameter(api_param, myAPI_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            //Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            // Open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();

            //Log.v(LOG_TAG, "Forecast string: " + movieJsonStr);
        }catch(IOException e){
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        }finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            getMovieDataFromJson(movieJsonStr);
        } catch (JSONException | IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    } //end of doInBackground

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getMovieDataFromJson(String movieJsonStr)
            throws JSONException, IOException {

        // Names of the JSON objects that need to be extracted.
        final String MOVIE_RESULTS = "results";
        final String MOVIE_TITLE = "title";
        final String POSTER_PATH = "poster_path";
        final String SUMMARY = "overview";
        final String USER_RATING = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String MOVIE_ID = "id";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray resultsArray = movieJson.getJSONArray(MOVIE_RESULTS);


        Movies[] movieResults = new Movies[resultsArray.length()];

        // Insert the new weather information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(resultsArray.length());


        for(int i = 0; i < resultsArray.length(); i++) {
            // Get the JSON object for each movie
            JSONObject movieObject = resultsArray.getJSONObject(i);

            String imageURL = movieObject.getString(POSTER_PATH);
            String title = movieObject.getString(MOVIE_TITLE);
            String summary = movieObject.getString(SUMMARY);
            double userRating = movieObject.getDouble(USER_RATING);
            String releaseDate = movieObject.getString(RELEASE_DATE);
            long movieID = movieObject.getLong(MOVIE_ID);

            //Log.v(LOG_TAG, "IMAGE URL : " + imageURL);
            //Log.v(LOG_TAG, "MOVIE NAME: " + name);
            //Log.v(LOG_TAG, "Synopsis : " + summary);
            //Log.v(LOG_TAG, "User Rating: " + userRating);
            //Log.v(LOG_TAG, "Release Date : " + releaseDate);

            final int NOT_FAVORITE = 0;

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieGeneral.COLUMN_MOVIE_ID, movieID);
            movieValues.put(MovieContract.MovieGeneral.COLUMN_MOVIE_TITLE, title);
            movieValues.put(MovieContract.MovieGeneral.COLUMN_POSTER_PATH, imageURL);
            movieValues.put(MovieContract.MovieGeneral.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MovieContract.MovieGeneral.COLUMN_USER_RATING, userRating);
            movieValues.put(MovieContract.MovieGeneral.COLUMN_MOVIE_SYNOPSIS, summary);
            movieValues.put(MovieContract.MovieGeneral.COLUMN_USER_FAVORITES,NOT_FAVORITE);

            cVVector.add(movieValues);

            //movieResults[i] = new Movies(imageURL, name, releaseDate, userRating, summary, movieID);
        }

        // add to database
        if ( cVVector.size() > 0 ) {
            // Student: call bulkInsert to add the weatherEntries to the database here
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MovieContract.MovieGeneral.CONTENT_URI, cvArray);
        }

    } //end of getMovieDataFromJson

    /*
            Students: This code will allow the FetchWeatherTask to continue to return the strings that
            the UX expects so that we can continue to test the application even once we begin using
            the database.
         */
    Movies convertContentValuesToMovieFormat(ContentValues movieValue) {
        long movieId =  movieValue.getAsLong(MovieContract.MovieGeneral.COLUMN_MOVIE_ID);
        String title = movieValue.getAsString(MovieContract.MovieGeneral.COLUMN_MOVIE_TITLE);
        String image = movieValue.getAsString(MovieContract.MovieGeneral.COLUMN_POSTER_PATH);
        String releaseDate = movieValue.getAsString(MovieContract.MovieGeneral.COLUMN_RELEASE_DATE);
        String userRating = movieValue.getAsString(MovieContract.MovieGeneral.COLUMN_USER_RATING);
        String summary =  movieValue.getAsString(MovieContract.MovieGeneral.COLUMN_MOVIE_SYNOPSIS);
        int favorite =  movieValue.getAsInteger(MovieContract.MovieGeneral.COLUMN_USER_FAVORITES);

        //public Movies(String img, String name, String releaseDate, String userRating, String summary, long movie_id)
        Movies contentMovie = new Movies(image, title, releaseDate, userRating, summary, movieId);
        return contentMovie;

        //}
    }

//    @Override
//    protected void onPostExecute(Movies[] results) {
//        if(results != null) {
//            mAdapter.clear();
//            for (Movies movies : results) {
//                mAdapter.add(movies);
//            }
//            //movieAdapter.addAll(results);
//        }
//    } //end of onPostExecute

} //end of class FetchMovieTask
