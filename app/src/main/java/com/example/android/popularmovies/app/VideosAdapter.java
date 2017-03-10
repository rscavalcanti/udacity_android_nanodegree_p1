package com.example.android.popularmovies.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by EXToliveir on 23/11/2016.
 */

public class VideosAdapter extends ArrayAdapter<Videos> {
    private ViewHolder holder;

    public VideosAdapter(Context context, List<Videos> videos) {
        super(context, 0, videos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.videos_item, parent, false);
            holder = new ViewHolder();
            holder.iconView = (ImageView) convertView.findViewById(R.id.video_image);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }
}
