package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * Details when the user clicks on a specific movie. A lot of the formatting will be changed for p2
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String baseURL = "http://image.tmdb.org/t/p/w185";
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // The detail Activity called via intent.
        Intent receivedIntent = getActivity().getIntent(); //get the intent which has started your activity
        if (receivedIntent != null && receivedIntent.hasExtra(Intent.EXTRA_TEXT)){
            Movies movie = receivedIntent.getParcelableExtra(Intent.EXTRA_TEXT);
            ((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.getMovieName());
            ImageView thumbnail = (ImageView) rootView.findViewById(R.id.image_poster);
            if(thumbnail != null){
                Picasso.with(getActivity())
                        .load(baseURL + movie.getImageUrl())
                        .placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.error_placeholder)
                        .into(thumbnail);
            }
            ((TextView) rootView.findViewById(R.id.release_year)).setText(movie.getYear());
            ((TextView) rootView.findViewById(R.id.user_rating)).setText(movie.getUserRating());
            ((TextView) rootView.findViewById(R.id.release_date)).setText(movie.getReleaseDate());
            ((TextView) rootView.findViewById(R.id.summary)).setText(movie.getMovieSummary());
        }
        return rootView;
    }

}
