package com.example.android.popularmovies.app;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.android.popularmovies.app.BuildConfig.THE_MOVIE_API_KEY;

public class MovieDetailsActivityFragment extends Fragment {
    private Movies mMovies;
    private List<Videos> videosList;
    private ArrayAdapter<Movies> mPopularMoviesAdapter;
    private ArrayAdapter<Videos> mVideosAdapter;
    private VideosAsyncTask videosAsyncTask;

    @BindView(R.id.textView_title) TextView title;
    @BindView(R.id.textView_year) TextView year;
    @BindView(R.id.textView_user_rating) TextView userRating;
    @BindView(R.id.textView_overview) TextView overview;

    private Unbinder unbinder;

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

            mVideosAdapter = new VideosAdapter(getActivity(), new ArrayList<Videos>());
            ListView listView1 = (ListView) rootView.findViewById(R.id.video_list);
            listView1.setAdapter(mVideosAdapter);

            unbinder = ButterKnife.bind(this, rootView);

            title.setText(mMovies.title);
            year.setText(mMovies.releaseDate.substring(0,4));
            overview.setText(mMovies.overview);
            userRating.setText(mMovies.userRating);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        videosAsyncTask = new VideosAsyncTask();
        videosAsyncTask.execute(Integer.toString(mMovies.id));

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mVideosAdapter.addAll(videosList);
    }

    public class VideosAsyncTask extends AsyncTask<String, Void, List<Videos>>{

        private final String LOG_TAG = VideosAsyncTask.class.getSimpleName();
        private final String VIDEO_BASE_URL = "http://api.themoviedb.org/3/movie/";
        private final String VIDEOS = "videos";
        private final String KEY_PARAM = "api_key";
        private final String REQUEST_METHOD = "GET";

        private ArrayList<Videos> videosArrayList;

        @Override
        protected List<Videos> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String videosJsonStr = null;
            StringBuffer buffer = new StringBuffer();

            try {
                urlConnection = getHttpURLConnection(urlConnection, params[0]);
                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                videosJsonStr = getVideosJsonStr(reader, buffer);

                if (videosJsonStr == null){
                    return null;
                }

                videosList = getVideosTrailers(videosJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return videosList;
        }

        private ArrayList<Videos> getVideosTrailers(String videosJsonStr) {
            try {
                videosArrayList = getVideosDataFromJson(videosJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return videosArrayList;
        }

        private ArrayList<Videos> getVideosDataFromJson(String videosJsonStr) throws JSONException {
            final String RESULTS = "results";
            final String ID = "id";
            final String ISO_639_1 = "iso_639_1";
            final String ISO_3166_1 = "iso_3166_1";
            final String KEY = "key";
            final String NAME = "name";
            final String SITE = "site";
            final String SIZE = "size";
            final String TYPE = "type";

            ArrayList<Videos> videosList = new ArrayList<>();

            JSONObject videosJson = new JSONObject(videosJsonStr);
            JSONArray videosArray = videosJson.getJSONArray(RESULTS);

            for (int i = 0; i < videosArray.length(); i++){
                String id = videosArray.getJSONObject(i).getString(ID);
                String iso6391 = videosArray.getJSONObject(i).getString(ISO_639_1);
                String iso31661 = videosArray.getJSONObject(i).getString(ISO_3166_1);
                String key = videosArray.getJSONObject(i).getString(KEY);
                String name = videosArray.getJSONObject(i).getString(KEY);
                String site = videosArray.getJSONObject(i).getString(KEY);
                int size = Integer.parseInt(videosArray.getJSONObject(i).getString(SIZE));
                String type = videosArray.getJSONObject(i).getString(SIZE);

                Videos videos = new Videos(id, iso6391, iso31661, key, name, site, size, type);
                videosList.add(videos);
            }
            return videosList;
        }

        private String getVideosJsonStr(BufferedReader reader, StringBuffer buffer) throws IOException {
            String videosJsonStr;
            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0){
                return null;
            }

            videosJsonStr = buffer.toString();
            return videosJsonStr;
        }

        @NonNull
        private HttpURLConnection getHttpURLConnection(HttpURLConnection urlConnection, String param) throws IOException {
            Uri builtUri = Uri.parse(VIDEO_BASE_URL).buildUpon()
                    .appendPath(param)
                    .appendPath(VIDEOS)
                    .appendQueryParameter(KEY_PARAM, THE_MOVIE_API_KEY).build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.connect();
            return urlConnection;
        }
    }
}
