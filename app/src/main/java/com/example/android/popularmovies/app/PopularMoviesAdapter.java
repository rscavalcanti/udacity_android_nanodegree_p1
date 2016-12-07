package com.example.android.popularmovies.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by EXToliveir on 23/11/2016.
 */

public class PopularMoviesAdapter extends ArrayAdapter<PopularMovies> {
    public PopularMoviesAdapter(Context context,  List<PopularMovies> popularMovies) {
        super(context, 0, popularMovies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PopularMovies popularMovie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movies_item, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.movie_image);
        Picasso.with(getContext()).load(popularMovie.url).into(iconView);

        return convertView;
    }
}
