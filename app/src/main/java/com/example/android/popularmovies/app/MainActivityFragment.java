package com.example.android.popularmovies.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnTaskCompleted {

    private ArrayList<Movies> moviesArrayList;
    private MoviesAdapter mMoviesAdapter;
    private MoviesAsyncTask moviesAsyncTask;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("movies")) {
            moviesArrayList = savedInstanceState.getParcelableArrayList("movies");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", moviesArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMoviesAdapter = new MoviesAdapter(getContext(), new ArrayList<Movies>());
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(mMoviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movies movies = mMoviesAdapter.getItem(position);
                ArrayList popularMoviesArray = new ArrayList<>();
                popularMoviesArray.add(movies);
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class)
                        .putParcelableArrayListExtra("movie", popularMoviesArray);

                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String prefMovies = prefs.getString(getString(R.string.pref_movies_key), getString(R.string.pref_movies_default));
        moviesAsyncTask = new MoviesAsyncTask(new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(List<Movies> result) {
                if (result != null) {
                    mMoviesAdapter.clear();
                    for (Movies popularMovie : result) {
                        mMoviesAdapter.add(popularMovie);
                    }
                }else{
                    createToastMessage(R.string.msg_something_went_wrong);
                }
            }
        });
        if (isOnline()) {
            moviesAsyncTask.execute(prefMovies);
        } else {
            createToastMessage(R.string.msg_internet_connection);
        }
    }

    private void createToastMessage(int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Check if there is a network connection
     *
     * @return
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onTaskCompleted(List<Movies> result) {}
}
