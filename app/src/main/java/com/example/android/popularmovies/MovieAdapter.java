package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by student on 9/1/15.
 */
public class MovieAdapter extends ArrayAdapter<Movies> {
    public MovieAdapter(Context context, List<Movies> moviesList) {
        super(context, 0,  moviesList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String baseURL = "http://image.tmdb.org/t/p/w185";
        Movies movies = this.getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
        }
        ImageView moviePoster = (ImageView) convertView.findViewById(R.id.griditem_movie_imageview);
        moviePoster.setMinimumHeight(MoviePosterFragment.deviceHeight / 2);
        if(moviePoster != null){
            Picasso.with(getContext())
                    .load(baseURL + movies.getImageUrl())
                    .into(moviePoster);
        }
        return convertView;
        //return super.getView(position, convertView, parent);
    }
}
