package com.nanodegree.udacity.try_1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nanodegree.udacity.try_1.Controller.MovieDataSQLite;
import com.nanodegree.udacity.try_1.model.ImageProcessing;
import com.nanodegree.udacity.try_1.model.MovieSortPreference;

import org.json.JSONException;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by jay on 06/10/2016.
 */

public class MovieListGridAdapter extends BaseAdapter {

    private String LOG_TAG = getClass().getSimpleName();
    private Context context;
    HashMap<String, String> moviesListHashMap;
    Collection<String> key;
    Collection<String> value;
    String dirPath, imgName;
    ImageProcessing imageProcessing;

    public MovieListGridAdapter(Context context, MovieSortPreference sortPreference) throws JSONException {
        this.context = context;
        moviesListHashMap = new MovieDataSQLite(this.context).fetchAllMoviesList(sortPreference);
        key = moviesListHashMap.keySet();
        value = moviesListHashMap.values();

        imageProcessing = new ImageProcessing(context);
    }

    @Override
    public int getCount() {
        return moviesListHashMap.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView poster;

        if(view == null){
            //if its not recycled, initialize some attributes
            poster = new ImageView(context);
//            poster.setLayoutParams(new GridView.LayoutParams(520,885));
            poster.setScaleType(ImageView.ScaleType.FIT_XY);
//            poster.setPadding(0,10,0,10);
        }else{
            poster = (ImageView) view;
        }

        dirPath = String.valueOf(value.toArray()[position]);
        imgName = String.valueOf(key.toArray()[position]);

        poster.setImageBitmap(imageProcessing.fetchPosterFromInternalStorage(dirPath, imgName, "png"));
        poster.setTag(imgName);

        return poster;
    }
}