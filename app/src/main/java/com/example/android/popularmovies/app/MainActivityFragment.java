package com.example.android.popularmovies.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.app.BuildConfig.THE_MOVIE_API_KEY;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnTaskCompleted {

    private ArrayList<PopularMovies> moviesArrayList;
    private PopularMoviesAdapter mPopularMoviesAdapter;
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
        mPopularMoviesAdapter = new PopularMoviesAdapter(getContext(), new ArrayList<PopularMovies>());
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(mPopularMoviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopularMovies popularMovies = mPopularMoviesAdapter.getItem(position);
                ArrayList popularMoviesArray = new ArrayList<>();
                popularMoviesArray.add(popularMovies);
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
            public void onTaskCompleted(List<PopularMovies> result) {
                if (result != null) {
                    mPopularMoviesAdapter.clear();
                    for (PopularMovies popularMovie : result) {
                        mPopularMoviesAdapter.add(popularMovie);
                    }
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
    public void onTaskCompleted(List<PopularMovies> result) {}
}
