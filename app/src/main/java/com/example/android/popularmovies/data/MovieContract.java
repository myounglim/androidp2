package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movie database.
 */
public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_MOVIE_GENERAL = "movie_general";
    public static final String PATH_MOVIE_DETAIL = "movie_detail";
    public static final String PATH_MOVIE_TRAILER = "movie_trailer";
    public static final String PATH_MOVIE_REVIEW = "movie_review";

    /* Inner class that defines the table contents of the movie table */
    public static final class MovieGeneral implements BaseColumns{

        //content://com.example.android.popularmovies.app/movie_general
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_GENERAL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_GENERAL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_GENERAL;

        public static final String PATH_FAVORITES = "favorites";
        public static final String PATH_RATINGS = "ratings";

        public static final String MOVIE_FAVORITE_ARG = "1";

        // Table name
        public static final String TABLE_NAME = "movie_general";

        //Long - the ID used by MOVIEDB API to identify each movie
        public static final String COLUMN_MOVIE_ID = "movie_id";

        //String
        public static final String COLUMN_MOVIE_TITLE = "title";

        //String
        public static final String COLUMN_POSTER_PATH = "poster_path";

        //String
        public static final String COLUMN_RELEASE_DATE = "release_date";

        //Float
        public static final String COLUMN_USER_RATING = "user_rating";

        //String
        public static final String COLUMN_MOVIE_SYNOPSIS = "synopsis";

        //Integer(0 for not favorite, 1 for favorited)
        public static final String COLUMN_USER_FAVORITES = "favorite";

        // for building URIs on insertion
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFavoritesUri(){
            return CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
        }

        public static Uri buildRatingsUri(){
            return CONTENT_URI.buildUpon().appendPath(PATH_RATINGS).build();
        }

    }//end of MovieGeneral class

    public static final class MovieDetail implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_DETAIL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAIL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAIL;

        public static final String TABLE_NAME = "movie_detail";

        //the foreign key that links to the general table
        public static final String COLUMN_GENERAL_KEY = "general_id";

        //String
        public static final String COLUMN_TRAILER_PATH = "trailer";
        //String
        public static final String COLUMN_REVIEW_PATH = "review";

        // for building URIs on insertion
        public static Uri buildDetailUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildDetailWithId(long id){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }

        public static long getMovieIdFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    } //end of MovieDetail class

    public static final class MovieTrailer implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_TRAILER;

        public static final String TABLE_NAME = "movie_trailer";

        //the foreign key that links to the movie general table
        public static final String COLUMN_MOVIES_KEY = "movies_id";

        //String - the path for the trailer
        public static final String COLUMN_TRAILER_PATH = "trailer_path";

        //String - the site the trailer is displayed
        public static final String COLUMN_TRAILER_SITE = "site";

        //String - the title of the trailer
        public static final String COLUMN_TRAILER_TITLE = "trailer_title";

        // for building URIs on insertion
        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class MovieReview implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEW;

        public static final String TABLE_NAME = "movie_review";

        //the foreign key that links to the movie general table
        public static final String COLUMN_MOVIES_KEY = "movies_id";

        //String - the review
        public static final String COLUMN_REVIEW = "review";

        //String - the author of the review
        public static final String COLUMN_AUTHOR = "review_author";

        // for building URIs on insertion
        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }



}
