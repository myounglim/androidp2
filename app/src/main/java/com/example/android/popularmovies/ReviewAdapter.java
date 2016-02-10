package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by student on 2/9/16.
 */
public class ReviewAdapter extends CursorAdapter {
    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView authorView = (TextView) view.findViewById(R.id.review_author);
        authorView.setText("Author: " + cursor.getString(DetailFragment.COL_REVIEW_AUTHOR));

        TextView reviewView = (TextView) view.findViewById(R.id.review_item);
        reviewView.setText(cursor.getString(DetailFragment.COL_MOVIE_REVIEW));
    }
}
