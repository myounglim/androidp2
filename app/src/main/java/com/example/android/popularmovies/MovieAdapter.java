package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * MovieAdapter for use to populate a gridview of images
 */
//public class MovieAdapter extends ArrayAdapter<Movies> {
//    public MovieAdapter(Context context, List<Movies> moviesList) {
//        super(context, 0,  moviesList);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        final String baseURL = "http://image.tmdb.org/t/p/w185";    //each movie thumbnail image is located at this URL(which includes the size, w185) + imageURL read from the JSON
//        Movies movies = this.getItem(position);
//
//        if(convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
//        }
//        ImageView moviePoster = (ImageView) convertView.findViewById(R.id.griditem_movie_imageview);
//        moviePoster.setMinimumHeight(MovieFragment.deviceHeight / 2);
//        if(moviePoster != null && movies.getImageUrl() != null ){
//            Picasso.with(getContext())
//                    .load(baseURL + movies.getImageUrl())
//                    .placeholder(R.drawable.user_placeholder)
//                    .error(R.drawable.error_placeholder)
//                    .into(moviePoster);
//        }
//        return convertView;
//    }
//}

public class MovieAdapter extends CursorAdapter {
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String baseURL = "http://image.tmdb.org/t/p/w185";    //each movie thumbnail image is located at this URL(which includes the size, w185) + imageURL read from the JSON
        String imageURL = baseURL + cursor.getString(MovieFragment.COL_POSTER_PATH);
        ImageView image = (ImageView)view;
        Picasso.with(context)
                    .load(imageURL)
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.error_placeholder)
                    .into(image);
    }
}

