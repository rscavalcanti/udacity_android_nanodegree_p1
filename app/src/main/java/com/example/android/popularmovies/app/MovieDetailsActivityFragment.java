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
    private PopularMovies mPopularMovies;
    private ArrayAdapter<PopularMovies> mPopularMoviesAdapter;

    public MovieDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        Intent intent = getActivity().getIntent();
        final String MOVIE = "movie";

        if (intent != null){ //TODO Enhance this check

            mPopularMovies = (PopularMovies) intent.getParcelableArrayListExtra(MOVIE).get(0);
            ArrayList<PopularMovies> popularMovies = new ArrayList<>();
            popularMovies.add(mPopularMovies);

            mPopularMoviesAdapter = new PopularMoviesAdapter(getActivity(),popularMovies);
            ListView listView = (ListView) rootView.findViewById(R.id.movie_list);
            listView.setAdapter(mPopularMoviesAdapter);

            TextView title = (TextView) rootView.findViewById(R.id.textView_title);
            TextView year = (TextView) rootView.findViewById(R.id.textView_year);
            TextView userRating = (TextView) rootView.findViewById(R.id.textView_user_rating);
            TextView overview = (TextView) rootView.findViewById(R.id.textView_overview);

            title.setText(mPopularMovies.title);
            year.setText(mPopularMovies.releaseDate.substring(0,4));
            overview.setText(mPopularMovies.overview);
            userRating.setText(mPopularMovies.userRating);
        }
        return rootView;
    }
}
