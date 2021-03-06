package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Movie;

/**
 * Manages a local database creation for movie data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 10;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        //CREATE TABLE movie_general (_id INTEGER PRIMARY KEY AUTOINCREMENT, movie_id INTEGER NOT NULL, movie_title TEXT NOT NULL, poster_path TEXT NOT NULL, user_favorites INTEGER NOT NULL,
        // UNIQUE (movie_id) ON CONFLICT IGNORE);
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieGeneral.TABLE_NAME + " (" +
                MovieContract.MovieGeneral._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieGeneral.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieGeneral.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieGeneral.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieGeneral.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieGeneral.COLUMN_USER_RATING + " REAL NOT NULL, " +
                MovieContract.MovieGeneral.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
                MovieContract.MovieGeneral.COLUMN_USER_FAVORITES + " INTEGER NOT NULL, " +
                "UNIQUE (" + MovieContract.MovieGeneral.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE);"; //don't have the same movie twice

        //CREATE TABLE movie_detail (_id INTEGER PRIMARY KEY, gen_key INTEGER NOT NULL, review_path TEXT NOT NULL, trailer_path TEXT NOT NULL, FOREIGN KEY (gen_key) REFERENCES
        //moviegeneral (_id) ON DELETE CASCADE ON UPDATE CASCADE, UNIQUE (gen_key) ON CONFLICt REPLACE);
        final String SQL_CREATE_DETAIL_TABLE = "CREATE TABLE " + MovieContract.MovieDetail.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                MovieContract.MovieDetail._ID + " INTEGER PRIMARY KEY, " +

                // the ID of the location entry associated with this weather data
                MovieContract.MovieDetail.COLUMN_GENERAL_KEY + " INTEGER NOT NULL, " +
                MovieContract.MovieDetail.COLUMN_REVIEW_PATH + " TEXT, " +
                MovieContract.MovieDetail.COLUMN_TRAILER_PATH + " TEXT, " +

                //MovieContract.MovieDetail.COLUMN_REVIEW_PATH + " TEXT NOT NULL, " +
                //MovieContract.MovieDetail.COLUMN_TRAILER_PATH + " TEXT NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                "FOREIGN KEY (" + MovieContract.MovieDetail.COLUMN_GENERAL_KEY + ") REFERENCES " +
                MovieContract.MovieGeneral.TABLE_NAME + " (" + MovieContract.MovieGeneral._ID + ") ON DELETE CASCADE ON UPDATE CASCADE);";

//                "UNIQUE (" + MovieContract.MovieDetail.COLUMN_TRAILER_PATH + ") ON CONFLICT IGNORE, " + //UNIQUE (TRAILER_PATH) ON CONFLICT IGNORE, UNIQUE (REVIEW_PATH) ON CONFLICT IGNORE
//                "UNIQUE (" + MovieContract.MovieDetail.COLUMN_REVIEW_PATH + ") ON CONFLICT IGNORE);";

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                //"UNIQUE (" + MovieContract.MovieDetail.COLUMN_GENERAL_KEY + ") ON CONFLICT REPLACE);";

        //CREATE TABLE movie_trailer (_id INTEGER PRIMARY KEY, movies_id INTEGER NOT NULL, trailer_path TEXT NOT NULL, trailer_title TEXT, trailer_site TEXT, FOREIGN KEY (movies_id) REFERENCES
        //movie_general (_id) ON DELETE CASCADE ON UPDATE CASCADE);
        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieContract.MovieTrailer.TABLE_NAME + " (" +
                MovieContract.MovieTrailer._ID + " INTEGER PRIMARY KEY, " +
                MovieContract.MovieTrailer.COLUMN_MOVIES_KEY + " INTEGER NOT NULL, " +
                MovieContract.MovieTrailer.COLUMN_TRAILER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieTrailer.COLUMN_TRAILER_TITLE + " TEXT, " +
                MovieContract.MovieTrailer.COLUMN_TRAILER_SITE + " TEXT, " +
                // Set up the location column as a foreign key to location table.
                "FOREIGN KEY (" + MovieContract.MovieTrailer.COLUMN_MOVIES_KEY + ") REFERENCES " +
                MovieContract.MovieGeneral.TABLE_NAME + " (" + MovieContract.MovieGeneral._ID + ") ON DELETE CASCADE ON UPDATE CASCADE);";

        //CREATE TABLE movie_review (_id INTEGER PRIMARY KEY, movies_id INTEGER NOT NULL, review TEXT NOT NULL, author TEXT, FOREIGN KEY (movies_id) REFERENCES movie_general (_id) ON DELETE CASCADE
        //ON UPDATE CASCADE);
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.MovieReview.TABLE_NAME + " (" +
                MovieContract.MovieReview._ID + " INTEGER PRIMARY KEY, " +
                MovieContract.MovieReview.COLUMN_MOVIES_KEY + " INTEGER NOT NULL, " +
                MovieContract.MovieReview.COLUMN_REVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieReview.COLUMN_AUTHOR + " TEXT, " +
                // Set up the location column as a foreign key to location table.
                "FOREIGN KEY (" + MovieContract.MovieReview.COLUMN_MOVIES_KEY + ") REFERENCES " +
                MovieContract.MovieGeneral.TABLE_NAME + " (" + MovieContract.MovieGeneral._ID + ") ON DELETE CASCADE ON UPDATE CASCADE);";

        //CREATE TABLE location_table (_id INTEGER PRIMARY KEY,location_setting TEXT UNIQUE NOT NULL, city_name TEXT NOT NULL, coord_lat REAL NOT NULL, coord_long REAL NOT NULL );

        //CREATE TABLE weather_table (_id INTEGER PRIMARY KEY AUTHOINCREMENT,loc_key INTEGER NOT NULL, date INTEGER NOT NULL, degrees REAL NOT NULL, FOREIGN KEY (loc_key) REFERENCES
        //location_table (_id), UNIQUE (date, loc_key) ON CONFLICT REPLACE);

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_DETAIL_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieGeneral.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieDetail.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieTrailer.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieReview.TABLE_NAME);
        onCreate(db);
    }
}
