package com.nanodegree.udacity.try_1.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.nanodegree.udacity.try_1.Controller.MovieDataSQLite;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by jay on 07/10/2016.
 */

public class JSONParse {

    private final String LOG_TAG = getClass().getSimpleName();
    JSONObject rootObj;

    private Context context;

    public JSONParse(Context context, String jsonStr) throws JSONException {
        rootObj = new JSONObject(jsonStr);
        this.context = context;
    }

    public void storeDataToDB() throws JSONException, IOException {
        JSONArray results = rootObj.getJSONArray("results");

        int movie_id;
        String movie_name, poster_path, adult, overview, release_date, genre_ids, lang, backdrop_path, video, favourite;
        double popularity, vote_count, vote_avg;
        Bitmap poster_image;

        if(results.length() <= 0) {
            return;
        }

        MovieDataSQLite db = new MovieDataSQLite(context);
        ImageProcessing imageProcessing = new ImageProcessing(context);

        for(int i=0; i<results.length(); i++){
            movie_id = results.getJSONObject(i).getInt("id");
            movie_name = results.getJSONObject(i).getString("title");

            poster_path = results.getJSONObject(i).getString("poster_path");
            poster_image = Picasso.with(context).load(Uri.parse("http://image.tmdb.org/t/p/w500/" + poster_path)).get();
            poster_path = imageProcessing.savePosterToInternalStorage(poster_image, movie_id+".png");

            adult = results.getJSONObject(i).getString("adult");
            adult = adult.equalsIgnoreCase("true") ? "1" : "0";

            overview = results.getJSONObject(i).getString("overview");
            release_date = results.getJSONObject(i).getString("release_date");
            genre_ids = results.getJSONObject(i).getJSONArray("genre_ids").getString(0);
            lang = results.getJSONObject(i).getString("original_language");
            backdrop_path = results.getJSONObject(i).getString("backdrop_path");
            video = results.getJSONObject(i).getString("video");
            popularity = results.getJSONObject(i).getDouble("popularity");
            vote_count = results.getJSONObject(i).getDouble("vote_count");
            vote_avg = results.getJSONObject(i).getDouble("vote_average");

            favourite = "0";

            db.insertMovieData(movie_id, movie_name, poster_path, adult, overview, release_date, lang, popularity, vote_count, vote_avg, favourite);
        }
    }
}
