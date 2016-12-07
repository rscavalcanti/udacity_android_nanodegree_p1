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
public class MainActivityFragment extends Fragment {

    private ArrayList<PopularMovies> moviesArrayList;
    private PopularMoviesAdapter mPopularMoviesAdapter;
    private final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final String KEY_PARAM = "api_key";
    private final String REQUEST_METHOD = "GET";

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

        updateMovies();

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

    /**
     * Async class responsible to fetch the data
     */
    public class FetchMoviesData extends AsyncTask<String, Void, List<PopularMovies>> {
        private final String LOG_TAG = FetchMoviesData.class.getSimpleName();

        @Override
        protected List<PopularMovies> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String popularMoviesJsonStr = null;
            StringBuffer buffer = new StringBuffer();


            try {
                urlConnection = getHttpURLConnection(urlConnection, params[0]);

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                popularMoviesJsonStr = getPopularMoviesJsonStr(reader, buffer);
                if (popularMoviesJsonStr == null) {
                    return null;
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return getPopularMovies(popularMoviesJsonStr);
        }

        /**
         * Resposible to get the movies
         *
         * @param popularMoviesJsonStr
         * @return
         */
        private ArrayList<PopularMovies> getPopularMovies(String popularMoviesJsonStr) {

            try {
                moviesArrayList = getMoviesDataFromJson(popularMoviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return moviesArrayList;
        }

        @Nullable
        private String getPopularMoviesJsonStr(BufferedReader reader, StringBuffer buffer) throws IOException {
            String popularMoviesJsonStr;
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            popularMoviesJsonStr = buffer.toString();
            return popularMoviesJsonStr;
        }

        @NonNull
        private HttpURLConnection getHttpURLConnection(HttpURLConnection urlConnection, String param) throws IOException {
            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(param)
                    .appendQueryParameter(KEY_PARAM, THE_MOVIE_API_KEY).build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.connect();
            return urlConnection;
        }

        @Override
        protected void onPostExecute(List<PopularMovies> result) {
            if (result != null) {
                mPopularMoviesAdapter.clear();
                for (PopularMovies popularMovie : result) {
                    mPopularMoviesAdapter.add(popularMovie);
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isOnline()) {
            updateMovies();
        } else {
            createToastMessage(R.string.msg_internet_connection);
        }
    }

    private void createToastMessage(int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Method responsible to convert the JSON movies String into an array of PopularMovies
     */
    private ArrayList<PopularMovies> getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {

        final String RESULTS = "results";
        final String URL = "http://image.tmdb.org/t/p/w185/";
        final String POSTER_PATH = "poster_path";
        final String TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String USER_RATING = "vote_average";

        ArrayList<PopularMovies> popularMoviesList = new ArrayList<>();

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

        for (int i = 0; i < moviesArray.length(); i++) {

            String posterPath = moviesArray.getJSONObject(i).getString(POSTER_PATH);
            String title = moviesArray.getJSONObject(i).getString(TITLE);
            String overview = moviesArray.getJSONObject(i).getString(OVERVIEW);
            String releaseDate = moviesArray.getJSONObject(i).getString(RELEASE_DATE);
            String userRating = moviesArray.getJSONObject(i).getString(USER_RATING);

            PopularMovies popularMovies = new PopularMovies(URL + posterPath, title, overview, releaseDate, userRating);
            popularMoviesList.add(popularMovies);
        }

        return popularMoviesList;
    }

    /**
     * Method responsible to update the movies in adapter accordingly
     * to the default shared preferences
     */
    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String prefMovies = prefs.getString(getString(R.string.pref_movies_key), getString(R.string.pref_movies_default));

        FetchMoviesData fetchMoviesData = new FetchMoviesData();
        fetchMoviesData.execute(prefMovies);
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
}
