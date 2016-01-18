package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.util.Vector;

/**
 * FetchDetailTask
 */
public class FetchDetailTask extends AsyncTask<Long, Void, Void> {
    private final String LOG_TAG = FetchDetailTask.class.getSimpleName();

    private final Context mContext;
    private long mId;

    public FetchDetailTask(Context context){
        mContext = context;
    }
    @Override
    protected Void doInBackground(Long... params) {
        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        long id = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String videoJsonStr = null;

        String myAPI_KEY = "e424af02c2bdff3412a3bcee47e71395";

        //sample api call
        //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[]

        //https://api.themoviedb.org/3/movie/[ID]/videos?api_key=[YOUR_API_KEY]
        //https://api.themoviedb.org/3/movie/[ID]/reviews?api_key=[YOUR_API_KEY]
        //image poster path sample
        //http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
        try {
            final String MOVIEDB_BASE_URL =
                    "https://api.themoviedb.org/3";
            final String moviePath = "movie";
            final String videoPath = "videos";
            final String sort_param = "sort_by";
            final String api_param = "api_key";

            Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                    .appendPath(moviePath)
                    .appendPath(Long.toString(id))
                    .appendPath(videoPath)
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
            videoJsonStr = buffer.toString();

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
            getMovieDataFromJson(videoJsonStr, id);
        } catch (JSONException | IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getMovieDataFromJson(String movieJsonStr, long movieId)
            throws JSONException, IOException {

        // Names of the JSON objects that need to be extracted.
        final String MOVIE_RESULTS = "results";
        final String TRAILER_TITLE = "name";
        final String SITE_NAME = "site";
        final String TRAILER_PATH = "key";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray resultsArray = movieJson.getJSONArray(MOVIE_RESULTS);

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieGeneral.CONTENT_URI,
                new String[]{MovieContract.MovieGeneral._ID},
                MovieContract.MovieGeneral.COLUMN_MOVIE_ID + " = ? ",
                new String[]{Long.toString(movieId)},
                null);
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(MovieContract.MovieGeneral._ID);
            long _id = cursor.getLong(index);
            for (int i = 0; i < resultsArray.length(); i++) {
                // Get the JSON object for each movie
                JSONObject movieObject = resultsArray.getJSONObject(i);

                String title = movieObject.getString(TRAILER_TITLE);
                String siteName = movieObject.getString(SITE_NAME);
                String trailerPath = movieObject.getString(TRAILER_PATH);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieDetail.COLUMN_GENERAL_KEY, _id);
                movieValues.put(MovieContract.MovieDetail.COLUMN_TRAILER_PATH, trailerPath);
                movieValues.put(MovieContract.MovieDetail.COLUMN_REVIEW_PATH, "abc");

                mContext.getContentResolver().insert(MovieContract.MovieDetail.buildDetailWithId(movieId),
                        movieValues);
            }
        }
        cursor.close();

    }
}
