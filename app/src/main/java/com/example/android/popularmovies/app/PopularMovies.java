package com.example.android.popularmovies.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by EXToliveir on 23/11/2016.
 */

public class PopularMovies implements Parcelable{

    String url;
    String title;
    String overview;
    String releaseDate;
    String userRating;

    public PopularMovies(String url, String title, String overview, String releaseDate, String userRating){
        this.url = url;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.userRating = userRating;
    }

    private PopularMovies(Parcel in) {
        url = in.readString();
        title = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        userRating = in.readString();
    }

    public static final Creator<PopularMovies> CREATOR = new Creator<PopularMovies>() {
        @Override
        public PopularMovies createFromParcel(Parcel in) {
            return new PopularMovies(in);
        }

        @Override
        public PopularMovies[] newArray(int size) {
            return new PopularMovies[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(userRating);
    }
}
