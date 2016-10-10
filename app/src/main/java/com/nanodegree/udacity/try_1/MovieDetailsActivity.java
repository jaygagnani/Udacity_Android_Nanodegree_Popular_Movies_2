package com.nanodegree.udacity.try_1;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nanodegree.udacity.try_1.Controller.MovieDataSQLite;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailsActivity extends AppCompatActivity {

    private final String LOG_TAG = getClass().getSimpleName();
    private String movie_id;
    FloatingActionButton fab;
    String youtubeRootLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        String movie_id = new Intent().getStringExtra("movie_id");
        movie_id = getIntent().getStringExtra("movie_id");
        Log.d(LOG_TAG, movie_id);

        fab = (FloatingActionButton) findViewById(R.id.fav_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setImageResource(R.drawable.btn_star_big_on_pressed);
                MovieDataSQLite movieDataSQLite = new MovieDataSQLite(getApplicationContext());
                movieDataSQLite.addMovieToFavourites(movie_id);
                Snackbar.make(view, "Movie Added to Favourites.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        youtubeRootLink = "https://www.youtube.com/results?search_query=";

        displayDetails();
    }

    public void displayDetails(){
        MovieDataSQLite db = new MovieDataSQLite(getApplicationContext());
        JSONObject movieDetailsJson = null;

        try {
            movieDetailsJson = db.fetchMovieDetails(movie_id);
            Log.d(LOG_TAG, movieDetailsJson.getString("name"));

            final String movie_name = movieDetailsJson.getString("name");

            this.setTitle(movie_name);

            String backdrop_path_local = movieDetailsJson.getString("poster_path_local");

            ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setBackground(Drawable.createFromPath(backdrop_path_local + "/" + movie_id + ".png"));

            ((TextView) findViewById(R.id.overview_tv)).setText(movieDetailsJson.getString("overview"));
            ((TextView) findViewById(R.id.release_date_tv)).setText(movieDetailsJson.getString("release_date"));
            ((TextView) findViewById(R.id.lang_tv)).setText(movieDetailsJson.getString("lang"));

            if(movieDetailsJson.getString("adult_bool").equalsIgnoreCase("true"))
                ((TextView) findViewById(R.id.adult_tv)).setText("Yes");
            else
                ((TextView) findViewById(R.id.adult_tv)).setText("No");

            ((TextView) findViewById(R.id.popularity_tv)).setText(movieDetailsJson.getString("popularity"));
            ((TextView) findViewById(R.id.vote_count_tv)).setText(movieDetailsJson.getString("vote_count"));
            ((TextView) findViewById(R.id.vote_avg_tv)).setText(movieDetailsJson.getString("vote_average"));

            int favourite = movieDetailsJson.getInt("favourite");
            if(favourite == 0)
                fab.setImageResource(R.drawable.btn_star_big_off);
            else
                fab.setImageResource(R.drawable.btn_star_big_on_pressed);

            ((Button) findViewById(R.id.view_trailer_btn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeRootLink + movie_name));
                    startActivity(intent);
                }
            });

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
