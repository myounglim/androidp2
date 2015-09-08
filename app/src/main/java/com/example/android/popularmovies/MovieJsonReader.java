package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Reads in popular movies from the moviedb api. I query this URL: http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[]
 * to retrieve the most popular movies. In the future may need to implement for other queries as well.
 *
 * !IMPORTANT Most of the code found here is from Udacity's Android Fundamentals for the Sunshine App. The set up is the same but with differences in
 * the api query and the json parsing.
 */
public class MovieJsonReader {
    private final String LOG_TAG = MovieJsonReader.class.getSimpleName();
    private Context movieFragmentContext;

    public MovieJsonReader(Context context){
        this.movieFragmentContext = context;
    }

    public Movies[] setUpConnection() {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        String myAPI_KEY = "";

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

            // Create the request to OpenWeatherMap, and open the connection
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
            return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException | IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;

    }//end of method setUpConnection

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private Movies[] getMovieDataFromJson(String movieJsonStr)
            throws JSONException, IOException {

        // Names of the JSON objects that need to be extracted.
        final String MOVIE_RESULTS = "results";
        final String MOVIE_TITLE = "title";
        final String POSTER_IMAGE = "poster_path";
        final String SUMMARY = "overview";
        final String USER_RATING = "vote_average";
        final String RELEASE_DATE = "release_date";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray resultsArray = movieJson.getJSONArray(MOVIE_RESULTS);


        Movies[] movieResults = new Movies[resultsArray.length()];


        for(int i = 0; i < resultsArray.length(); i++) {
            // Get the JSON object for each movie
            JSONObject movieObject = resultsArray.getJSONObject(i);

            String imageURL = movieObject.getString(POSTER_IMAGE);
            String name = movieObject.getString(MOVIE_TITLE);
            String summary = movieObject.getString(SUMMARY);
            String userRating = movieObject.getString(USER_RATING);
            String releaseDate = movieObject.getString(RELEASE_DATE);

            //Log.v(LOG_TAG, "IMAGE URL : " + imageURL);
            //Log.v(LOG_TAG, "MOVIE NAME: " + name);
            //Log.v(LOG_TAG, "Synopsis : " + summary);
            //Log.v(LOG_TAG, "User Rating: " + userRating);
            //Log.v(LOG_TAG, "Release Date : " + releaseDate);

            movieResults[i] = new Movies(imageURL, name, releaseDate, userRating, summary);
        }
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(movieFragmentContext);
        String sortBy = preferences.getString(movieFragmentContext.getString(R.string.pref_sort_key),
                movieFragmentContext.getString(R.string.pref_sort_popular));
        if(sortBy.equals(movieFragmentContext.getString(R.string.pref_sort_toprated))){
            //Log.v(LOG_TAG, "I'M SORTING NOW");
            Arrays.sort(movieResults); //sorting by highest to lowest user ratings(descending order) using the comparable interface
        }
        /*
        else
            Log.v(LOG_TAG, "I'M NOT SORTING");
        */
        return movieResults;

    } //end of getMovieDataFromJson

}
