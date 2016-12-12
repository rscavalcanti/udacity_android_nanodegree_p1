package com.example.android.popularmovies.app;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieDetailsActivityFragment extends Fragment {
    private Movies mMovies;
    private ArrayAdapter<Movies> mPopularMoviesAdapter;

    public MovieDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        Intent intent = getActivity().getIntent();
        final String MOVIE = "movie";

        if (intent != null){ //TODO Enhance this check

            mMovies = (Movies) intent.getParcelableArrayListExtra(MOVIE).get(0);
            ArrayList<Movies> movies = new ArrayList<>();
            movies.add(mMovies);

            mPopularMoviesAdapter = new MoviesAdapter(getActivity(), movies);
            ListView listView = (ListView) rootView.findViewById(R.id.movie_list);
            listView.setAdapter(mPopularMoviesAdapter);

            TextView title = (TextView) rootView.findViewById(R.id.textView_title);
            TextView year = (TextView) rootView.findViewById(R.id.textView_year);
            TextView userRating = (TextView) rootView.findViewById(R.id.textView_user_rating);
            TextView overview = (TextView) rootView.findViewById(R.id.textView_overview);

            title.setText(mMovies.title);
            year.setText(mMovies.releaseDate.substring(0,4));
            overview.setText(mMovies.overview);
            userRating.setText(mMovies.userRating);
        }
        return rootView;
    }
}
