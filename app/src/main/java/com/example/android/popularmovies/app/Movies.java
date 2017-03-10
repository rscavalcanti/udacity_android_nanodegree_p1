package com.example.android.popularmovies.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by EXToliveir on 23/11/2016.
 */

public class Movies implements Parcelable{

    int id;
    String url;
    String title;
    String overview;
    String releaseDate;
    String userRating;

    public Movies(int id, String url, String title, String overview, String releaseDate, String userRating){
        this.id = id;
        this.url = url;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.userRating = userRating;
    }

    private Movies(Parcel in) {
        id = in.readInt();
        url = in.readString();
        title = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        userRating = in.readString();
    }

    public static final Creator<Movies> CREATOR = new Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(userRating);
    }
}
