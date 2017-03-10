package com.example.android.popularmovies.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by caval on 03/03/2017.
 */

public class Videos implements Parcelable {

    String id;
    String iso6391;
    String iso31661;
    String key;
    String name;
    String site;
    int size;
    String type;

    public Videos(String id, String iso6391, String iso31661, String key, String name, String site, int size, String type) {
        this.id = id;
        this.iso6391 = iso6391;
        this.iso31661 = iso31661;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    private Videos(Parcel in) {
        id = in.readString();
        iso31661 = in.readString();
        iso6391 = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readInt();
        type = in.readString();
    }

    public static final Creator<Videos> CREATOR = new Creator<Videos>() {
        @Override
        public Videos createFromParcel(Parcel in) { return new Videos(in); }

        @Override
        public Videos[] newArray(int size) { return new Videos[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(iso6391);
        dest.writeString(iso31661);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeInt(size);
        dest.writeString(type);
    }
}
