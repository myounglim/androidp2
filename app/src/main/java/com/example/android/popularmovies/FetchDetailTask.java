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

        //videos and reviews appeneded for single api call
        //https://api.themoviedb.org/3/movie/[ID]?api_key=[]&append_to_response=videos,reviews

        //image poster path sample
        //http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
        try {
            final String MOVIEDB_BASE_URL =
                    "https://api.themoviedb.org/3";
            final String moviePath = "movie";
            final String videoPath = "videos";
            final String sort_param = "sort_by";
            final String api_param = "api_key";
            final String append_to_response = "append_to_response";
            final String trailerReviewParam = "videos,reviews";

//            Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
//                    .appendPath(moviePath)
//                    .appendPath(Long.toString(id))
//                    .appendPath(videoPath)
//                    .appendQueryParameter(api_param, myAPI_KEY)
//                    .build();

            Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                    .appendPath(moviePath)
                    .appendPath(Long.toString(id))
                    .appendQueryParameter(api_param, myAPI_KEY)
                    .appendQueryParameter(append_to_response, trailerReviewParam)
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

        int totalTrailerResults = 0;

        // Names of the JSON objects that need to be extracted.
//        final String MOVIE_RESULTS = "results";
//        final String TRAILER_TITLE = "name";
//        final String SITE_NAME = "site";
//        final String TRAILER_PATH = "key";

        //Names of the JSON objects that need to be extracted.
        //trailers here
        final String VIDEO_RESULTS = "videos";
        final String RESULTS_ARRAY = "results";
        final String TRAILER_NAME = "name";
        final String TRAILER_SITE = "site";
        final String TRAILER_PATH = "key";

        //reviews here
        final String REVIEW_RESULTS = "reviews";
        final String AUTHOR_NAME = "author";
        final String REVIEW_CONTENT = "content";

//        JSONObject movieJson = new JSONObject(movieJsonStr);
//        JSONArray resultsArray = movieJson.getJSONArray(MOVIE_RESULTS);

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONObject videoObject = movieJson.getJSONObject(VIDEO_RESULTS);
        JSONArray videoArray = videoObject.getJSONArray(RESULTS_ARRAY);

        totalTrailerResults = videoArray.length();

        JSONObject reviewObject = movieJson.getJSONObject(REVIEW_RESULTS);
        JSONArray reviewArray = reviewObject.getJSONArray(RESULTS_ARRAY);

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieGeneral.CONTENT_URI,
                new String[]{MovieContract.MovieGeneral._ID},
                MovieContract.MovieGeneral.COLUMN_MOVIE_ID + " = ? ",
                new String[]{Long.toString(movieId)},
                null);
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(MovieContract.MovieGeneral._ID);
            long _id = cursor.getLong(index);
//            for (int i = 0; i < resultsArray.length(); i++) {
//                // Get the JSON object for each movie
//                JSONObject movieObject = resultsArray.getJSONObject(i);
//
//                String title = movieObject.getString(TRAILER_TITLE);
//                String siteName = movieObject.getString(SITE_NAME);
//                String trailerPath = movieObject.getString(TRAILER_PATH);
//
//                ContentValues movieValues = new ContentValues();
//                movieValues.put(MovieContract.MovieDetail.COLUMN_GENERAL_KEY, _id);
//                movieValues.put(MovieContract.MovieDetail.COLUMN_TRAILER_PATH, trailerPath);
//                movieValues.put(MovieContract.MovieDetail.COLUMN_REVIEW_PATH, "abc");
//
//                mContext.getContentResolver().insert(MovieContract.MovieDetail.buildDetailWithId(movieId),
//                        movieValues);
//            }
            Cursor detailCursor = mContext.getContentResolver().query(
                    MovieContract.MovieDetail.buildDetailWithId(movieId),
                    null,
                    MovieContract.MovieDetail.COLUMN_GENERAL_KEY + " = ? ",
                    new String[]{Long.toString(_id)},
                    null);

            if(detailCursor.moveToFirst()){
                Log.v(LOG_TAG, "ALREADY EXISTS");
                detailCursor.close();
                return;
            }
            detailCursor.close();

            for(int i=0; i<videoArray.length(); i++){
                JSONObject trailerObject = videoArray.getJSONObject(i);

                String title = trailerObject.getString(TRAILER_NAME);
                String siteName = trailerObject.getString(TRAILER_SITE);
                String trailerPath = trailerObject.getString(TRAILER_PATH);

                Log.v(LOG_TAG, "Trailer title: " + title);
                Log.v(LOG_TAG, "Site name: " + siteName);
                Log.v(LOG_TAG, "Path to trailer: " + trailerPath);

                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieContract.MovieDetail.COLUMN_GENERAL_KEY, _id);
                movieValues.put(MovieContract.MovieDetail.COLUMN_TRAILER_PATH, trailerPath);
                movieValues.put(MovieContract.MovieDetail.COLUMN_REVIEW_PATH, "");

                ContentValues trailerValues = new ContentValues();
                trailerValues.put(MovieContract.MovieTrailer.COLUMN_MOVIES_KEY, _id);
                trailerValues.put(MovieContract.MovieTrailer.COLUMN_TRAILER_PATH, trailerPath);
                trailerValues.put(MovieContract.MovieTrailer.COLUMN_TRAILER_TITLE, title);
                trailerValues.put(MovieContract.MovieTrailer.COLUMN_TRAILER_SITE, siteName);

                mContext.getContentResolver().insert(MovieContract.MovieDetail.buildDetailWithId(movieId),
                        movieValues);

                Log.v(LOG_TAG, "Adding to movietrailer table");
                mContext.getContentResolver().insert(MovieContract.MovieTrailer.buildTrailerUri(movieId),
                        trailerValues);


            }
            for(int i=0; i<reviewArray.length(); i++){
                JSONObject reviewIndex = reviewArray.getJSONObject(i);

                String author = reviewIndex.getString(AUTHOR_NAME);
                String review = reviewIndex.getString(REVIEW_CONTENT);

                Log.v(LOG_TAG, "Author: " + author);
                Log.v(LOG_TAG, "Review: " + review);

                ContentValues reviewValues = new ContentValues();
                reviewValues.put(MovieContract.MovieDetail.COLUMN_GENERAL_KEY, _id);
                reviewValues.put(MovieContract.MovieDetail.COLUMN_TRAILER_PATH, "");
                reviewValues.put(MovieContract.MovieDetail.COLUMN_REVIEW_PATH, review);

                ContentValues reviewValue = new ContentValues();
                reviewValue.put(MovieContract.MovieReview.COLUMN_MOVIES_KEY, _id);
                reviewValue.put(MovieContract.MovieReview.COLUMN_REVIEW, review);
                reviewValue.put(MovieContract.MovieReview.COLUMN_AUTHOR, author);

                mContext.getContentResolver().insert(MovieContract.MovieDetail.buildDetailWithId(movieId), reviewValues);

                Log.v(LOG_TAG, "Adding to review table");
                mContext.getContentResolver().insert(MovieContract.MovieReview.buildReviewUri(movieId), reviewValue);
            }
        }
        cursor.close();

    }
}
