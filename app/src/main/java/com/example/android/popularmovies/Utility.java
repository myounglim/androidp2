package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;

/**
 * Utility class
 */
public class Utility {
    public static int sortByRatings(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortBy = prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_popular));
        if(sortBy.equals(context.getString(R.string.pref_sort_toprated)))
            return MovieFragment.SORT_BY_TOP_RATED;
        else if(sortBy.equals(context.getString(R.string.pref_sort_favorites)))
            return MovieFragment.SORT_BY_FAVORITES;
        else
            return MovieFragment.SORT_BY_MOST_POPULAR;
    }
}
