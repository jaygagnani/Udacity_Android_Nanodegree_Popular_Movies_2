package com.nanodegree.udacity.try_1.Controller;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nanodegree.udacity.try_1.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private String movie_id, youtubeRootLink;
    private FloatingActionButton fab;
    Activity activity;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movie_id = getArguments().getString("movie_id");
        youtubeRootLink = "https://www.youtube.com/results?search_query=";
        activity = this.getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_movie_detail, container, false);

        fab =  (FloatingActionButton) rootView.findViewById(R.id.fav_fab);

        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                fab.setImageResource(R.drawable.btn_star_big_on_pressed);
                MovieDataSQLite movieDataSQLite = new MovieDataSQLite(activity.getApplicationContext());
                movieDataSQLite.addMovieToFavourites(movie_id);
                Snackbar.make(view, "Movie Added to Favourites.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        displayDetails(rootView);

        return rootView;
    }

    public void displayDetails(View rootView){
        MovieDataSQLite db = new MovieDataSQLite(activity.getApplicationContext());
        JSONObject movieDetailsJson = null;

        try {
            movieDetailsJson = db.fetchMovieDetails(movie_id);
            Log.d(LOG_TAG, movieDetailsJson.getString("name"));

            final String movie_name = movieDetailsJson.getString("name");

//            this.setTitle(movieDetailsJson.getString("name"));
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setTitle(movie_name);

            String backdrop_path_local = movieDetailsJson.getString("poster_path_local");

            collapsingToolbarLayout.setBackground(Drawable.createFromPath(backdrop_path_local + "/" + movie_id + ".png"));

            ((TextView) rootView.findViewById(R.id.overview_tv)).setText(movieDetailsJson.getString("overview"));
            ((TextView) rootView.findViewById(R.id.release_date_tv)).setText(movieDetailsJson.getString("release_date"));
            ((TextView) rootView.findViewById(R.id.lang_tv)).setText(movieDetailsJson.getString("lang"));

            if(movieDetailsJson.getString("adult_bool").equalsIgnoreCase("true"))
                ((TextView) rootView.findViewById(R.id.adult_tv)).setText("Yes");
            else
                ((TextView) rootView.findViewById(R.id.adult_tv)).setText("No");

            ((TextView) rootView.findViewById(R.id.popularity_tv)).setText(movieDetailsJson.getString("popularity"));
            ((TextView) rootView.findViewById(R.id.vote_count_tv)).setText(movieDetailsJson.getString("vote_count"));
            ((TextView) rootView.findViewById(R.id.vote_avg_tv)).setText(movieDetailsJson.getString("vote_average"));

            int favourite = movieDetailsJson.getInt("favourite");
            if(favourite == 0)
                fab.setImageResource(R.drawable.btn_star_big_off);
            else
                fab.setImageResource(R.drawable.btn_star_big_on_pressed);

            ((Button) rootView.findViewById(R.id.view_trailer_btn)).setOnClickListener(new View.OnClickListener() {
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
