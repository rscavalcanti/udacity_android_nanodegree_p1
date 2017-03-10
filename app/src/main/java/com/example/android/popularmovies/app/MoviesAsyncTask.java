package com.example.android.popularmovies.app;

/**
 * Created by EXToliveir on 12/8/2016.
 */

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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
 * Async class responsible to fetch the data
 */
public class MoviesAsyncTask extends AsyncTask<String, Void, List<Movies>> {

    private final String LOG_TAG = MoviesAsyncTask.class.getSimpleName();
    private final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final String KEY_PARAM = "api_key";
    private final String REQUEST_METHOD = "GET";

    private ArrayList<Movies> moviesArrayList;
    public OnTaskCompleted listener;

    public MoviesAsyncTask(OnTaskCompleted listener){
        this.listener = listener;
    }

    @Override
    protected List<Movies> doInBackground(String... params) {
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
    private ArrayList<Movies> getPopularMovies(String popularMoviesJsonStr) {

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
    protected void onPostExecute(List<Movies> result) {
        listener.onTaskCompleted(result);
    }

    /**
     * Method responsible to convert the JSON movies String into an array of Movies
     */
    private ArrayList<Movies> getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {

        final String ID = "id";
        final String RESULTS = "results";
        final String URL = "http://image.tmdb.org/t/p/w185/";
        final String POSTER_PATH = "poster_path";
        final String TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String USER_RATING = "vote_average";

        ArrayList<Movies> moviesList = new ArrayList<>();

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

        for (int i = 0; i < moviesArray.length(); i++) {

            int id = moviesArray.getJSONObject(i).getInt(ID);
            String posterPath = moviesArray.getJSONObject(i).getString(POSTER_PATH);
            String title = moviesArray.getJSONObject(i).getString(TITLE);
            String overview = moviesArray.getJSONObject(i).getString(OVERVIEW);
            String releaseDate = moviesArray.getJSONObject(i).getString(RELEASE_DATE);
            String userRating = moviesArray.getJSONObject(i).getString(USER_RATING);

            Movies movies = new Movies(id, URL + posterPath, title, overview, releaseDate, userRating);
            moviesList.add(movies);
        }

        return moviesList;
    }
}
