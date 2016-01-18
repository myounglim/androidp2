package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Movie Content provider
 */
public class MovieProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mDbHelper;

    private static final SQLiteQueryBuilder sQueryBuilder;

    static final int MOVIES = 100; //general query based on popularity
    static final int MOVIES_FAVORITES = 102; //query for movies user has favorited
    //static final int MOVIES_WITH_RATINGS = 103; //query sorted by user ratings(decreasing)

    static final int MOVIE_DETAIL = 300; //query for a single movie details

    static{
        sQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        //movie_general INNER JOIN movie_detail ON movie_general._id = movie_detail.gen_key
        sQueryBuilder.setTables(
                MovieContract.MovieGeneral.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieDetail.TABLE_NAME +
                        " ON " + MovieContract.MovieGeneral.TABLE_NAME +
                        "." + MovieContract.MovieGeneral._ID +
                        " = " + MovieContract.MovieDetail.TABLE_NAME +
                        "." + MovieContract.MovieDetail.COLUMN_GENERAL_KEY);
    }

    //movie_general.movieid = ?
    private static final String sMovieIdSelection =
            MovieContract.MovieGeneral.TABLE_NAME + "." +  MovieContract.MovieGeneral.COLUMN_MOVIE_ID + " = ? ";

    //movie_general.user_favorites = ?
    private static final String sMovieFavoriteSelection =
            MovieContract.MovieGeneral.TABLE_NAME + "." + MovieContract.MovieGeneral.COLUMN_USER_FAVORITES + " = ? ";


    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE_GENERAL, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_GENERAL + "/" + MovieContract.MovieGeneral.PATH_FAVORITES, MOVIES_FAVORITES);
        //matcher.addURI(authority, MovieContract.PATH_MOVIE_GENERAL + "/" + MovieContract.MovieGeneral.PATH_RATINGS, MOVIES_WITH_RATINGS);

        matcher.addURI(authority, MovieContract.PATH_MOVIE_DETAIL + "/#", MOVIE_DETAIL);
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIES:
                return MovieContract.MovieGeneral.CONTENT_TYPE;
            case MOVIES_FAVORITES:
                return MovieContract.MovieGeneral.CONTENT_TYPE;
            //case MOVIES_WITH_RATINGS:
            //    return MovieContract.MovieGeneral.CONTENT_TYPE;
            case MOVIE_DETAIL:
                return MovieContract.MovieDetail.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case MOVIES: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        MovieContract.MovieGeneral.TABLE_NAME,    //table
                        projection,                               //columns
                        selection,                                //selection
                        selectionArgs,                            //selection args
                        null,                                     //group by
                        null,                                     //having
                        sortOrder                                 //sort order
                );
                break;
            }
            // "weather/*"
            case MOVIES_FAVORITES: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        MovieContract.MovieGeneral.TABLE_NAME,
                        projection,
                        sMovieFavoriteSelection,
                        new String[]{MovieContract.MovieGeneral.MOVIE_FAVORITE_ARG},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "weather"
//            case MOVIES_WITH_RATINGS: {
//                retCursor = mDbHelper.getReadableDatabase().query(
//                        MovieContract.MovieGeneral.TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        sortOrder       //MovieContract.MovieGeneral.COLUMN_USER_RATING + "DESC"
//                );
//                break;
//            }
            // "movie details with appended id"
            case MOVIE_DETAIL: {
                long id = MovieContract.MovieDetail.getMovieIdFromUri(uri);

                retCursor =  sQueryBuilder.query(mDbHelper.getReadableDatabase(),
                        projection,
                        sMovieIdSelection,
                        new String[]{Long.toString(id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri); //Register to watch a content URI for changes
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(MovieContract.MovieGeneral.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieGeneral.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE_DETAIL: {
                long _id = db.insert(MovieContract.MovieDetail.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieDetail.buildDetailUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null); //notify registered observers
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(MovieContract.MovieGeneral.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_DETAIL:
                rowsDeleted = db.delete(MovieContract.MovieDetail.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                //normalizeDate(values);
                rowsUpdated = db.update(MovieContract.MovieGeneral.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MOVIE_DETAIL:
                rowsUpdated = db.update(MovieContract.MovieDetail.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        //normalizeDate(value);
                        long _id = db.insert(MovieContract.MovieGeneral.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
