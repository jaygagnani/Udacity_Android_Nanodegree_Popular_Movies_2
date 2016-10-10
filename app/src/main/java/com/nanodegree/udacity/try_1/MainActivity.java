package com.nanodegree.udacity.try_1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.nanodegree.udacity.try_1.Controller.GetMovieList;
import com.nanodegree.udacity.try_1.Controller.MovieDetailFragment;
import com.nanodegree.udacity.try_1.adapter.MovieListGridAdapter;
import com.nanodegree.udacity.try_1.model.MovieSortPreference;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean mTwoPane;
    public static Context context;
    MovieSortPreference movieSortPreference;
    FloatingActionButton popular_sort_fab, top_rated_sort_fab, favourite_sort_fab;

    GridView movieListGrid;
    MovieListGridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        movieSortPreference = new MovieSortPreference(context);

        Log.d(LOG_TAG, movieSortPreference.readPrefence());

        movieListGrid = (GridView) findViewById(R.id.movie_list_grid);
        assert movieListGrid != null;

//        if(checkIfInternetConnected()){
//            fetchMovieList(movieSortPreference);
//        }
//        else {
//            fetchMovieListFromDB(movieSortPreference);
//        }

        fetchMovieList(movieSortPreference);

        fetchMovieListFromDB(movieSortPreference);


        FloatingActionMenu sort_fab_menu = (FloatingActionMenu) findViewById(R.id.sort_fab_menu);
        popular_sort_fab = (FloatingActionButton) sort_fab_menu.findViewById(R.id.popular_sort_fab);
        top_rated_sort_fab = (FloatingActionButton) sort_fab_menu.findViewById(R.id.top_rated_sort_fab);
        favourite_sort_fab = (FloatingActionButton) sort_fab_menu.findViewById(R.id.favourite_sort_fab);

        popular_sort_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movieSortPreference.editPreference("popular");
                sortMovieList(movieSortPreference);
            }
        });

        top_rated_sort_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movieSortPreference.editPreference("top_rated");
                sortMovieList(movieSortPreference);
            }
        });

        favourite_sort_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movieSortPreference.editPreference("favourite");
                sortMovieList(movieSortPreference);
                Log.d(LOG_TAG, movieSortPreference.readPrefence());
            }
        });

        movieListGrid.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(findViewById(R.id.movie_details_frame) != null){
                    mTwoPane = true;

                    Bundle arguments = new Bundle();
                    arguments.putString("movie_id", (String) view.getTag());
                    MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
                    movieDetailFragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction().replace(R.id.movie_details_frame, movieDetailFragment).commit();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), MovieDetailsActivity.class);
                    intent.putExtra("movie_id", (String) view.getTag());
                    startActivity(intent);
                }

            }
        });

    }

    public static boolean checkIfInternetConnected(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnected();
    }

    public void fetchMovieList(MovieSortPreference sortPreference){
        if(checkIfInternetConnected()){
            new GetMovieList(context).execute(sortPreference.readPrefence());
        }
    }

    public void sortMovieList(MovieSortPreference sortPreference){
        if(!(sortPreference.readPrefence()).equalsIgnoreCase("favourite")) {
            if (checkIfInternetConnected()) {
                new GetMovieList(context).execute(sortPreference.readPrefence());
            }
        }

        fetchMovieListFromDB(sortPreference);
    }

    public void fetchMovieListFromDB(MovieSortPreference sortPreference){
        try {
            gridAdapter = new MovieListGridAdapter(context, sortPreference);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        movieListGrid.setAdapter(gridAdapter);
    }
}
