package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by student on 2/8/16.
 */
public class TrailerAdapter extends CursorAdapter {
    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_trailer, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String trailerPath = cursor.getString(DetailFragment.COL_TRAILER_PATH);

        TextView trailerText = (TextView) view.findViewById(R.id.trailer_list);
        trailerText.setText("Trailer " + (cursor.getPosition() + 1) + ": " + trailerPath);
    }
}
